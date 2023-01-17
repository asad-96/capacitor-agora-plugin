<template>
  <div>
    <w-meeting-room-container :config="meetingConfig" />
  </div>
</template>
<script lang="ts">
import { defineComponent, ref } from '@nuxtjs/composition-api'
import WMeetingRoomContainer from '../../../components/meeting-room-container.vue'
export interface IMeetingConfig {
  appId: string
  channel: string
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
    const meetingConfig = ref<IMeetingConfig>()
    return { meetingConfig }
  }
})
</script>
