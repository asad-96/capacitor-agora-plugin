import { ClientRole, DeviceInfo, NetworkQuality, UID } from 'agora-rtc-sdk-ng'

import { PluginListenerHandle } from '@capacitor/core'

export interface IParticipant {
  _id: string
  name: string
  role: ClientRole
  subtitle: string
  hasJoined?: boolean
  uid?: UID
}

export interface CapacitorPluginAgoraPlugin {
  // METHODS

  /**
   * Join Agora Channel
   */
  joinChannel(options: {
    room: string
    uid: UID
    token: string
    appId: string
  }): Promise<UID>

  /**
   * Leave Agora Channel
   */
  leaveChannel(room?: string): Promise<void>

  /**
   * Update participant lists, including those who are not joined agora channel. Returned participants mapped with their hasJoined status.
   */
  updateParticipantLists(participants: IParticipant[]): Promise<IParticipant[]>

  /**
   * Set a participant to spotlight. Spotlighted participant frame should be highlighted, and place reordered to 1st position in the participant list.
   */
  setSpotlight(participantId: string): Promise<UID>

  /**
   * Show red icon for recording status. This recording is not meant to be performed on frontend.
   */
  showRecordingStatus(isShown: boolean): Promise<void>

  /**
   * Show a countdown. A non-positive value will disable it.
   */
  setCountdown(seconds: number): Promise<void>

  // EVENTS
  /**
   * Local media action events.
   * @event
   */
  addListener(
    eventName:
      | 'onMicrophoneChanged'
      | 'onCameraChanged'
      | 'onPlaybackDeviceChanged',
    listenerFunc: (deviceInfo: DeviceInfo) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle

  /**
   * Participant events
   * @event
   */
  addListener(
    eventName: 'onParticipantAction',
    listenerFunc: (
      participantId: string,
      event:
        | 'nudge'
        | 'call'
        | 'mute'
        | 'unmute'
        | 'enableCamera'
        | 'disableCamera',
      data?: any
    ) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle

  /**
   * Exceptions
   * @event
   */
  addListener(
    eventName: 'exception',
    listenerFunc: (event: { code: number; msg: string; uid: UID }) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle

  /**
   * Network Quality
   * @event
   */
  addListener(
    eventName: 'network-quality',
    listenerFunc: (stats: NetworkQuality) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle
}
