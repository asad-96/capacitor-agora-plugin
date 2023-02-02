/* eslint-disable @typescript-eslint/no-unused-vars */
import {
  getCurrentInstance,
  useRoute,
  computed,
  watch,
  useRouter,
  useContext,
  ref
} from '@nuxtjs/composition-api'
import { useDevicesList } from '@vueuse/core'
type RoomRole = 'host' | 'participant'
type RoomStatus = 'waiting' | 'empty' | 'meeting'
export interface Room {
  role?: RoomRole
  status?: RoomStatus
}

export default () => {
  const timer = ref(null)
  const currentInstance = getCurrentInstance()
  const { app } = useContext()
  const route = useRoute()
  const router = useRouter()
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
    appId: computed(() => route.value.query.appId?.toString()),
    room: computed(() => route.value.params.id?.toString()),
    uid: computed(() => route.value.query.uid?.toString()),
    token: computed(() => route.value.query.token?.toString())
  }

  const emitData = {
    user: options.uid.value,
    room: options.room.value
  }

  const socket = app.$nuxtSocket({
    name: 'Video-Conversation',
    channel: '/Video-Conversation'
  })

  const joinMeeting = () => {
    router.push({
      path: `/room/${options.room?.value}/meeting`,
      query: {
        uid: options.uid?.value,
        token: options.token?.value,
        channel: options.room?.value,
        appId: options.appId?.value,
        microphoneId: devices.value?.microphoneId,
        speakerId: devices.value?.speakerId,
        cameraId: devices.value?.cameraId
      }
    })
    clearInterval(timer.value)
  }

  const getRoom = () =>
    new Promise<Room>((resolve, reject) => {
      try {
        const roomInfo = {
          role:
            (route.value.query.role?.toString() as RoomRole) || 'participant',
          status:
            (route.value.query.roomStatus?.toString() as RoomStatus) ||
            'waiting'
        }
        resolve(roomInfo)
      } catch (error) {
        reject(error)
      }
    })

  const executeStartMeeting = () =>
    new Promise<Room>((resolve, reject) => {
      try {
        resolve({ status: 'meeting' })
      } catch (error) {
        reject(error)
      }
    })

  const initialize = async () => {
    try {
      socket.emit('join', emitData)
      const room = await getRoom()
      socket.emit('participant-join', emitData)
      socket.emit('participant-online', emitData)
      if (room.status === 'waiting') onWaitingRoomStatus(room)
      else if (room.status === 'meeting') onMeetingRoomStatus()
    } catch (error: any) {
      console.log({ message: error.message, stack: error.stack })
    }
  }

  const onWaitingRoomStatus = ({ role }: Room) => {
    const LOOP_DURATION = 15000 // 15 seconds

    socket.on('participant-online', async () => {
      if (role === 'host') {
        const { status } = await executeStartMeeting()
        if (status !== 'meeting') return
        socket.emit('start-meeting', emitData)
        joinMeeting()
      }
    })

    socket.on('start-meeting', () => {
      joinMeeting()
    })

    socket.on('who-is-online', () => {
      socket.emit('participant-online', emitData)
    })

    timer.value = setInterval(() => {
      socket.emit('who-is-online')
    }, LOOP_DURATION)
  }

  const onMeetingRoomStatus = () => {
    setTimeout(joinMeeting, 3000)
  }

  return { devices, initialize }
}
