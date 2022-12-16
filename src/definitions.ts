import { CameraVideoTrackInitConfig, ClientConfig, IAgoraRTCClient, ICameraVideoTrack, IMicrophoneAudioTrack, MicrophoneAudioTrackInitConfig } from "agora-rtc-sdk-ng"

export interface CapacitorPluginAgoraPlugin {
  /**
   * echo input value
   */
  echo(options: { value: string }): Promise<{ value: string }>

  /**
 * createClient input value
 */
  createClient(options: ClientConfig): Promise<IAgoraRTCClient>
  /**
* createMicrophoneAudioTrack input value
*/
  createMicrophoneAudioTrack(config?: MicrophoneAudioTrackInitConfig): Promise<IMicrophoneAudioTrack>

  /**
* createCameraVideoTrack input value
*/
  createCameraVideoTrack(config?: CameraVideoTrackInitConfig): Promise<ICameraVideoTrack>
}
