<template>
  <div>
    <v-container>
      <h1>{{ $t('name') }}</h1>

      <v-btn @click="drawer = !drawer">Config</v-btn>
      <v-btn @click="join">join</v-btn>
      <v-btn @click="leave">leave</v-btn>
      <v-row>
        <v-col>
          <v-row id="local"></v-row>
          <v-row id="remote"></v-row>
        </v-col>
        <v-col>
          <h3>Logs:</h3>
          <v-divider />
          <p v-for="(item, index) in logs" :key="index">{{ item }}</p>
        </v-col>
      </v-row>
    </v-container>
    <v-navigation-drawer v-model="drawer" absolute temporary right>
      <v-container>
        <h2>CONFIG</h2>
        <v-text-field v-model="options.appId" label="appId" />
        <v-text-field v-model="options.channel" label="channel" />
        <v-text-field v-model="options.token" label="token" />
        <v-text-field v-model="options.uid" label="uid" />
        <a
          href="https://webdemo.agora.io/basicVideoCall/index.html"
          target="_blank"
          >Agora Web Demo</a
        >
      </v-container>
    </v-navigation-drawer>
  </div>
</template>

<script lang="ts">
import {
  defineComponent,
  reactive,
  ref,
  useContext
} from '@nuxtjs/composition-api'
import { CapacitorPluginAgora } from '@wellcare/capacitor-plugin-agora'

export default defineComponent({
  name: 'HomePage',
  setup() {
    const drawer = ref(false)
    const { $config } = useContext()
    const logs = ref(['setup...'])
    if (process.client) {
      logs.value.push('loading client')
      document.addEventListener(
        'deviceready',
        () => {
          logs.value.push('deviceready')
        },
        false
      )
    }
    const options = reactive({
      // Pass your App ID here.
      appId: $config.agora.appId,
      // Set the channel name.
      channel: $config.agora.channel,
      // Pass your temp token here.
      token: $config.agora.token,
      // Set the user ID.
      uid: 0
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

    // Create an instance of the Agora Engine
    const agoraEngine = CapacitorPluginAgora.createClient({
      mode: 'rtc',
      codec: 'vp8'
    })
    console.log('[index] agoraEngine: ', agoraEngine)
    // agoraEngine.on(
    //   'user-published',
    //   (user: any, mediaType: 'audio' | 'video') => {
    //     // Subscribe to the remote user when the SDK triggers the "user-published" event.
    //     agoraEngine.subscribe(user, mediaType)
    //     logs.value.push('subscribe success: ' + mediaType)
    //     // Subscribe and play the remote video in the container If the remote user publishes a video track.
    //     if (mediaType === 'video') {
    //       // Retrieve the remote video track.
    //       channelParameters.remoteVideoTrack = user.videoTrack
    //       // Retrieve the remote audio track.
    //       channelParameters.remoteAudioTrack = user.audioTrack
    //       // Save the remote user id for reuse.
    //       channelParameters.remoteUid = user.uid.toString()
    //       // Specify the ID of the DIV container. You can use the uid of the remote user.
    //       remotePlayerContainer.id = user.uid.toString()
    //       channelParameters.remoteUid = user.uid.toString()
    //       remotePlayerContainer.textContent =
    //         'Remote user ' + user.uid.toString()
    //       // Append the remote container to the page body.
    //       // document.body.append(remotePlayerContainer)
    //       document.getElementById('remote')?.appendChild(remotePlayerContainer)
    //       // Play the remote video track.
    //       channelParameters.remoteVideoTrack.play(remotePlayerContainer)
    //     }
    //     // Subscribe and play the remote audio track If the remote user publishes the audio track only.
    //     if (mediaType === 'audio') {
    //       // Get the RemoteAudioTrack object in the AgoraRTCRemoteUser object.
    //       channelParameters.remoteAudioTrack = user.audioTrack
    //       // Play the remote audio track. No need to pass any DOM element.
    //       channelParameters.remoteAudioTrack.play()
    //     }
    //     // Listen for the "user-unpublished" event.
    //     agoraEngine.on('user-unpublished', (user: any) => {
    //       logs.value.push(user.uid + 'has left the channel')
    //     })
    //   }
    // )
    // Dynamically create a container in the form of a DIV element to play the remote video track.
    const remotePlayerContainer = document.createElement('div')
    // Dynamically create a container in the form of a DIV element to play the local video track.
    const localPlayerContainer = document.createElement('div')

    const startBasicCall = () => {
      // Specify the ID of the DIV container. You can use the uid of the local user.
      localPlayerContainer.id = options.uid.toString()
      // Set the textContent property of the local video container to the local user id.
      localPlayerContainer.textContent = 'Local user ' + options.uid
      // Set the local video container size.
      localPlayerContainer.style.width = '640px'
      localPlayerContainer.style.height = '480px'
      localPlayerContainer.style.padding = '15px 5px 5px 5px'
      // Set the remote video container size.
      remotePlayerContainer.style.width = '640px'
      remotePlayerContainer.style.height = '480px'
      remotePlayerContainer.style.padding = '15px 5px 5px 5px'
      // Listen for the "user-published" event to retrieve a AgoraRTCRemoteUser object.
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
      await agoraEngine.join(
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
      document.getElementById('local')?.appendChild(localPlayerContainer)
      // Publish the local audio and video tracks in the channel.
      await agoraEngine.publish([
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
      agoraEngine.leave()
      logs.value.push('You left the channel')
      // Refresh the page for reuse
      window.location.reload()
    }
    startBasicCall()

    return { logs, channelParameters, options, join, leave, drawer }
  }
})
</script>
