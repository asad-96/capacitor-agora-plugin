import { Options } from '@nuxtjs/i18n/types'

export const i18nConfig: Options = {
  locales: [
    { code: 'vi', iso: 'vi-VN', file: 'vi.json' },
    { code: 'en', iso: 'en-GB', file: 'en.json' }
  ],
  vueI18n: {
    numberFormats: {
      vi: {
        currency: {
          style: 'currency',
          currency: 'VND'
        }
      },
      en: {
        currency: {
          style: 'currency',
          currency: 'VND'
        }
      }
    },
    dateTimeFormats: {
      vi: {
        short: {
          month: '2-digit',
          day: '2-digit',
          year: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        }
      },
      en: {
        short: {
          month: '2-digit',
          day: '2-digit',
          year: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        }
      }
    }
  },
  defaultLocale: 'vi',
  // https://i18n.nuxtjs.org/lazy-load-translations/#lazy-configuration-options
  lazy: {
    skipNuxtState: true
  },
  strategy: 'no_prefix',
  langDir: './locales',
  detectBrowserLanguage: {
    alwaysRedirect: false,
    fallbackLocale: 'vi',
    redirectOn: 'root',
    useCookie: true,
    cookieCrossOrigin: false,
    cookieDomain: null,
    cookieKey: 'locale',
    cookieSecure: false
  }
}
