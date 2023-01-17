<template>
  <v-app>
    <v-main class="grey darken-4 no-scrollbar">
      <Nuxt keep-alive />
    </v-main>
  </v-app>
</template>
<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
</style>
