<template>
  <div>
    <w-meeting-room-container
      v-if="!isNative"
      :config="meetingConfig"
      :auth-user="authUser"
    />
  </div>
</template>
<script lang="ts">
import { defineComponent, computed, useRoute } from '@nuxtjs/composition-api'
import { Capacitor } from '@capacitor/core'
import { CapacitorPluginAgora } from '@wellcare/capacitor-plugin-agora'
import WMeetingRoomContainer from '../../../components/meeting-room-container.vue'

export interface IMeetingConfig {
  appId: string
  room: string
  token: string
  uid: string
  microphoneId?: string
  speakerId?: string
  cameraId?: string
}
export default defineComponent({
  components: { WMeetingRoomContainer },
  layout: 'meeting-room',
  setup() {
    const route = useRoute()
    const isNative = Capacitor.isNativePlatform()
    // mock user
    const authUser = computed(() => ({
      _id: route.value.query.uid.toString(),
      name: route.value.query.uid.toString(),
      avatar: {
        url: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8czLLczg6As4Noqb2sanBsq4n6lf4anQY4g&usqp=CAU'
      },
      role: 'Me'
    }))
    // mock config
    const meetingConfig = computed<IMeetingConfig>(() => {
      const decodeToken = decodeURIComponent(route.value.query.token.toString())
      return {
        appId: route.value.query.appId.toString(),
        room: route.value.params.id.toString(),
        token: decodeToken,
        uid: route.value.query.uid.toString(),
        microphoneId: route.value.query.microphoneId.toString(),
        cameraId: route.value.query.cameraId.toString(),
        speakerId: route.value.query.speakerId.toString()
      }
    })
    if (isNative) {
      CapacitorPluginAgora.joinChannel(meetingConfig.value).then((res: any) => {
        console.log('res: ', res)
      })
    }

    return { meetingConfig, authUser, isNative }
  }
})
</script>
