import { WebPlugin } from '@capacitor/core'

import type { CapacitorPluginAgoraPlugin } from './definitions'
import AgoraRTC, { IAgoraRTCClient, ICameraVideoTrack, CameraVideoTrackInitConfig, MicrophoneAudioTrackInitConfig, IMicrophoneAudioTrack } from 'agora-rtc-sdk-ng'

export class CapacitorPluginAgoraWeb
  extends WebPlugin
  implements CapacitorPluginAgoraPlugin {

  constructor() {
    super(WebPlugin)
    this.agoraEngine = AgoraRTC.createClient({ mode: 'rtc', codec: 'vp8' })
  }

  agoraEngine: IAgoraRTCClient

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options)
    return options
  }

  createClient(): IAgoraRTCClient {
    return this.agoraEngine
  }

  async createMicrophoneAudioTrack(config?: MicrophoneAudioTrackInitConfig): Promise<IMicrophoneAudioTrack> {
    return AgoraRTC.createMicrophoneAudioTrack(config)
  }


  async createCameraVideoTrack(config?: CameraVideoTrackInitConfig): Promise<ICameraVideoTrack> {
    return AgoraRTC.createCameraVideoTrack(config)
  }
}
