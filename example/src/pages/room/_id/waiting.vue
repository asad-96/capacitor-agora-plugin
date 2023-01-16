<template>
  <w-video-waiting-room
    :permissions="permissions"
    :auth-user="authUser"
    :participants="[authUser, ...participants]"
    :show-alert="true"
    :show-back-button="true"
    @call="onCall"
    @call-nudge="onCallNudge"
    @back="$router.back()"
  >
    <template #default>
      <div id="cameraPreview"></div>
    </template>
  </w-video-waiting-room>
</template>
<script lang="ts">
import {
  computed,
  defineComponent,
  onMounted,
  onUnmounted,
  reactive,
  Ref,
  ref,
  useContext
} from '@nuxtjs/composition-api'
import { CameraPreview, CameraPreviewOptions } from '@capgo/camera-preview'
export default defineComponent({
  layout: 'waiting-room',
  setup() {
    const { $vuetify, app, route } = useContext()
    const logs: any = reactive([])
    const options = {
      appId: computed(() => route.value.query.appId),
      room: computed(() => route.value.query.room),
      uid: computed(() => route.value.query.uid),
      token: computed(() => route.value.query.token)
    }
    const socket = app.$nuxtSocket({
      name: 'Video-Conference',
      channel: '/Video-Conference'
    })
    socket.emit('join', { user: options.uid, room: options.uid })
    socket.on('participant-join', (data: any) => {
      logs.push('[participant-join] ' + JSON.stringify(data))
    })

    const imagesCapture = reactive([]) as any
    const cameraPreviewOptions: Ref<CameraPreviewOptions> = ref({
      position: 'front',
      height: 250,
      width: 250,
      toBack: true
    })
    const permissions = reactive([
      {
        type: 'audio',
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
        type: 'micro',
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

    const onCall = (data: any) => {
      console.log('onCall', { data })
    }

    const onCallNudge = (data: any) => {
      console.log('onCallNudge', { data })
    }

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

    const onFlip = async () => {
      try {
        await CameraPreview.flip()
      } catch (error) {
        console.error(error)
      }
    }

    const onCapture = async () => {
      try {
        const data = await CameraPreview.capture({ quality: 50 })
        const base64 = 'data:image/png;base64,' + data.value
        imagesCapture.push(base64)
      } catch (error) {
        console.error(error)
      }
    }
    onMounted(() => onStart())
    onUnmounted(() => onStop())
    return {
      options,
      cameraPreviewOptions,
      logs,
      onStart,
      onStop,
      onFlip,
      onCapture,
      imagesCapture,
      authUser,
      participants,
      permissions,
      onCall,
      onCallNudge
    }
  }
})
</script>
