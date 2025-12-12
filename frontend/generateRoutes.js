const fs = require('fs');
const path = require('path');
const { axiosInstanceServer } = require('./src/services/axiosInstance.server');

const LANGUAGES = ['en', 'de'];

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

    // Save to JSON
    const routesPath = path.resolve(__dirname, 'prerender-routes.json');
    fs.writeFileSync(routesPath, JSON.stringify(allRoutes, null, 2), 'utf8');

    console.log(`✅ Generated ${allRoutes.length} routes for prerendering`);
  } catch (err) {
    console.error('❌ Error generating routes:', err.message);
    process.exit(1);
  }
})();
