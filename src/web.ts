import { WebPlugin } from '@capacitor/core'

import type { CapacitorPluginAgoraPlugin } from './definitions'
import AgoraRTC, { IAgoraRTCClient, ICameraVideoTrack, CameraVideoTrackInitConfig, MicrophoneAudioTrackInitConfig, IMicrophoneAudioTrack } from 'agora-rtc-sdk-ng'

export class CapacitorPluginAgoraWeb
  extends WebPlugin
  implements CapacitorPluginAgoraPlugin {

  constructor() {
    super(WebPlugin)
    this.agoraEngine = AgoraRTC.createClient({ mode: 'rtc', codec: 'vp8' })
    console.log('[web] contructor')
  }

  agoraEngine: IAgoraRTCClient

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('[web] echo - options ', options)
    return options
  }

  async createClient(): Promise<IAgoraRTCClient> {
    console.log('[web] createClient - this.agoraEngine: ', this.agoraEngine)
    return this.agoraEngine
  }

  async createMicrophoneAudioTrack(config?: MicrophoneAudioTrackInitConfig): Promise<IMicrophoneAudioTrack> {
    console.log('[web] createMicrophoneAudioTrack - config:', config)
    return AgoraRTC.createMicrophoneAudioTrack(config)
  }


  async createCameraVideoTrack(config?: CameraVideoTrackInitConfig): Promise<ICameraVideoTrack> {
    console.log('[web] createMicrophoneAudioTrack - config: ', config)
    return AgoraRTC.createCameraVideoTrack(config)
  }
}
