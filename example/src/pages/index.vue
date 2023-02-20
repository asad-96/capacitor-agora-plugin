<template>
  <v-container>
    <v-text-field v-model="options.appId" clearable label="appId" />
    <v-text-field v-model="options.room" clearable label="room" />
    <v-text-field v-model="options.token" clearable label="token" />
    <v-text-field v-model="options.uid" clearable label="uid" />
    <v-text-field v-model="options.chatRoom" clearable label="chat room" />
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
    // const userId = computed(() => state.authen.user._id)
    const router = useRouter()
    const options = reactive({
      appId: 'ba4643f2b3a145f29575b8783d3a5ec1',
      room: 'test1',
      uid: '',
      chatRoom: '',
      token:
        '007eJxTYJDpTrv7z23J6T/8pf5rH22WNds7vyf9TIbrtduGMfMP3OJUYEhKNDEzMU4zSjJONDQxTTOyNDU3TbIwtzBOMU40TU02dLrwKbkhkJEhfZMyCyMDBIL4rAwlqcUlhgwMAPlEIV8=',
      roomStatus: 'meeting',
      role: 'host'
    })

    const enterWaitingRoom = () => {
      const decodeToken = encodeURIComponent(options.token)
      router.push(
        `/agora?room=${options.room}&uid=${options.uid}&token=${decodeToken}&appId=${options.appId}&roomStatus=${options.roomStatus}&role=${options.role}&chatRoom=${options.chatRoom}`
      )
    }
    return { options, enterWaitingRoom }
  }
})
</script>
