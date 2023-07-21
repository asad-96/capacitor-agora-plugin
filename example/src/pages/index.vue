<template>
  <v-container>
    <v-text-field v-model="options.appId" clearable label="appId" />
    <v-text-field v-model="options.room" clearable label="room" />
    <v-text-field v-model="options.token" clearable label="token" />
    <v-text-field v-model="options.uid" clearable label="uid" />
    <v-btn @click="join">Join (auto record after 3s)</v-btn>
    <v-btn @click="setTimeoutToLeave">Set timeout to leave</v-btn>
    <!-- <v-btn @click="joinChannel()">join</v-btn> -->
  </v-container>
</template>
<!-- eslint-disable @typescript-eslint/no-unused-vars -->
<script lang="ts">
// @ts-nocheck
import { defineComponent, reactive } from '@nuxtjs/composition-api'
import { CapacitorPluginAgora } from '@wellcare/capacitor-plugin-agora'
export default defineComponent({
  setup() {
    // const userId = computed(() => state.authen.user._id)
    const options = reactive({
      appId: 'cdaf2c3e3eed47849d02560c9f843dea',
      room: 'test1',
      uid: 123123,
      token:
        '007eJxTYJg+0SRhwuPZ0cpnr6l9tOOeGcC7v5nr1olbnw79j/i6/oufAkNySmKaUbJxqnFqaoqJuYWJZYqBkamZQbJlmoWJcUpqYv+MLSkNgYwMLf5yjIwMEAjiszKUpBaXGDIwAAA4oSJi'
    })
    /**
     * join agora video call use capacitor plugin
     */
    const join = () => {
      CapacitorPluginAgora.addListener('debug', (data) => {
        console.log('[Wellcare] debug ' + JSON.stringify(data))
      })
      CapacitorPluginAgora.addListener('network-quality', (data) => {
        console.log('[Wellcare] network-quality ' + JSON.stringify(data))
      })
      CapacitorPluginAgora.addListener('onSelfAction', (data) => {
        console.log('[Wellcare] onSelfAction', JSON.stringify(data))
        const { event } = data
        if (event === 'back') {
          CapacitorPluginAgora.enterPictureInPictureMode()
        }
      })
      CapacitorPluginAgora.addListener('onParticipantAction', (data) => {
        console.log('[Wellcare] onParticipantAction', JSON.stringify(data))
      })
      CapacitorPluginAgora.addListener('exception', (data) => {
        console.log('[Wellcare] exception', JSON.stringify(data))
      })
      CapacitorPluginAgora.addListener('onRemoteStreamChanged', (data) => {
        console.log('[Wellcare] onRemoteStreamChanged', JSON.stringify(data))
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
      setTimeout(() => {
        CapacitorPluginAgora.showRecordingStatus({
          isShown: true
        })
      }, 3000)
    }
    return { options, join }
  }
})
</script>
