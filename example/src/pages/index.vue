<template>
  <v-container>
    <v-text-field v-model="options.appId" clearable label="appId" />
    <v-text-field v-model="options.room" clearable label="room" />
    <v-text-field v-model="options.token" clearable label="token" />
    <v-text-field v-model="options.uid" clearable label="uid" />
    <v-btn @click="join">Join (with aler after 10s)</v-btn>
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
      appId: 'ba4643f2b3a145f29575b8783d3a5ec1',
      room: 'test1',
      uid: 123123,
      token:
        '007eJxTYLj972uG2yeur6+OxserL3JX/DCpcP9zqbZqI5sXhw96TtdQYEhKNDEzMU4zSjJONDQxTTOyNDU3TbIwtzBOMU40TU02fC+2LqUhkJGhe4IpIyMDBIL4rAwlqcUlhgwMACJ2IXg='
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
      setTimeout(() => {
        CapacitorPluginAgora.showAlert({
          alertText: 'hihi !'
        })
      }, 10000)
    }
    return { options, join }
  }
})
</script>
