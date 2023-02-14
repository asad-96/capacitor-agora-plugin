# Prerequisites:
- install node, version 16.x
- install node package manager (`npm` or `yarn`)
- 

# Install guide
```bash
# install tools at root folder
yarn
# install example project
cd example
yarn # install dependency
yarn build:plugin # update plugin code to example project
copy .env.build .env # copy env to build
# for ios:
yarn cap:ios # generate web files and copy ios files from node_modules to xcode
# for android:
yarn cap:android # generate web files and copy android files from node_modules to android project 
```

# Development guide
When development, use can use Android Studio or XCode to to change the files directly. But remmber this only changes node_modules in local files, and will not committed to source codes. You need to copy these changes to respective `/android` or `/ios` folder.

Note:
- When web code changes, `yarn cap:ios`  or `yarn cap:android` is required to build web codes.