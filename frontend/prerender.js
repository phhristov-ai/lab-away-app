const fs = require('fs-extra');
const path = require('path');
const puppeteer = require('puppeteer');
const AWS = require('aws-sdk');

const s3 = new AWS.S3({
  accessKeyId: process.env.AWS_ACCESS_KEY_ID,
  secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
  region: 'eu-north-1',
});

const bucketName = 'lab-away-prerendered';
const BASE_URL = 'http://localhost:45679';
const OUTPUT_DIR = path.join(__dirname, 'build');

const PAGES = JSON.parse(fs.readFileSync(path.join(__dirname, 'prerender-routes.json'), 'utf8'));

async function uploadFileToS3(filePath, key) {
  const fileContent = fs.readFileSync(filePath);
  await s3.upload({
    Bucket: bucketName,
    Key: key,
    Body: fileContent,
    ContentType: 'text/html',
  }).promise();
  console.log(`✅ Uploaded ${key}`);
}

(async () => {
  const browser = await puppeteer.launch({
    headless: "new",
    args: ["--no-sandbox", "--disable-setuid-sandbox"]
  });

  const page = await browser.newPage();

  // Pretend to be Googlebot (helps with SEO and CSR blocking)
  await page.setUserAgent('Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)');

  // Disable JS so HTML is 100% static
  await page.setJavaScriptEnabled(false);

  for (const route of PAGES) {
    const url = `${BASE_URL}${route}`;
    console.log(`📄 Prerendering ${url}`);

    await page.goto(url, {
      waitUntil: 'networkidle2',
      timeout: 60000,
    });

    const html = await page.content();

    // Build output filepath
    const filePath =
      route === '/'
        ? path.join(OUTPUT_DIR, 'index.html')
        : path.join(OUTPUT_DIR, route, 'index.html');

    await fs.ensureDir(path.dirname(filePath));
    await fs.writeFile(filePath, html, 'utf8');

    // Prepare S3 key
    const s3Key =
      route === '/'
        ? 'index.html'
        : path.join(route, 'index.html').replace(/\\/g, '/');

    await uploadFileToS3(filePath, s3Key);
  }

  await browser.close();
  console.log('🎉 All pages prerendered and uploaded!');
})();
