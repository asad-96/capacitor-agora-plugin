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
      <div id="cameraPreview">
        <v-btn class="mt-16" @click="joinMeeting">Start meeting</v-btn>
      </div>
    </template>
  </w-video-waiting-room>
</template>
<!-- eslint-disable @typescript-eslint/no-unused-vars -->
<script lang="ts">
import {
  computed,
  defineComponent,
  onMounted,
  onUnmounted,
  reactive,
  Ref,
  ref,
  useContext,
  useRouter,
  watch
} from '@nuxtjs/composition-api'
import { useDevicesList } from '@vueuse/core'
import { CameraPreview, CameraPreviewOptions } from '@capgo/camera-preview'
export default defineComponent({
  layout: 'meeting-room',
  setup() {
    const { $vuetify, app, route } = useContext()
    const router = useRouter()
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

    const onStart = async () => {
      try {
        cameraPreviewOptions.value.height = $vuetify.breakpoint.height
        cameraPreviewOptions.value.width = $vuetify.breakpoint.width
        await CameraPreview.start(cameraPreviewOptions.value)
      } catch (error) {
        console.error(error)
      }
    }

    const onStop = async () => {
      try {
        await CameraPreview.stop()
      } catch (error) {
        console.error(error)
      }
    }

    onMounted(() => {
      onStart()
      emitReady()
    })

    onUnmounted(() => onStop())
    // Waiting room with microphone testing
    /* ------------ */
    /* ------------ */
    // Calling login
    const {
      videoInputs: cameras,
      audioInputs: microphones,
      audioOutputs: speakers
    } = useDevicesList({
      requestPermissions: true
    })

    const devices = ref()
    const defaultDevices = computed(() => ({
      cameraId: cameras.value[0]?.deviceId,
      microphoneId: microphones.value[0]?.deviceId,
      speakerId: speakers.value[0]?.deviceId
    }))

    watch(defaultDevices, () => (devices.value = defaultDevices.value))

    const options = {
      appId: computed(() => route.value.query.appId),
      room: computed(() => route.value.params.id),
      uid: computed(() => route.value.query.uid),
      token: computed(() => route.value.query.token)
    }

    const socket = app.$nuxtSocket({
      name: 'Video-Conference',
      channel: '/Video-Conversation'
    })

    socket.emit('join', { user: options.uid.value, room: options.room.value })

    const emitReady = () =>
      socket.emit('participant-join', { user: options.uid.value })

    const joinMeeting = () => {
      router.push({
        path: `/room/${options.room.value}/meeting`,
        query: {
          uid: options.uid.value,
          token: options.token.value,
          channel: options.room.value,
          appId: options.appId.value,
          microphoneId: devices.value.microphoneId,
          speakerId: devices.value.speakerId,
          cameraId: devices.value.cameraId
        }
      })
    }

    socket.on('participant-join', (data: any) => {
      console.log('[participant-join] ' + JSON.stringify(data))
      joinMeeting()
    })

    return {
      options,
      cameraPreviewOptions,
      onStart,
      onStop,
      authUser,
      participants,
      permissions,
      devices,
      joinMeeting
    }
  }
})
</script>
