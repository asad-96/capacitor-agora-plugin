const fs = require('fs')
const path = require('path')

// write package.json
const packageJson = require('./package.json')
delete packageJson.devDependencies
delete packageJson.scripts
packageJson.version = process.env.GIT_TAG || packageJson.version
fs.writeFileSync(
  path.resolve(__dirname, 'dist/package.json'),
  JSON.stringify(packageJson)
)
