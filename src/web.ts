import { WebPlugin } from '@capacitor/core'

import type { CapacitorPluginAgoraPlugin } from './definitions'
import AgoraRTC, { IAgoraRTCClient, ICameraVideoTrack, CameraVideoTrackInitConfig, ClientConfig, MicrophoneAudioTrackInitConfig, IMicrophoneAudioTrack } from 'agora-rtc-sdk-ng'

export class CapacitorPluginAgoraWeb
  extends WebPlugin
  implements CapacitorPluginAgoraPlugin {

  constructor() {
    super(WebPlugin)
  }

  agoraEngine: any

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options)
    return options
  }

  async createClient(options: ClientConfig): Promise<IAgoraRTCClient> {
    this.agoraEngine = AgoraRTC.createClient(options)
    return this.agoraEngine
  }

  async createMicrophoneAudioTrack(config?: MicrophoneAudioTrackInitConfig): Promise<IMicrophoneAudioTrack> {
    return AgoraRTC.createMicrophoneAudioTrack(config)
  }


  async createCameraVideoTrack(config?: CameraVideoTrackInitConfig): Promise<ICameraVideoTrack> {
    return AgoraRTC.createCameraVideoTrack(config)
  }
}
