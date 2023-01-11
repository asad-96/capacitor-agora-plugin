const fs = require('fs')
const path = require('path')

function copy(src, dest, condition = { extension: undefined }) {
  const stat = fs.statSync(src)
  if (stat.isDirectory()) {
    const files = fs.readdirSync(src)
    files.map((file) => {
      const srcFilePath = path.join(src, file)
      const destFilePath = path.join(dest, file)
      copy(srcFilePath, destFilePath, condition)
    })
  } else if (
    !condition.extension ||
    (condition.extension && src.includes(condition.extension))
  ) {
    const dirPath = path.dirname(dest)
    if (fs.existsSync(dirPath)) {
      fs.copyFileSync(src, dest)
    } else {
      fs.mkdirSync(dirPath, { recursive: true })
      fs.copyFileSync(src, dest)
    }
  }
}

copy(path.resolve(__dirname, './dist'), path.resolve(__dirname, `./lib/dist`))
copy(
  path.resolve(__dirname, './README.md'),
  path.resolve(__dirname, `./lib/README.md`)
)
copy(
  path.resolve(__dirname, './rollup.config.js'),
  path.resolve(__dirname, `./lib/rollup.config.js`)
)
copy(
  path.resolve(__dirname, './android'),
  path.resolve(__dirname, `./lib/android`)
)
copy(path.resolve(__dirname, './ios'), path.resolve(__dirname, `./lib/ios`))
fs.copyFileSync(
  path.resolve(__dirname, './WellcareCapacitorPluginAgora.podspec'),
  path.resolve(__dirname, `./lib/WellcareCapacitorPluginAgora.podspec`)
)

// write package.json
const packageJson = require('./package.json')
delete packageJson.devDependencies
delete packageJson.scripts
packageJson.version = process.env.GIT_TAG || packageJson.version
fs.writeFileSync(
  path.resolve(__dirname, 'lib/package.json'),
  JSON.stringify(packageJson)
)
