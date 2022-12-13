export const purgeCssConfig = {
  mode: 'postcss',
  enabled: false,
  paths: [
    '~/src/components/**/*.vue',
    '~/src/layouts/**/*.vue',
    '~/src/pages/**/*.vue',
    '~/src/pages/*.vue',
    '~/src/plugins/**/*.js'
  ],
  styleExtensions: ['.css'],
  whitelist: ['body', 'html', 'nuxt-progress'],
  extractors: [
    {
      extractor: (content) => content.match(/[A-z0-9-:\\/]+/g) || [],
      extensions: ['html', 'vue', 'js']
    }
  ]
}
