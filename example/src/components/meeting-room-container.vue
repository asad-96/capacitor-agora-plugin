<template>
  <div>
    <w-video-kit
      :auth-id="authUser._id"
      :active-id="activeId"
      :participants="[
        ...participants,
        { ...authUser, signal: localSignal || 0 }
      ]"
      :camera-state="localCameraState"
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
  computed,
  Ref
} from '@nuxtjs/composition-api'
import { CapacitorPluginAgora } from '@wellcare/capacitor-plugin-agora'
import { IAgoraRTCClient } from 'agora-rtc-sdk-ng'
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
      const mock = {
        avatar: {
          url: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8czLLczg6As4Noqb2sanBsq4n6lf4anQY4g&usqp=CAU'
        },
        role: 'Participant'
      }
      if (!agoraEngine.value) return []
      const result = agoraEngine.value._users.map((u: any) => {
        const user = {
          _id: u.uid.toString(),
          name: u.uid.toString(),
          ...mock,
          ...u,
          signal:
            remoteSignals.value &&
            remoteSignals.value[u.uid] &&
            remoteSignals.value[u.uid].downlinkNetworkQuality
              ? 4 -
                Math.ceil(remoteSignals.value[u.uid].downlinkNetworkQuality / 2)
              : 0
        }
        return user
      })
      return result
    })

    const remoteSignals = ref()
    const localSignal = ref()
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

    const channelParameters: any = reactive({
      localAudioTrack: null,
      localVideoTrack: null,
      remoteAudioTrack: null,
      remoteVideoTrack: null,
      remoteUid: null
    })

    const localCameraState = computed(
      () => channelParameters?.localVideoTrack?._enabled || false
    )

    const localAudioState = computed(
      () => channelParameters?.localAudioTrack?._enabled || false
    )

    const localPlayerContainer = document.createElement('div')
    const agoraEngine: Ref<IAgoraRTCClient> | Ref<any> = ref()
    const startBasicCall = async () => {
      localPlayerContainer.id = options.uid.toString()
      localPlayerContainer.style.width = '100%'
      localPlayerContainer.style.height = '100%'

      agoraEngine.value = await CapacitorPluginAgora.createClient({
        mode: 'rtc',
        codec: 'vp8'
      })

      agoraEngine.value.on('user-joined', (user: any) => {
        showMessage(`${user.uid.toString()} has joined.`)
      })

      agoraEngine.value.on('user-left', (user: any) => {
        showMessage(`${user.uid.toString()} has left.`)
      })

      agoraEngine.value.on(
        'user-published',
        async (user: any, mediaType: 'audio' | 'video') => {
          await agoraEngine.value.subscribe(user, mediaType)
          const remoteUser = agoraEngine.value._users.find(
            (u: any) => u.uid.toString() === user.uid.toString()
          )
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

            remoteUser._videoTrack.play(remotePlayerContainer)
          }
          if (mediaType === 'audio') {
            remoteUser._audioTrack.play()
          }
          remoteUser._audio_muted_ = true
        }
      )
    }

    const join = async () => {
      await agoraEngine.value.join(
        options.appId,
        options.channel,
        options.token,
        authUser.value._id
      )

      channelParameters.localAudioTrack =
        await CapacitorPluginAgora.createMicrophoneAudioTrack()
      channelParameters.localVideoTrack =
        await CapacitorPluginAgora.createCameraVideoTrack()

      document
        .getElementById('participant_me')
        ?.querySelector('.image-placeholder')
        ?.remove()

      document
        .getElementById('participant_me')
        ?.appendChild(localPlayerContainer)

      await agoraEngine.value.publish([
        channelParameters.localAudioTrack,
        channelParameters.localVideoTrack
      ])

      channelParameters.localVideoTrack.play(localPlayerContainer)
      channelParameters.localAudioTrack.setEnabled(false)
      agoraEngine.value.on('network-quality', (stats: any) => {
        localSignal.value = stats.downlinkNetworkQuality
          ? 4 - Math.ceil(stats.downlinkNetworkQuality / 2)
          : 0
        remoteSignals.value = agoraEngine.value.getRemoteNetworkQuality()
      })
    }

    const leave = () => {
      channelParameters.localAudioTrack.close()
      channelParameters.localVideoTrack.close()
      agoraEngine.value.leave()
      router.push('/')
    }

    const onActiveParticipantChange = (uid: string) => {
      activeId.value = uid
    }

    const onCameraClick = () => {
      if (channelParameters.localVideoTrack._enabled) {
        channelParameters.localVideoTrack.setEnabled(false)
      } else channelParameters.localVideoTrack.setEnabled(true)
    }

    const onMicrophoneClick = () => {
      if (channelParameters.localAudioTrack._enabled) {
        channelParameters.localAudioTrack.setEnabled(false)
      } else channelParameters.localAudioTrack.setEnabled(true)
    }

    startBasicCall().then(() => join())

    return {
      channelParameters,
      options,
      join,
      leave,
      onActiveParticipantChange,
      onCameraClick,
      onMicrophoneClick,
      localCameraState,
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
