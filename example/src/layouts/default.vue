<template>
  <v-app>
    <v-main>
      <Nuxt keep-alive />
    </v-main>
    <w-bottom-menu :menu-items="menuItems" :max="4" />
  </v-app>
</template>
<script lang="ts">
import { defineComponent } from '@nuxtjs/composition-api'

export default defineComponent({
  transition: 'page',
  setup() {
    const menuItems = [
      {
        text: 'Main',
        icon: '$homeOutline',
        activeIcon: '$home',
        to: '/'
      },
      {
        text: 'Debug',
        icon: '$shieldBugOutline',
        activeIcon: '$shieldBug',
        to: 'debug'
      }
    ]
    return { menuItems }
  }
})
</script>
