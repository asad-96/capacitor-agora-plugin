import { NuxtOptions } from '@nuxt/types'
import DotEnv from 'dotenv'
import {
  axiosConfig,
  buildConfg,
  dayjsConfig,
  googleFontsConfig,
  headConfig,
  i18nConfig,
  purgeCssConfig,
  pwaOption,
  toastConfig,
  vuetifyConfig
} from './src/configs/index'
DotEnv.config({ path: './.env' })

export default {
  target: 'static',
  ssr: false,
  components: true,
  publicRuntimeConfig: {
    'nuxt-module-data-layer': {
      accountBaseURL: process.env.ACCOUNT_BASE_URL,
      baseURL: process.env.API_ENDPOINT,
      xTenantId: process.env.X_TENTANT_ID
    }
  },
  srcDir: process.cwd() + '/src/',
  dir: {
    assets: 'assets',
    layouts: 'layouts',
    middleware: 'middleware',
    pages: 'pages',
    static: 'static',
    store: 'store'
  },
  plugins: [],
  css: ['~/assets/styles/app'],
  modules: [
    '@nuxtjs/axios',
    'nuxt-user-agent',
    '@nuxtjs/dayjs',
    '@nuxtjs/i18n',
    '@nuxtjs/toast',
    'cookie-universal-nuxt',
    '@nuxtjs/pwa'
    // '@nuxtjs/sentry'
  ],
  buildModules: [
    ['nuxt-purgecss', purgeCssConfig],
    '@nuxtjs/composition-api/module',
    '@nuxtjs/eslint-module',
    '@nuxtjs/vuetify',
    '@nuxtjs/google-fonts',
    '@nuxt/typescript-build',
    '@nuxtjs/stylelint-module',
    'nuxt-typed-vuex',
    ['@wellcare/vue-component', { prefix: 'w', level: 1 }],
    ['@wellcare/nuxt-module-data-layer', { prefix: 'w', level: 1 }]
  ],
  googleFonts: googleFontsConfig,
  pwa: pwaOption,
  build: buildConfg,
  head: headConfig,
  server: {
    host: process.env.APP_HOST || '0.0.0.0',
    port: process.env.APP_PORT || '8080'
  },
  axios: axiosConfig,
  vuetify: vuetifyConfig,
  sentry: {
    dsn: 'https://2e750cc40b224e22acc11a3c98512aef@o1191490.ingest.sentry.io/4504263931002880'
  },
  i18n: i18nConfig,
  toast: toastConfig,
  // https://typescript.nuxtjs.org/guide/lint.html
  typescript: {
    typeCheck: {
      eslint: {
        files: './**/*.{ts,vue}'
      }
    }
  },
  dayjs: dayjsConfig,
  // https://github.com/nuxt-community/eslint-module
  eslint: {
    fix: true
  },

  // https://github.com/nuxt-community/stylelint-module
  stylelint: {
    fix: true
  },
  generate: {
    interval: 3000
  }
} as Partial<NuxtOptions>
