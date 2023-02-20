import fs from 'fs'
import path from 'path'
import { NuxtOptions } from '@nuxt/types'
import DotEnv from 'dotenv'
import {
  axiosConfig,
  buildConfg,
  dayjsConfig,
  headConfig,
  i18nConfig,
  socketIoConfig,
  toastConfig,
  vuetifyConfig,
  firebaseConfig
} from './src/configs/index'
DotEnv.config({ path: './.env' })

export default {
  target: 'static',
  ssr: false,
  components: true,
  env: {
    FIRE_ENV: process.env.FIRE_ENV || 'sandbox'
  },
  srcDir: process.cwd() + '/src/',
  publicRuntimeConfig: {
    accountBaseUrl: process.env.ACCOUNT_BASE_URL,
    endpoint: process.env.API_ENDPOINT,
    agora: {
      appId: process.env.AGORA_APPID || 'ba4643f2b3a145f29575b8783d3a5ec1',
      channel: process.env.AGORA_CHANNEL || 'test',
      token: process.env.AGORA_TOKEN || '',
      uid: process.env.AGORA_UID || 0
    },
    'nuxt-module-data-layer': {
      baseURL: process.env.API_ENDPOINT,
      accountBaseURL: process.env.ACCOUNT_BASE_URL,
      xTenantId: 'wellcare'
    }
  },
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
    '@wellcare/nuxt-module-data-layer',
    '@wellcare/vue-authen',
    '@nuxtjs/axios',
    'nuxt-user-agent',
    '@nuxtjs/dayjs',
    '@nuxtjs/i18n',
    '@nuxtjs/toast',
    'cookie-universal-nuxt',
    'nuxt-socket-io'
  ],
  buildModules: [
    '@nuxtjs/composition-api/module',
    '@nuxtjs/eslint-module',
    '@nuxtjs/vuetify',
    '@nuxt/typescript-build',
    '@nuxtjs/stylelint-module',
    'nuxt-typed-vuex',
    ['@nuxtjs/firebase', firebaseConfig],
    ['@wellcare/vue-component', { prefix: 'w', level: 1 }],
    ['@wellcare/nuxt-module-chat', { prefix: 'w', level: 1 }],
    ['@wellcare/nuxt-module-media', { prefix: 'w', level: 1 }],
    ['@wellcare/nuxt-module-conference', { prefix: 'w', level: 1 }]
  ],
  build: buildConfg,
  head: headConfig,
  server: {
    host: process.env.APP_HOST || '0.0.0.0',
    port: process.env.APP_PORT || '8080',
    https: {
      key: fs.readFileSync(path.resolve(__dirname, 'localhost.key')),
      cert: fs.readFileSync(path.resolve(__dirname, 'localhost.crt'))
    }
  },
  io: socketIoConfig(process.env),
  axios: axiosConfig,
  vuetify: vuetifyConfig,
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
