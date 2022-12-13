# PREPARE NODE_MODULES IN PRODUCTION MODE
FROM mhealthvn/node-builder:master as builder
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

FROM builder as mobile-builder
# Install OpenJDK-11
RUN apk add openjdk17-jre
ARG JAR_FILE=build/libs/*.jar
RUN java -version
ENV ANDROID_HOME /opt/android-sdk-linux
ENV SDK_TOOLS_VERSION 25.2.5
ENV API_LEVELS android-23
ENV BUILD_TOOLS_VERSIONS build-tools-25.0.2,build-tools-23.0.1
ENV ANDROID_EXTRAS extra-android-m2repository,extra-android-support,extra-google-google_play_services,extra-google-m2repository
ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/platform-tools

RUN apk update && apk add --no-cache bash unzip libstdc++

RUN mkdir -p /opt/android-sdk-linux && cd /opt \
    && wget -q http://dl.google.com/android/repository/tools_r${SDK_TOOLS_VERSION}-linux.zip -O android-sdk-tools.zip \
    && unzip -q android-sdk-tools.zip -d ${ANDROID_HOME} \
    && rm -f android-sdk-tools.zip


RUN yes | android update sdk --no-ui -a --filter \
    tools,platform-tools,${ANDROID_EXTRAS},${API_LEVELS},${BUILD_TOOLS_VERSIONS} --no-https

# FROM tabrindle/min-alpine-android-sdk:latest as android
COPY --from=builder /usr/src/app/node_modules node_modules
COPY --from=builder /usr/src/app/dist dist
COPY --from=builder /usr/src/app/android android

RUN yarn verify:android
# RUN yarn verify:ios

# step 4: release and publish
FROM runner
RUN node build.js
RUN yarn publish:package

