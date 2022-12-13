<template>
  <v-container>
    <h1>{{ $t('name') }}</h1>
    <h2>config</h2>
    <v-text-field v-model="options.appId" label="appId" />
    <v-text-field v-model="options.channel" label="channel" />
    <v-text-field v-model="options.token" label="token" />
    <v-text-field v-model="options.uid" label="uid" />
    <a href="https://webdemo.agora.io/basicVideoCall/index.html"
      >Agora Web Demo</a
    >
    <v-btn @click="echo">echo</v-btn>
    <v-btn @click="join">join</v-btn>
    <v-btn @click="leave">leave</v-btn>
    <h2>Logs:</h2>
    <p v-for="(item, index) in logs" :key="index">{{ item }}</p>
  </v-container>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from '@nuxtjs/composition-api'
import { CapacitorPluginStarter } from 'capacitor-plugin-starter'
import AgoraRTC from 'agora-rtc-sdk-ng'

export default defineComponent({
  name: 'HomePage',
  setup() {
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
    // Create an instance of the Agora Engine
    const agoraEngine = AgoraRTC.createClient({ mode: 'rtc', codec: 'vp8' })
    // Dynamically create a container in the form of a DIV element to play the remote video track.
    const remotePlayerContainer = document.createElement('div')
    // Dynamically create a container in the form of a DIV element to play the local video track.
    const localPlayerContainer = document.createElement('div')
    const options = reactive({
      // Pass your App ID here.
      appId: 'ba4643f2b3a145f29575b8783d3a5ec1',
      // Set the channel name.
      channel: 'test',
      // Pass your temp token here.
      token:
        '007eJxTYLAxd9leINl4nfvh4tqyZV5v2VYsXVX6P+jvq22uuwX/XM1UYEhKNDEzMU4zSjJONDQxTTOyNDU3TbIwtzBOMU40TU02bI2bkdwQyMjgb+fPxMgAgSA+C0NJanEJAwMApfggUg==',
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
      agoraEngine.on(
        'user-published',
        (user: any, mediaType: 'audio' | 'video') => {
          // Subscribe to the remote user when the SDK triggers the "user-published" event.
          agoraEngine.subscribe(user, mediaType)
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
            remotePlayerContainer.textContent =
              'Remote user ' + user.uid.toString()
            // Append the remote container to the page body.
            document.body.append(remotePlayerContainer)
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
          agoraEngine.on('user-unpublished', (user) => {
            logs.value.push(user.uid + 'has left the channel')
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

    const echo = () => {
      CapacitorPluginStarter.echo({ value: 'echo' }).then((res) =>
        logs.value.push(res.value)
      )
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
        await AgoraRTC.createMicrophoneAudioTrack()
      // Create a local video track from the video captured by a camera.
      channelParameters.localVideoTrack =
        await AgoraRTC.createCameraVideoTrack()
      // Append the local video container to the page body.
      document.body.append(localPlayerContainer)
      // Publish the local audio and video tracks in the channel.
      await agoraEngine.publish([
        channelParameters.localAudioTrack,
        channelParameters.localVideoTrack
      ])
      // Play the local video track.
      channelParameters.localVideoTrack.play(localPlayerContainer)
      logs.value.push('publish success!')

      CapacitorPluginStarter.echo({ value: 'join' }).then((res) =>
        logs.value.push(res.value)
      )
    }

    const leave = () => {
      CapacitorPluginStarter.echo({ value: 'leave' }).then((res) =>
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

    return { logs, echo, channelParameters, options, join, leave }
  }
})
</script>
