// craco.config.js
const path = require('path');

module.exports = {
  webpack: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      '@components': path.resolve(__dirname, 'src/components'),
      '@utils': path.resolve(__dirname, 'src/utils'),
      '@assets': path.resolve(__dirname, 'src/assets'),
      '@hooks': path.resolve(__dirname, 'src/hooks'),
      '@pages': path.resolve(__dirname, 'src/pages'),
      '@types': path.resolve(__dirname,'src/types'),
      '@mocks': path.resolve(__dirname,'src/mocks'),
      '@services': path.resolve(__dirname,'src/services'),
      '@config': path.resolve(__dirname,'src/config'),
    }
  }
};