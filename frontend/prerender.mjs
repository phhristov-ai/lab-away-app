import fs from 'fs-extra';
import path from 'node:path';
import puppeteer from 'puppeteer';
import AWS from 'aws-sdk';
import axios from 'axios';

// ----------------- AWS S3 setup -----------------
const s3 = new AWS.S3({
  accessKeyId: process.env.AWS_ACCESS_KEY_ID,
  secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
  region: 'eu-north-1',
});

const bucketName = 'lab-away-prerendered';
const BASE_URL = 'http://localhost:45679';
const OUTPUT_DIR = path.join(process.cwd(), 'build'); // safer than __dirname

// ----------------- Routes to prerender -----------------
const PAGES = JSON.parse(
  await fs.readFile(path.join(process.cwd(), 'prerender-routes.json'), 'utf8')
);

// ----------------- Upload HTML file to S3 -----------------
async function uploadFileToS3(filePath, key) {
  const fileContent = await fs.readFile(filePath);

  await s3
    .upload({
      Bucket: bucketName,
      Key: key,
      Body: fileContent,
      ContentType: 'text/html',
    })
    .promise();

  console.log(`✅ Uploaded ${key}`);
}

// ----------------- Puppeteer setup -----------------
const browser = await puppeteer.launch({
  headless: 'new',
  args: ['--no-sandbox', '--disable-setuid-sandbox'],
});

const page = await browser.newPage();

// Pretend to be Googlebot for SEO
await page.setUserAgent(
  'Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)'
);

// Enable JavaScript so React renders fully
await page.setJavaScriptEnabled(true);

// ----------------- Main prerender loop -----------------
for (const route of PAGES) {
  const url = `${BASE_URL}${route}`;
  console.log(`📄 Prerendering ${url}`);

  // Navigate and wait for DOM + React content
  await page.goto(url, { waitUntil: 'domcontentloaded', timeout: 60000 });
  await page.waitForSelector('#root > *', { timeout: 60000 });

  let html = await page.content();

  // ----------------- Inject structured data -----------------
  if (route.startsWith('/en/product/')) {
    const productSlug = route.split('/').pop();

    try {
      const productRes = await axios.get(
        `https://api.lab-away.com/api/products/${productSlug}?lang=EN`
      );
      const product = productRes.data;

      const structuredData = {
        "@context": "https://schema.org/",
        "@type": "Product",
        name: product.name,
        description: product.description,
        sku: product.slug,
        offers: {
          "@type": "Offer",
          price: product.price,
          priceCurrency: 'EUR',
        },
        url: `https://www.lab-away.com${route}`,
      };

      html = html.replace(
        '</head>',
        `<script type="application/ld+json">${JSON.stringify(structuredData)}</script></head>`
      );
    } catch (err) {
      console.error(`❌ Failed to fetch structured data for ${route}: ${err.message}`);
    }
  }

  if (route.startsWith('/en/blog/')) {
    const slug = route.split('/').pop();
    const blogData = await axios
      .get(`https://api.lab-away.com/api/blogs/${slug}`, { params: { lang: 'EN' } })
      .then(res => res.data)
      .catch(() => null);

    if (blogData) {
      const structuredData = {
        "@context": "https://schema.org/",
        "@type": "BlogPosting",
        headline: blogData.title,
        description: blogData.excerpt,
        author: blogData.author || 'Lab-Away',
        datePublished: blogData.publishedAt,
        url: `https://www.lab-away.com${route}`,
        image: blogData.image || undefined,
      };

      html = html.replace(
        '</head>',
        `<script type="application/ld+json">${JSON.stringify(structuredData)}</script></head>`
      );
    }
  }

  // ----------------- Build file paths -----------------
  const cleanRoute = route.replace(/^\/+/, '');
  const filePath =
    cleanRoute === ''
      ? path.join(OUTPUT_DIR, 'index.html')
      : path.join(OUTPUT_DIR, cleanRoute, 'index.html');

  await fs.ensureDir(path.dirname(filePath));
  await fs.writeFile(filePath, html, 'utf8');

  const s3Key = cleanRoute === '' ? 'index.html' : `${cleanRoute}/index.html`;
  await uploadFileToS3(filePath, s3Key);
}

await browser.close();
console.log('🎉 All pages prerendered and uploaded!');
