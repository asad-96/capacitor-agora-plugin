<template>
  <v-container>
    <v-text-field v-model="options.appId" clearable label="appId" />
    <v-text-field v-model="options.room" clearable label="room" />
    <v-text-field v-model="options.token" clearable label="token" />
    <v-text-field v-model="options.uid" clearable label="uid" />
    <v-btn
      :to="`/room/${options.room}/waiting?uid=${options.uid}&token=${options.token}&appId=${options.appId}`"
      >ENTER WAITING ROOM</v-btn
    >
    <!-- <v-btn @click="joinChannel()">join</v-btn> -->
  </v-container>
</template>
<script lang="ts">
import { defineComponent, reactive, useContext } from '@nuxtjs/composition-api'
// import { CapacitorPluginAgora } from '@wellcare/capacitor-plugin-agora'
export default defineComponent({
  setup() {
    const { $config } = useContext()
    const options = reactive({
      appId: $config.agora.appId,
      room: $config.agora.channel,
      uid: $config.agora.uid,
      token: $config.agora.token
    })
    return { options }
  }
  // methods: {
  //   joinChannel() {
  //     console.log('appId: ', this.options.appId)
  //     console.log('channel: ', this.options.room)
  //     console.log('uid: ', this.options.uid)
  //     console.log('token: ', this.options.token)
  //     const options = {
  //       room: this.options.room,
  //       uid: this.options.uid,
  //       token: this.options.token,
  //       appId: this.options.appId
  //     }
  //     CapacitorPluginAgora.joinChannel(options).then((res: any) => {
  //       console.log('res: ', res)
  //     })
  //     return ''
  //   }
  // }
})
</script>
