<template>
  <div>
    <w-meeting-room-container />
  </div>
</template>
<script lang="ts">
import { defineComponent } from '@nuxtjs/composition-api'
import WMeetingRoomContainer from '../../../components/meeting-room-container.vue'
export default defineComponent({
  components: { WMeetingRoomContainer },
  layout: 'meeting-room',
  setup() {
    return {}
  }
})
</script>
