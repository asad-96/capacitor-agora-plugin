<template>
  <v-container>
    <v-text-field v-model="options.appId" clearable label="appId" />
    <v-text-field v-model="options.room" clearable label="room" />
    <v-text-field v-model="options.token" clearable label="token" />
    <v-text-field v-model="options.uid" clearable label="uid" />
    <v-btn @click="join">Join</v-btn>
    <v-btn @click="setTimeoutToLeave">Set timeout to leave</v-btn>
    <!-- <v-btn @click="joinChannel()">join</v-btn> -->
  </v-container>
</template>
<!-- eslint-disable @typescript-eslint/no-unused-vars -->
<script lang="ts">
// @ts-nocheck
import {
  defineComponent,
  onMounted,
  onUnmounted,
  reactive,
  useRouter
} from '@nuxtjs/composition-api'
import { CapacitorPluginAgora } from '@wellcare/capacitor-plugin-agora'
export default defineComponent({
  setup() {
    // const userId = computed(() => state.authen.user._id)
    const options = reactive({
      appId: 'ba4643f2b3a145f29575b8783d3a5ec1',
      room: 'test1',
      uid: 123123,
      token:
        '007eJxTYAgMrm5R2bD3f/Tr+PdJJhYH1BgjNdevTXlXrxdhqNRczqTAkJySmGaUbJxqnJqaYmJuYWKZYmBkamaQbJlmYWKckpr4nmNtSkMgI8OZGSsYGKEQxGdlKEktLjFkYAAALAofSw=='
    })
    const join = () => {
      CapacitorPluginAgora.addListener('debug', (data) => {
        console.log('[Wellcare] debug ' + JSON.stringify(data))
      })
      CapacitorPluginAgora.addListener('network-quality', (data) => {
        console.log('[Wellcare] network-quality ' + JSON.stringify(data))
      })
      CapacitorPluginAgora.addListener('onSelfAction', (data) => {
        console.log('[Wellcare] onSelfAction', data)
      })
      CapacitorPluginAgora.addListener('onParticipantAction', (data) => {
        console.log('[Wellcare] onParticipantAction', data)
      })
      CapacitorPluginAgora.addListener('exception', (data) => {
        console.log('[Wellcare] exception', data)
      })
      CapacitorPluginAgora.addListener('onRemoteStreamChanged', (data) => {
        console.log('[Wellcare] onRemoteStreamChanged', data)
      })
      CapacitorPluginAgora.joinChannel({
        room: options.room,
        uid: options.uid,
        token: options.token,
        appId: options.appId,
        user: {
          name: 'Khai Hoan',
          uid: 123123,
          role: 'host'
        }
      })
    }
    return { options, join }
  }
})
</script>
