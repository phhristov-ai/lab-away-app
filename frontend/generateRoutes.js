const fs = require('fs');
const path = require('path');
const { axiosInstanceServer } = require('./src/services/axiosInstance.server');

const LANGUAGES = ['en', 'de'];
const BASE_URL = 'https://www.lab-away.com'; // your public URL

(async () => {
  try {
    const staticRoutes = [
      '/', '/shop', '/blog', '/delivery', '/privacy-policy',
      '/terms-and-conditions', '/contact', '/imprint'
    ];

    let allRoutes = [];

    for (const lang of LANGUAGES) {
      const upperLang = lang.toUpperCase();

      // Products
      const productRes = await axiosInstanceServer.get('/products', {
        params: { lang: upperLang },
      });
      const productSlugs = productRes.data.map(product => product.slug);
      const productRoutes = productSlugs.map(slug => `/${lang}/product/${slug}`);

      // Blogs
      const blogRes = await axiosInstanceServer.get('/blogs', {
        params: { lang: upperLang },
      });
      const blogSlugs = blogRes.data.map(blog => blog.slug);
      const blogRoutes = blogSlugs.map(slug => `/${lang}/blog/${slug}`);

      // Localized static pages
      const localizedStaticRoutes = staticRoutes.map(route => `/${lang}${route}`);

      allRoutes.push(...localizedStaticRoutes, ...productRoutes, ...blogRoutes);
    }

    // Save routes JSON for prerendering
    const routesPath = path.resolve(__dirname, 'prerender-routes.json');
    fs.writeFileSync(routesPath, JSON.stringify(allRoutes, null, 2), 'utf8');
    console.log(`✅ Generated ${allRoutes.length} routes for prerendering`);

    // Generate sitemap.xml
    const sitemapContent = `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
${allRoutes.map(route => `
  <url>
    <loc>${BASE_URL}${route}</loc>
    <changefreq>weekly</changefreq>
    <priority>0.8</priority>
  </url>`).join('')}
</urlset>
`;
    const sitemapPath = path.resolve(__dirname, 'public', 'sitemap.xml');
    fs.writeFileSync(sitemapPath, sitemapContent, 'utf8');
    console.log('✅ sitemap.xml generated!');

    // Generate robots.txt
    const robotsContent = `User-agent: *
Allow: /
Sitemap: ${BASE_URL}/sitemap.xml
`;
    const robotsPath = path.resolve(__dirname, 'public', 'robots.txt');
    fs.writeFileSync(robotsPath, robotsContent, 'utf8');
    console.log('✅ robots.txt generated!');

  } catch (err) {
    console.error('❌ Error generating routes:', err);
    process.exit(1);
  }
})();
