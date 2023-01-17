<template>
  <v-container>
    <v-row>
      <v-col>
        <h1>you are in meeting room</h1>
      </v-col>
    </v-row>
    <meeting-room-container @click:chat="handleChat"></meeting-room-container>
    <v-navigation-drawer v-show="showChat"></v-navigation-drawer>
  </v-container>
</template>
<script lang="ts">
import { ref } from '@nuxtjs/composition-api'
import { defineComponent } from '@vue/composition-api'
// import { CapacitorPluginAgora } from '@wellcare/capacitor-plugin-agora'

export default defineComponent({
  setup() {
    const showChat = ref(false)
    const handleChat = () => {
      showChat.value = true
      // CapacitorPluginAgora.enablePip()
    }
    return { handleChat }
  }
})
</script>
