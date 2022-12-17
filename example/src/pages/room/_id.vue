<template>
  <div>
    <w-video-kit
      :auth-id="$route.query.uid"
      :participants="[{ _id: $route.query.uid }]"
      :camera-state="localCameraState"
      :microphone-state="localAudioState"
      @change:active-participant="onActiveParticipantChange"
      @click:exit="leave"
      @click:camera="onCameraClick"
      @click:microphone="onMicrophoneClick"
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
import { CapacitorPluginAgora } from '@wellcare/capacitor-plugin-agora'

export default defineComponent({
  name: 'RoomPage',
  layout: 'plain',
  setup() {
    const snackbar = reactive({
      show: false,
      message: ''
    })
    const showMessage = (message: string) => {
      snackbar.show = true
      snackbar.message = message
    }
    const { $config } = useContext()
    const route = useRoute()
    const router = useRouter()
    const logs = ref(['setup...'])
    const options = reactive({
      // Pass your App ID here.
      appId: $config.agora.appId,
      // Set the channel name.
      channel: route.value.params.id,
      // Pass your temp token here.
      token: $config.agora.token,
      // Set the user ID.
      uid: route.value.query.uid || 0
    })
    const channelParameters: any = reactive({
      // A variable to hold a local audio track.
      localAudioTrack: null,
      // A variable to hold a local video track.
      localVideoTrack: null,
      // A variable to hold a remote audio track.
      remoteAudioTrack: null,
      // A variable to hold a remote video track.
      remoteVideoTrack: null,
      // A variable to hold the remote user id.s
      remoteUid: null
    })

    const localCameraState = computed(
      () => channelParameters?.localVideoTrack?._enabled || false
    )

    const localAudioState = computed(
      () => channelParameters?.localAudioTrack?._enabled || false
    )
    // Dynamically create a container in the form of a DIV element to play the remote video track.
    const remotePlayerContainer = document.createElement('div')
    // Dynamically create a container in the form of a DIV element to play the local video track.
    const localPlayerContainer = document.createElement('div')
    const agoraEngine = ref()
    const startBasicCall = async () => {
      // Specify the ID of the DIV container. You can use the uid of the local user.
      localPlayerContainer.id = options.uid.toString()
      // Set the textContent property of the local video container to the local user id.
      // Set the local video container size.
      localPlayerContainer.style.width = '100%'
      localPlayerContainer.style.height = '100%'
      // Set the remote video container size.
      remotePlayerContainer.style.width = '100%'
      remotePlayerContainer.style.height = '100%'
      // Listen for the "user-published" event to retrieve a AgoraRTCRemoteUser object.
      // Create an instance of the Agora Engine
      agoraEngine.value = await CapacitorPluginAgora.createClient({
        mode: 'rtc',
        codec: 'vp8'
      })
      console.log('[index] agoraEngine: ', agoraEngine)

      agoraEngine.value.on('user-joined', (user: any) => {
        showMessage(`${user.uid.toString()} has joined.`)
      })

      agoraEngine.value.on('user-left', (user: any) => {
        showMessage(`${user.uid.toString()} has left.`)
      })

      agoraEngine.value.on(
        'user-published',
        async (user: any, mediaType: 'audio' | 'video') => {
          // Subscribe to the remote user when the SDK triggers the "user-published" event.
          await agoraEngine.value.subscribe(user, mediaType)
          logs.value.push('subscribe success: ' + mediaType)
          // Subscribe and play the remote video in the container If the remote user publishes a video track.
          if (mediaType === 'video') {
            // Retrieve the remote video track.
            channelParameters.remoteVideoTrack = user.videoTrack
            // Retrieve the remote audio track.
            channelParameters.remoteAudioTrack = user.audioTrack
            // Save the remote user id for reuse.
            channelParameters.remoteUid = user.uid.toString()
            // Specify the ID of the DIV container. You can use the uid of the remote user.
            remotePlayerContainer.id = user.uid.toString()
            channelParameters.remoteUid = user.uid.toString()
            // Append the remote container to the page body.
            // document.body.append(remotePlayerContainer)
            document
              .getElementById('participant_active')
              ?.replaceWith(remotePlayerContainer)
            // Play the remote video track.
            channelParameters.remoteVideoTrack.play(remotePlayerContainer)
          }
          // Subscribe and play the remote audio track If the remote user publishes the audio track only.
          if (mediaType === 'audio') {
            // Get the RemoteAudioTrack object in the AgoraRTCRemoteUser object.
            channelParameters.remoteAudioTrack = user.audioTrack
            // Play the remote audio track. No need to pass any DOM element.
            channelParameters.remoteAudioTrack.play()
          }
          // Listen for the "user-unpublished" event.
          agoraEngine.value.on('user-unpublished', (user: any) => {
            console.log('user-unpublished', user)
          })
        }
      )
    }
    // Remove the video stream from the container.
    const removeVideoDiv = (elementId: string) => {
      logs.value.push('Removing ' + elementId + 'Div')
      const Div = document.getElementById(elementId)
      if (Div) {
        Div.remove()
      }
    }

    const join = async () => {
      // Join a channel.
      await agoraEngine.value.join(
        options.appId,
        options.channel,
        options.token,
        options.uid
      )
      // Create a local audio track from the audio sampled by a microphone.
      channelParameters.localAudioTrack =
        await CapacitorPluginAgora.createMicrophoneAudioTrack()
      // Create a local video track from the video captured by a camera.
      channelParameters.localVideoTrack =
        await CapacitorPluginAgora.createCameraVideoTrack()
      // Append the local video container to the page body.
      // document.body.append(localPlayerContainer)
      document
        .getElementById('participant_me')
        ?.replaceWith(localPlayerContainer)
      // Publish the local audio and video tracks in the channel.
      await agoraEngine.value.publish([
        channelParameters.localAudioTrack,
        channelParameters.localVideoTrack
      ])
      // Play the local video track.
      channelParameters.localVideoTrack.play(localPlayerContainer)
      logs.value.push('publish success!')

      CapacitorPluginAgora.echo({ value: 'join' }).then((res) =>
        logs.value.push(res.value)
      )
    }

    const leave = () => {
      CapacitorPluginAgora.echo({ value: 'leave' }).then((res) =>
        logs.value.push(res.value)
      )
      // Destroy the local audio and video tracks.
      channelParameters.localAudioTrack.close()
      channelParameters.localVideoTrack.close()
      // Remove the containers you created for the local video and remote video.
      removeVideoDiv(remotePlayerContainer.id)
      removeVideoDiv(localPlayerContainer.id)
      // Leave the channel
      agoraEngine.value.leave()
      logs.value.push('You left the channel')
      router.push('/')
      // Refresh the page for reuse
    }

    const onActiveParticipantChange = (uid: string) => {
      console.log('on participant change', uid)
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
      logs,
      channelParameters,
      options,
      join,
      leave,
      onActiveParticipantChange,
      onCameraClick,
      onMicrophoneClick,
      localCameraState,
      localAudioState,
      snackbar
    }
  }
})
</script>
