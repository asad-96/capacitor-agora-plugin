<template>
  <div>
    <w-video-kit
      :auth-id="authUser._id"
      :active-id="activeId"
      :participants="[...participants, { ...authUser, signal: localSignal }]"
      :camera-state="localVideoState"
      :microphone-state="localAudioState"
      @click:participant="onActiveParticipantChange"
    ></w-video-kit>
    <v-snackbar v-model="snackbar.show">{{ snackbar.message }}</v-snackbar>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  ref,
  useContext,
  reactive,
  useRoute,
  useRouter,
  computed
} from '@nuxtjs/composition-api'
import AgoraRTC, {
  IAgoraRTCClient,
  ILocalAudioTrack,
  ILocalVideoTrack,
  NetworkQuality,
  IAgoraRTCRemoteUser
} from 'agora-rtc-sdk-ng'
export type Signal = 0 | 1 | 2 | 3 | 4 | 5 | 6
export interface IRemoteSignal {
  [uid: string]: NetworkQuality
}
export default defineComponent({
  name: 'RoomPage',
  layout: 'meeting-room',
  setup() {
    const route = useRoute()
    const snackbar = reactive({
      show: false,
      message: ''
    })
    const activeId = ref(route.value.query.uid)
    const authUser = computed(() => ({
      _id: route.value.query.uid,
      avatar: {
        url: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8czLLczg6As4Noqb2sanBsq4n6lf4anQY4g&usqp=CAU'
      },
      name: route.value.query.uid,
      role: 'Me',
      ...agoraEngine.value
    }))

    const participants: any = computed(() => {
      // Mock only => will map with Wellcare user data
      const extendMockData = {
        avatar: {
          url: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8czLLczg6As4Noqb2sanBsq4n6lf4anQY4g&usqp=CAU'
        },
        role: 'Participant'
      }
      if (!agoraEngine.value) return []
      const result = agoraEngine.value.remoteUsers.map(
        (remoteUser: IAgoraRTCRemoteUser) => {
          const participant = {
            _id: remoteUser.uid.toString(),
            name: remoteUser.uid.toString(),
            signal:
              remoteSignals.value && remoteSignals.value[remoteUser.uid]
                ? remoteSignals.value[remoteUser.uid].downlinkNetworkQuality
                : 0,
            ...remoteUser,
            ...extendMockData
          }
          return participant
        }
      )
      return result
    })

    const remoteSignals = ref<IRemoteSignal>()
    const localSignal = ref<Signal>(0)
    const showMessage = (message: string) => {
      snackbar.show = true
      snackbar.message = message
    }

    const { $config } = useContext()
    const router = useRouter()
    const options = reactive({
      appId: $config.agora.appId,
      channel: route.value.params.id,
      token: $config.agora.token,
      uid: route.value.query.uid || 0
    })

    const localAudioTrack = ref<ILocalAudioTrack>()
    const localVideoTrack = ref<ILocalVideoTrack>()
    const localVideoState = computed(
      () => localVideoTrack.value?.enabled || false
    )
    const localAudioState = computed(
      () => localAudioTrack.value?.enabled || false
    )
    const localPlayerContainer = document.createElement('div')
    const agoraEngine = ref<IAgoraRTCClient>()
    const startBasicCall = () => {
      localPlayerContainer.id = options.uid.toString()
      localPlayerContainer.style.width = '100%'
      localPlayerContainer.style.height = '100%'

      agoraEngine.value = AgoraRTC.createClient({
        mode: 'rtc',
        codec: 'vp8'
      })

      agoraEngine.value.on('user-joined', (user: IAgoraRTCRemoteUser) => {
        showMessage(`${user.uid.toString()} has joined.`)
      })

      agoraEngine.value.on(
        'user-left',
        (user: IAgoraRTCRemoteUser, reason: string) => {
          showMessage(`${user.uid.toString()} has left. Reason: ${reason}`)
        }
      )

      agoraEngine.value.on(
        'user-published',
        async (user: IAgoraRTCRemoteUser, mediaType: 'audio' | 'video') => {
          await agoraEngine.value?.subscribe(user, mediaType)
          const remotePlayerContainer = document.createElement('div')
          remotePlayerContainer.style.width = '100%'
          remotePlayerContainer.style.height = '100%'
          if (mediaType === 'video') {
            remotePlayerContainer.id = user.uid.toString()
            document
              .getElementById(`participant_${user.uid.toString()}`)
              ?.querySelector('.image-placeholder')
              ?.remove()

            document
              .getElementById(`participant_${user.uid.toString()}`)
              ?.appendChild(remotePlayerContainer)

            user.videoTrack?.play(remotePlayerContainer)
          }
          if (mediaType === 'audio') {
            user.audioTrack?.play()
          }
        }
      )
    }

    const join = async () => {
      await agoraEngine.value?.join(
        options.appId,
        options.channel,
        options.token,
        authUser.value._id.toString()
      )

      localAudioTrack.value = await AgoraRTC.createMicrophoneAudioTrack()
      localVideoTrack.value = await AgoraRTC.createCameraVideoTrack()

      document
        .getElementById('participant_me')
        ?.querySelector('.image-placeholder')
        ?.remove()

      document
        .getElementById('participant_me')
        ?.appendChild(localPlayerContainer)

      await agoraEngine.value?.publish([
        localAudioTrack.value,
        localVideoTrack.value
      ])

      localVideoTrack.value.play(localPlayerContainer)
      agoraEngine.value?.on('network-quality', (stats: NetworkQuality) => {
        localSignal.value = stats.downlinkNetworkQuality
        remoteSignals.value = agoraEngine.value?.getRemoteNetworkQuality()
      })
    }

    const leave = () => {
      localAudioTrack.value?.close()
      localVideoTrack.value?.close()
      agoraEngine.value?.leave()
      router.push('/')
    }

    const onActiveParticipantChange = (uid: string) => {
      activeId.value = uid
    }

    const onCameraClick = () => {
      if (localVideoState.value) {
        localVideoTrack.value?.setEnabled(false)
      } else localVideoTrack.value?.setEnabled(true)
    }

    const onMicrophoneClick = () => {
      if (localAudioState.value) {
        localAudioTrack.value?.setEnabled(false)
      } else localAudioTrack.value?.setEnabled(true)
    }

    startBasicCall()
    join()

    return {
      options,
      join,
      leave,
      onActiveParticipantChange,
      onCameraClick,
      onMicrophoneClick,
      localVideoState,
      localAudioState,
      snackbar,
      authUser,
      activeId,
      participants,
      agoraEngine,
      localSignal,
      remoteSignals
    }
  }
})
</script>
