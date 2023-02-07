<template>
  <div>
    <w-video-kit
      :auth-id="authUser._id"
      :spotlight-id="spotlightId"
      :participants="[...participants, { ...localUser, signal: localSignal }]"
      :video-state="localVideoState"
      :microphone-state="localAudioState"
      :devices="devicesManager"
      :volumn-indicators="volumnIndicators"
      @click:exit="onExitCall"
      @click:camera="onCameraClick"
      @click:microphone="onMicrophoneClick"
      @click:device-microphone="onMicrophoneChangeManually"
      @click:device-speaker="onSpeakerChangeManually"
      @click:device-camera="onCameraChangeManually"
    >
      <template #chat-area>
        <div class="chat-container">
          <w-chat-window
            :key="authUser._id"
            :theme="'dark'"
            :room="config.chatRoom || authUser._id"
            :user="authUser._id"
            :should-show-header="false"
            :background="'transparent'"
            :from="'cam'"
            :media-feat="{
              voice: false,
              image: false,
              video: false
            }"
          ></w-chat-window>
        </div>
      </template>
    </w-video-kit>
    <v-snackbar v-model="snackbar.show">{{ snackbar.message }}</v-snackbar>
  </div>
</template>
<!-- eslint-disable @typescript-eslint/no-unused-vars -->
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
  IAgoraRTCRemoteUser,
  UID
} from 'agora-rtc-sdk-ng'
export type Signal = 0 | 1 | 2 | 3 | 4 | 5 | 6
export interface IRemoteSignal {
  [uid: string]: NetworkQuality
}
export interface IVolumnIndicator {
  uid: UID
  level: number
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
    const spotlightId = computed(() => participants.value[0]?._id)
    const playbackDevice = ref(props.config.speakerId)
    const devicesManager = computed(() => ({
      cameraId: (localVideoTrack as any).value?._config.cameraId,
      microphoneId: (localAudioTrack as any).value?._config.microphoneId,
      speakerId: playbackDevice.value
    }))

    const volumnIndicators = ref<IVolumnIndicator[]>([])
    const participants = computed<any>(() => {
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
    const showMessage = (message: string) => {
      snackbar.show = true
      snackbar.message = message
    }
    const router = useRouter()
    const remoteSignals = ref<IRemoteSignal>()

    const localUser = computed(() => ({
      ...props.authUser,
      ...agoraEngine.value
    }))
    const localSignal = ref<Signal>(0)
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

    const initialArogaClient = () => {
      agoraEngine.value = AgoraRTC.createClient({
        mode: 'rtc',
        codec: 'vp8'
      })
      agoraEngine.value.on('user-joined', onUserJoined)
      agoraEngine.value.on('user-left', onUserleft)
      agoraEngine.value.on('user-published', onUserPublished)
      agoraEngine.value.on('user-unpublished', onUserUnpublished)
    }

    const onUserJoined = (user: IAgoraRTCRemoteUser) =>
      showMessage(`${user.uid.toString()} has joined.`)

    const onUserleft = (user: IAgoraRTCRemoteUser, reason: string) =>
      showMessage(`${user.uid.toString()} has left. Reason: ${reason}`)

    const onUserPublished = async (
      user: IAgoraRTCRemoteUser,
      mediaType: 'audio' | 'video'
    ) => {
      await agoraEngine.value?.subscribe(user, mediaType)
      const remotePlayerContainer = document.getElementById(user.uid.toString())
      if (mediaType === 'video') {
        const remoteVideoWrapper = document.getElementById(
          `participant_${user.uid.toString()}`
        )
        const remoteImagePlaceholder =
          remoteVideoWrapper?.querySelector('.image-placeholder')
        remoteImagePlaceholder?.classList.add('hidden')

        if (remotePlayerContainer) {
          user.videoTrack?.play(remotePlayerContainer)
        } else {
          const newRemotePlayerContainer = document.createElement('div')
          newRemotePlayerContainer.id = user.uid.toString()
          newRemotePlayerContainer.style.width = '100%'
          newRemotePlayerContainer.style.height = '100%'
          remoteVideoWrapper?.appendChild(newRemotePlayerContainer)
          user.videoTrack?.play(newRemotePlayerContainer)
        }
      }
      if (mediaType === 'audio') {
        user.audioTrack?.play()
        user.audioTrack?.setPlaybackDevice(devicesManager.value.speakerId)
      }
    }

    const onUserUnpublished = async (
      user: IAgoraRTCRemoteUser,
      mediaType: 'audio' | 'video'
    ) => {
      await agoraEngine.value?.unsubscribe(user, mediaType)
      if (mediaType === 'video') {
        const remoteVideoWrapper = document.getElementById(
          `participant_${user.uid.toString()}`
        )
        user.videoTrack?.stop()
        const remoteImagePlaceholder =
          remoteVideoWrapper?.querySelector('.image-placeholder')
        remoteImagePlaceholder?.classList.remove('hidden')
      }

      if (mediaType === 'audio') {
        user.audioTrack?.stop()
      }
    }

    const join = async () => {
      localPlayerContainer.id = props.config.uid.toString()
      localPlayerContainer.style.width = '100%'
      localPlayerContainer.style.height = '100%'
      const { appId, room, token, uid, microphoneId, cameraId } = props.config
      console.log({ appId, room, token, uid, microphoneId, cameraId })
      await agoraEngine.value?.join(appId, room, token, uid)
      const defaultAgoraMicrophones = await AgoraRTC.getMicrophones()
      const defaultAgoraCameras = await AgoraRTC.getCameras()

      const getMicrophoneId = () => {
        if (microphoneId) return microphoneId
        return defaultAgoraMicrophones[0].deviceId
      }

      const getCameraId = () => {
        if (cameraId) return cameraId
        return defaultAgoraCameras[0].deviceId
      }

      localAudioTrack.value = await AgoraRTC.createMicrophoneAudioTrack({
        microphoneId: getMicrophoneId()
      })
      localVideoTrack.value = await AgoraRTC.createCameraVideoTrack({
        cameraId: getCameraId()
      })

      const localVideoWrapper = document.getElementById('participant_me')
      const localImagePlaceholder =
        localVideoWrapper?.querySelector('.image-placeholder')

      localImagePlaceholder?.classList.add('hidden')
      localVideoWrapper?.appendChild(localPlayerContainer)

      await agoraEngine.value?.publish([
        localAudioTrack.value,
        localVideoTrack.value
      ])

      localVideoTrack.value.play(localPlayerContainer)
      agoraEngine.value?.on('network-quality', (stats: NetworkQuality) => {
        localSignal.value = stats.downlinkNetworkQuality
        remoteSignals.value = agoraEngine.value?.getRemoteNetworkQuality()
      })
      agoraEngine.value?.enableAudioVolumeIndicator()
      agoraEngine.value?.on('volume-indicator', function (result) {
        volumnIndicators.value = result
      })
    }

    const onExitCall = () => {
      localAudioTrack.value?.close()
      localVideoTrack.value?.close()
      agoraEngine.value?.leave()
      router.push('/')
    }

    const onCameraClick = () => {
      const localVideoWrapper = document.getElementById('participant_me')
      const localImagePlaceholder =
        localVideoWrapper?.querySelector('.image-placeholder')
      if (localVideoState.value) {
        localVideoTrack.value?.setEnabled(false)
        localImagePlaceholder?.classList.remove('hidden')
      } else {
        localImagePlaceholder?.classList.add('hidden')
        localVideoTrack.value?.setEnabled(true)
      }
    }

    const onMicrophoneClick = () => {
      if (localAudioState.value) {
        localAudioTrack.value?.setEnabled(false)
      } else localAudioTrack.value?.setEnabled(true)
    }

    const onCameraChangeManually = async (deviceId: string) => {
      if (deviceId === devicesManager.value.cameraId) return
      try {
        localVideoTrack.value?.close()
        await agoraEngine.value?.unpublish(localVideoTrack.value)
        localVideoTrack.value = await AgoraRTC.createCameraVideoTrack({
          cameraId: deviceId
        })
        agoraEngine.value?.publish(localVideoTrack.value)
        localVideoTrack.value.play(localPlayerContainer)
      } catch (error: any) {
        showMessage(
          '[Error]: error when change camera manually: ' +
            (error.message || error)
        )
      }
    }

    const onMicrophoneChangeManually = async (deviceId: string) => {
      if (deviceId === devicesManager.value.microphoneId) return
      try {
        await agoraEngine.value?.unpublish(localAudioTrack.value)
        localAudioTrack.value = await AgoraRTC.createMicrophoneAudioTrack({
          microphoneId: deviceId
        })
        agoraEngine.value?.publish(localAudioTrack.value)
      } catch (error: any) {
        showMessage(
          '[Error]: error when change microphone manually: ' +
            (error.message || error)
        )
      }
    }

    const onSpeakerChangeManually = (deviceId: string) => {
      if (deviceId === playbackDevice.value) return
      if (participants.value.length === 0) {
        playbackDevice.value = deviceId
        return
      }
      try {
        participants.value.forEach((participant: any) => {
          participant._audioTrack?.setPlaybackDevice(deviceId)
        })
        playbackDevice.value = deviceId
      } catch (error: any) {
        showMessage(
          '[Error]: error when change speaker manually: ' +
            (error.message || error)
        )
      }
    }

    initialArogaClient()
    join()
    return {
      join,
      onExitCall,
      onCameraClick,
      onMicrophoneClick,
      localVideoState,
      localAudioState,
      snackbar,
      localUser,
      spotlightId,
      participants,
      localSignal,
      remoteSignals,
      onSpeakerChangeManually,
      onMicrophoneChangeManually,
      onCameraChangeManually,
      devicesManager,
      volumnIndicators
    }
  }
})
</script>
<style scoped>
.chat-container {
  height: calc(100vh - 72px) !important;
  width: 100%;
}
</style>
