<template>
  <w-video-waiting-room
    :permissions="permissions"
    :auth-user="authUser"
    :participants="[authUser, ...participants]"
    :show-alert="true"
    :show-back-button="true"
    :devices="devices"
    @back="$router.back()"
    @click:device-microphone="($data) => (devices.microphoneId = $data)"
    @click:device-speaker="($data) => (devices.speakerId = $data)"
    @click:device-camera="($data) => (devices.cameraId = $data)"
  >
    <template #default>
      <div id="cameraPreview"></div>
    </template>
  </w-video-waiting-room>
</template>
<!-- eslint-disable -->
<script lang="ts">
import {
  defineComponent,
  onMounted,
  onUnmounted,
  reactive,
  Ref,
  ref,
  useContext
} from '@nuxtjs/composition-api'
import { CameraPreview, CameraPreviewOptions } from '@capgo/camera-preview'
import useWaitingRoom from '../../../composables/use-waiting-room'
export default defineComponent({
  layout: 'meeting-room',
  setup() {
    const WaitingRoom = useWaitingRoom()
    const { $vuetify } = useContext()
    const cameraPreviewOptions: Ref<CameraPreviewOptions> = ref({
      position: 'front',
      height: 250,
      width: 250,
      toBack: true
    })
    const permissions = reactive([
      {
        type: 'speaker',
        icon: {
          on: '$volumnHigh',
          off: '$volumnOff'
        },
        status: {
          label: 'OK',
          value: true
        }
      },
      {
        type: 'camera',
        icon: {
          on: '$videoOutline',
          off: '$videoOffOutline'
        },
        status: {
          label: 'OK',
          value: true
        }
      },
      {
        type: 'connection',
        icon: {
          on: '$tune',
          off: '$tune'
        },
        status: {
          label: 'OK',
          value: true
        }
      },
      {
        type: 'microphone',
        icon: {
          on: '$microphoneOutline',
          off: '$microphoneOff'
        },
        status: {
          label: 'OK',
          value: true
        }
      }
    ])
    const authUser = ref({
      _id: '1',
      name: 'Khai Hoan',
      avatar: {
        url: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdEM3WslIvUN-HVSvP06Y6JhISiVw5DGb2VQ&usqp=CAU'
      },
      role: 'Patient'
    })
    const participants = reactive([
      {
        _id: '2',
        name: 'Hoa',
        avatar: {
          url: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRffyTDh4fWE7u1Am9reNoPw7m9vG7EYv489A&usqp=CAU'
        },
        role: 'Doctor'
      }
    ])

    const onCameraPreviewStart = async () => {
      try {
        cameraPreviewOptions.value.height = $vuetify.breakpoint.height
        cameraPreviewOptions.value.width = $vuetify.breakpoint.width
        await CameraPreview.start(cameraPreviewOptions.value)
      } catch (error) {
        console.error(error)
      }
    }

    const onCameraPreviewStop = async () => {
      try {
        await CameraPreview.stop()
      } catch (error) {
        console.error(error)
      }
    }

    onMounted(() => {
      onCameraPreviewStart()
      WaitingRoom.initialize()
    })

    onUnmounted(() => onCameraPreviewStop())
    // Waiting room with microphone testing
    /* ------------ */
    /* ------------ */

    return {
      cameraPreviewOptions,
      authUser,
      participants,
      permissions,
      devices: WaitingRoom.devices
    }
  }
})
</script>
