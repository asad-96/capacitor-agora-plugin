<template>
  <v-app>
    <v-main class="grey darken-4">
      <Nuxt keep-alive />
    </v-main>
  </v-app>
</template>
<script lang="ts">
import { defineComponent } from '@nuxtjs/composition-api'

export default defineComponent({
  setup() {}
})
</script>

<style>
::-webkit-scrollbar {
  width: 0 !important;
  background: transparent !important;
  display: none !important;
}
</style>
