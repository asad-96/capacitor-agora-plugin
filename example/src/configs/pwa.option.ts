export const pwaOption = {
  // https://pwa.nuxtjs.org/meta
  meta: {
    lang: 'vi',
    nativeUI: true
  },
  // https://pwa.nuxtjs.org/manifest
  manifest: {
    theme_color: '#4299E1',
    start_url: './'
    //   useWebmanifestExtension: false
  },

  icon: {
    fileName: 'logo.png'
  },
  // https://pwa.nuxtjs.org/workbox
  workbox: {
    offline: true,
    offlineStrategy: 'networkFirst',
    offlineAnalytics: true,
    enabled: true,
    runtimeCaching: [
      {
        urlPattern: 'https://ajax.cloudflare.com/.*',
        handler: 'staleWhileRevalidate'
      },
      {
        urlPattern: 'https://fonts.gstatic.com/.*',
        handler: 'staleWhileRevalidate'
      },
      {
        urlPattern: 'https://www.google.com/.*',
        handler: 'staleWhileRevalidate'
      },
      {
        urlPattern: 'https://www.gstatic.com/.*',
        handler: 'staleWhileRevalidate'
      }
    ]
  }
}
