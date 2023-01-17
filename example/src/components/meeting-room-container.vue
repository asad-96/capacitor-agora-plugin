<template>
  <div>
    <w-video-kit
      :auth-id="authUser._id"
      :active-id="activeId"
      :participants="[...participants, { ...localUser, signal: localSignal }]"
      :video-state="localVideoState"
      :microphone-state="localAudioState"
      :devices="devicesManager"
      @click:participant="onActiveParticipantChange"
      @click:exit="onExitCall"
      @click:camera="onCameraClick"
      @click:microphone="onMicrophoneClick"
      @click:device-microphone="onMicrophoneChangeManually"
      @click:device-speaker="onSpeakerChangeManually"
      @click:device-camera="onCameraChangeManually"
    ></w-video-kit>
    <v-snackbar v-model="snackbar.show">{{ snackbar.message }}</v-snackbar>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  ref,
  reactive,
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
  props: {
    config: {
      type: Object,
      default: () => {}
    },
    authUser: {
      type: Object,
      default: () => {}
    }
  },
  setup(props) {
    const snackbar = reactive({
      show: false,
      message: ''
    })
    const activeId = ref(props.config.uid)
    const localUser = computed(() => ({
      ...props.authUser,
      ...agoraEngine.value
    }))

    const devicesManager = ref({
      cameraId: props.config.cameraId,
      speakerId: props.config.speakerId,
      microphoneId: props.config.microphoneId
    })

    const participants: any = computed(() => {
      // Mock only => will map with participant info in room info
      const extendMockData = {
        avatar: {
          url: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8czLLczg6As4Noqb2sanBsq4n6lf4anQY4g&usqp=CAU'
        },
        role: 'Participant'
      }
      if (!agoraEngine.value) return []
      const result = agoraEngine.value.remoteUsers.map((remoteUser: any) => {
        const participant = {
          _id: remoteUser.uid.toString(),
          name: remoteUser.uid.toString(),
          hasVideo: !remoteUser._video_muted_,
          hasAudio: !remoteUser._audio_muted_,
          signal:
            remoteSignals.value && remoteSignals.value[remoteUser.uid]
              ? remoteSignals.value[remoteUser.uid].downlinkNetworkQuality
              : 0,
          ...remoteUser,
          ...extendMockData
        }
        return participant
      })
      return result
    })

    const remoteSignals = ref<IRemoteSignal>()
    const localSignal = ref<Signal>(0)
    const showMessage = (message: string) => {
      snackbar.show = true
      snackbar.message = message
    }
    const router = useRouter()
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
      localPlayerContainer.id = props.config.uid.toString()
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
          const remotePlayerContainer = document.getElementById(
            user.uid.toString()
          )
          if (mediaType === 'video') {
            document
              .getElementById(`participant_${user.uid.toString()}`)
              ?.querySelector('.image-placeholder')
              ?.remove()

            if (remotePlayerContainer) {
              console.log('[debug] already have remotePlayerContainer')
              user.videoTrack?.play(remotePlayerContainer)
            } else {
              console.log('[debug] not have remotePlayerContainer yet')
              const newContainer = document.createElement('div')
              newContainer.id = user.uid.toString()
              newContainer.style.width = '100%'
              newContainer.style.height = '100%'
              document
                .getElementById(`participant_${user.uid.toString()}`)
                ?.appendChild(newContainer)
              user.videoTrack?.play(newContainer)
            }
          }
          if (mediaType === 'audio') {
            user.audioTrack?.play()
          }
        }
      )
    }

    const join = async () => {
      const { appId, channel, token, uid, microphoneId, cameraId } =
        props.config

      console.log('[config]', token)
      await agoraEngine.value?.join(
        appId,
        channel,
        '007eJxTYBCt00g/ceDqLCGx/6wXVsT9irkg0LH3sqozp/e5X75COXcUGJISTcxMjNOMkowTDU1M04wsTc1NkyzMLYxTjBNNU5MNf6ceS24IZGSQtb7OysgAgSA+C0NJanEJAwMAR5sfjg==',
        uid
      )
      localAudioTrack.value = await AgoraRTC.createMicrophoneAudioTrack({
        microphoneId
      })
      localVideoTrack.value = await AgoraRTC.createCameraVideoTrack({
        cameraId
      })

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

    const onExitCall = () => {
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

    const onCameraChangeManually = (deviceId: string) => {
      console.log('onCameraChangeManually', deviceId)
    }

    const onMicrophoneChangeManually = (deviceId: string) => {
      console.log('onCameraChangeManually', deviceId)
    }

    const onSpeakerChangeManually = (deviceId: string) => {
      console.log('onCameraChangeManually', deviceId)
    }

    /// INIT
    startBasicCall()
    join()

    return {
      join,
      onExitCall,
      onActiveParticipantChange,
      onCameraClick,
      onMicrophoneClick,
      localVideoState,
      localAudioState,
      snackbar,
      localUser,
      activeId,
      participants,
      agoraEngine,
      localSignal,
      remoteSignals,
      onSpeakerChangeManually,
      onMicrophoneChangeManually,
      onCameraChangeManually,
      devicesManager
    }
  }
})
</script>
