<template>
  <v-container>
    <h1>{{ error.statusCode }}</h1>
    <p>{{ error.message }}</p>
  </v-container>
</template>

<script lang="ts">
import { defineComponent } from '@nuxtjs/composition-api'
export default defineComponent({
  layout: 'default',
  props: {
    error: {
      type: Object,
      default: () => {}
    }
  }
})
</script>
