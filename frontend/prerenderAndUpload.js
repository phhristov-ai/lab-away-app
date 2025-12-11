const fs = require('fs');
const path = require('path');
const AWS = require('aws-sdk');

const s3 = new AWS.S3({
  accessKeyId: process.env.AWS_ACCESS_KEY_ID,
  secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
  region: 'eu-north-1',
});

const bucketName = 'lab-away-prerendered';
const buildDir = path.resolve(__dirname, 'build');

function uploadFile(filePath, key) {
  const fileContent = fs.readFileSync(filePath);

  const params = {
    Bucket: bucketName,
    Key: key,
    Body: fileContent,
    ContentType: 'text/html',
  };

  return s3.upload(params).promise();
}

async function uploadDir(dir, prefix = '') {
  const files = fs.readdirSync(dir);
  for (const file of files) {
    const fullPath = path.join(dir, file);
    const key = path.join(prefix, file).replace(/\\/g, '/');

    if (fs.statSync(fullPath).isDirectory()) {
      await uploadDir(fullPath, key);
    } else if (file === 'index.html') {
      await uploadFile(fullPath, key);
      console.log(`Uploaded ${key}`);
    }
  }
}

(async () => {
  try {
    console.log('📦 Uploading prerendered pages to S3...');
    await uploadDir(buildDir);
    console.log('✅ All pages uploaded successfully.');
  } catch (err) {
    console.error('❌ Error uploading pages:', err);
    process.exit(1);
  }
})();
