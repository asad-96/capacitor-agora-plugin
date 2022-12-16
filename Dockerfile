# PREPARE NODE_MODULES IN PRODUCTION MODE
FROM alvrme/alpine-android:android-33-jdk17 as mobile-builder
RUN apk update \ 
    && apk upgrade --available \
    && apk --no-cache --virtual add git curl python3 py3-pip make g++ gcc nodejs npm

RUN apk add --update npm 
RUN npm install -g yarn 

WORKDIR /usr/src/app

# step 1: install dependency
COPY package.json ./
RUN yarn install --silent

# step 2: build
COPY . .
COPY .env.build .env
ARG GIT_TAG
ENV GIT_TAG=${GIT_TAG}
RUN yarn build

# step 3: TESTS
RUN yarn verify:web
RUN yarn run verify:android
# RUN yarn verify:ios

# step 4: release and publish
RUN node build.js
RUN yarn publish:package

