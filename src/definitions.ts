import {
  CameraVideoTrackInitConfig,
  ClientConfig,
  IAgoraRTCClient,
  ICameraVideoTrack,
  IMicrophoneAudioTrack,
  MicrophoneAudioTrackInitConfig
} from 'agora-rtc-sdk-ng'

export interface CapacitorPluginAgoraPlugin {
  /**
   * echo input value
   */
  echo(options: { value: string }): Promise<{ value: string }>

  joinChannel(options: {
    room: string
    uid: string
    token: string
    appId: string
  }): Promise<{ room: string; uid: string, token: string,appId: string }>

  leaveChannel(options: { room: string }): Promise<{ room: string }>

  /**
   * createClient input value
   */
  createClient(options: ClientConfig): Promise<IAgoraRTCClient>
  /**
   * createMicrophoneAudioTrack input value
   */
  createMicrophoneAudioTrack(
    config?: MicrophoneAudioTrackInitConfig
  ): Promise<IMicrophoneAudioTrack>

  /**
   * createCameraVideoTrack input value
   */
  createCameraVideoTrack(
    config?: CameraVideoTrackInitConfig
  ): Promise<ICameraVideoTrack>
}
