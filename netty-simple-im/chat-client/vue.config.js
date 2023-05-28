const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: './',
  devServer: {
    proxy: {
      '/im': {
        target: 'http://localhost:8888',
        changeOrigin: true,
        pathRewrite: {
          '^/im': '/im' // 将URL路径重写为目标路由后面的部分，包含 /im
        }
      }
    }
  }

})
