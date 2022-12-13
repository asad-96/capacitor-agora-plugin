import { resolve } from 'path'
// import { Options } from '@nuxtjs/vuetify/dist/options'

export const vuetifyConfig = {
  treeShake: true,
  defaultAssets: false,
  customVariables: ['~/assets/styles/vuetify.scss'],
  optionsPath: resolve(__dirname, './vuetify.options.ts')
}
