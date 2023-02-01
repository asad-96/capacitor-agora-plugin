<template>
  <v-container>
    <v-text-field v-model="options.appId" clearable label="appId" />
    <v-text-field v-model="options.room" clearable label="room" />
    <v-text-field v-model="options.token" clearable label="token" />
    <v-text-field v-model="options.uid" clearable label="uid" />
    <v-select
      v-model="options.roomStatus"
      :items="[
        { label: 'waiting', value: 'waiting' },
        { label: 'meeting', value: 'meeting' }
      ]"
      item-text="label"
      item-value="value"
      label="room status (mock)"
    ></v-select>
    <v-select
      v-model="options.role"
      :items="[
        { label: 'host', value: 'host' },
        { label: 'participant', value: 'participant' }
      ]"
      item-text="label"
      item-value="value"
      label="role (mock)"
    ></v-select>
    <v-btn @click="enterWaitingRoom">ENTER WAITING ROOM</v-btn>
    <!-- <v-btn @click="joinChannel()">join</v-btn> -->
  </v-container>
</template>
<!-- eslint-disable @typescript-eslint/no-unused-vars -->
<script lang="ts">
import { defineComponent, reactive, useRouter } from '@nuxtjs/composition-api'
// import { CapacitorPluginAgora } from '@wellcare/capacitor-plugin-agora'
export default defineComponent({
  setup() {
    const router = useRouter()
    const options = reactive({
      appId: 'ba4643f2b3a145f29575b8783d3a5ec1',
      room: 'test',
      uid: '0',
      token:
        '007eJxTYDjFoPh6udNGrz8sy9lSzb3Wnj+i/Gx332Gnp/4Wyl9c5Z4oMCQlmpiZGKcZJRknGpqYphlZmpqbJlmYWxinGCeapiYbzr5/M7khkJGBrXsLMyMDBIL4LAwlqcUlDAwAVSYf6Q==',
      roomStatus: 'waiting',
      role: 'host'
    })
    const enterWaitingRoom = () => {
      const decodeToken = encodeURIComponent(options.token)
      router.push(
        `/room/${options.room}/waiting?uid=${options.uid}&token=${decodeToken}&appId=${options.appId}&roomStatus=${options.roomStatus}&role=${options.role}`
      )
    }
    return { options, enterWaitingRoom }
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
