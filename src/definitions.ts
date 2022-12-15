import { CameraVideoTrackInitConfig, ClientConfig, IAgoraRTCClient, ICameraVideoTrack, IMicrophoneAudioTrack, MicrophoneAudioTrackInitConfig } from "agora-rtc-sdk-ng"

export interface CapacitorPluginAgoraPlugin {
  /**
   * echo input value
   */
  echo(options: { value: string }): Promise<{ value: string }>

  /**
 * createClient input value
 */
  createClient(options: ClientConfig): IAgoraRTCClient
  /**
* createMicrophoneAudioTrack input value
*/
  createMicrophoneAudioTrack(config?: MicrophoneAudioTrackInitConfig): Promise<IMicrophoneAudioTrack>

  /**
* createCameraVideoTrack input value
*/
  createCameraVideoTrack(config?: CameraVideoTrackInitConfig): Promise<ICameraVideoTrack>

  // /**
  //  * To implement Video Calling, you use Video SDK to create an Agora Engine instance
  //  */
  // setupVideoSDKEngine(options: { value: string }): Promise<{ value: string }>

  // /**
  //  * registerEventHandler
  //  */
  // registerEventHandler(options: { value: string }): Promise<{ value: string }>

  // /**
  //  * initialize
  //  */
  // initialize(options: { value: string }): Promise<{ value: string }>

  //   /**
  //  * enableVideo
  //  */
  // enableVideo(options: { value: string }): Promise<{ value: string }>
}
