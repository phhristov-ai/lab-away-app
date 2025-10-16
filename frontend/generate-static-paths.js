const fs = require('fs');
const path = require('path');
const { axiosInstanceServer } = require('./src/services/axiosInstance.server');

const LANGUAGES = ['en', 'de', 'bg'];

(async () => {
  try {
    const staticRoutes = [
      '/', '/shop', '/blog', '/delivery', '/privacy-policy',
      '/terms-and-conditions', '/contact', '/imprint'
    ];

    let allRoutes = [];

    for (const lang of LANGUAGES) {
      const upperLang = lang.toUpperCase();

      // 🛍️ Products
      const productRes = await axiosInstanceServer.get('/products', {
        params: { lang: upperLang },
      });
      const productSlugs = productRes.data.map(product => product.slug);
      const productRoutes = productSlugs.map(slug => `/${lang}/product/${slug}`);

      // 📝 Blogs
      const blogRes = await axiosInstanceServer.get('/blogs', {
        params: { lang: upperLang },
      });
      const blogSlugs = blogRes.data.map(blog => blog.slug);
      const blogRoutes = blogSlugs.map(slug => `/${lang}/blog/${slug}`);

      // 🧭 Static localized pages
      const localizedStaticRoutes = staticRoutes.map(route => `/${lang}${route}`);

      // ➕ Collect all for this language
      allRoutes.push(...localizedStaticRoutes, ...productRoutes, ...blogRoutes);
    }

    // Update package.json reactSnap.include
    const packagePath = path.resolve(__dirname, 'package.json');
    const packageJson = JSON.parse(fs.readFileSync(packagePath, 'utf8'));

    packageJson.reactSnap = packageJson.reactSnap || {};
    packageJson.reactSnap.include = allRoutes;

    fs.writeFileSync(packagePath, JSON.stringify(packageJson, null, 2));
    console.log(`✅ Added ${allRoutes.length} localized routes to reactSnap.include`);
  } catch (err) {
    console.error('❌ Error generating localized static paths:', err.message);
    process.exit(1);
  }
})();
