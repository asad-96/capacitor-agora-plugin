import { WebPlugin } from '@capacitor/core'

import AgoraRTC, {
  IAgoraRTCClient,
  ICameraVideoTrack,
  CameraVideoTrackInitConfig,
  MicrophoneAudioTrackInitConfig,
  IMicrophoneAudioTrack,
  ClientConfig
} from 'agora-rtc-sdk-ng'
import type { CapacitorPluginAgoraPlugin } from './definitions'

export class CapacitorPluginAgoraWeb
  extends WebPlugin
  implements CapacitorPluginAgoraPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('[web] echo - options ', options)
    return options
  }

  async joinChannel(options: {
    room: string
    uid: string
  }): Promise<{ room: string; uid: string }> {
    console.log('joinChannel', options)
    return options
  }

  async leaveChannel(options: { room: string }): Promise<{ room: string }> {
    console.log('leaveChannel', options)
    return options
  }

  async createClient(config: ClientConfig): Promise<IAgoraRTCClient> {
    console.log('[web] createClient - config:', config)
    return AgoraRTC.createClient(config)
  }

  async createMicrophoneAudioTrack(
    config?: MicrophoneAudioTrackInitConfig
  ): Promise<IMicrophoneAudioTrack> {
    console.log('[web] createMicrophoneAudioTrack - config:', config)
    return AgoraRTC.createMicrophoneAudioTrack(config)
  }

  async createCameraVideoTrack(
    config?: CameraVideoTrackInitConfig
  ): Promise<ICameraVideoTrack> {
    console.log('[web] createMicrophoneAudioTrack - config: ', config)
    return AgoraRTC.createCameraVideoTrack(config)
  }
}

const CapacitorPluginAgora = new CapacitorPluginAgoraWeb()

export { CapacitorPluginAgora }
