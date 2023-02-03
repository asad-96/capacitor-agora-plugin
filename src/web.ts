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
  async joinChannel(options: {
    room: string
    uid: string
    token: string
    appId: string
  }): Promise<{ room: string; uid: string, token: string, appId: string }> {
    return options
  }

  async leaveChannel(options: { room: string }): Promise<{ room: string }> {
    return options
  }

  async createClient(config: ClientConfig): Promise<IAgoraRTCClient> {
    return AgoraRTC.createClient(config)
  }

  async createMicrophoneAudioTrack(
    config?: MicrophoneAudioTrackInitConfig
  ): Promise<IMicrophoneAudioTrack> {
    return AgoraRTC.createMicrophoneAudioTrack(config)
  }

  async createCameraVideoTrack(
    config?: CameraVideoTrackInitConfig
  ): Promise<ICameraVideoTrack> {
    return AgoraRTC.createCameraVideoTrack(config)
  }
}

const CapacitorPluginAgora = new CapacitorPluginAgoraWeb()

export { CapacitorPluginAgora }
