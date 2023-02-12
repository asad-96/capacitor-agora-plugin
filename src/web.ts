import { WebPlugin } from '@capacitor/core'

import { UID } from 'agora-rtc-sdk-ng'
import type { CapacitorPluginAgoraPlugin, IParticipant } from './definitions'

export class CapacitorPluginAgoraWeb
  extends WebPlugin
  implements CapacitorPluginAgoraPlugin
{
  async enterPictureInPictureMode(): Promise<void> {
    this.unavailable()
  }
  async setMediaSource(kind: MediaDeviceKind, deviceId: string): Promise<void> {
    console.log(kind, deviceId)
    this.unavailable()
  }
  async updateParticipantLists(
    participants: IParticipant[]
  ): Promise<IParticipant[]> {
    console.log(participants)
    this.unavailable()
    return participants
  }
  async setSpotlight(participantId: string): Promise<UID> {
    console.log(participantId)
    this.unavailable()
    return participantId
  }
  async showRecordingStatus(isShown: boolean): Promise<void> {
    console.log(isShown)
    this.unavailable()
  }
  async setCountdown(seconds: number): Promise<void> {
    console.log(seconds)
    this.unavailable()
  }
  async joinChannel(options: {
    room: string
    uid: string
    token: string
    appId: string
  }): Promise<string> {
    console.log('joinChannel', options)
    this.unavailable()
    return options.uid
  }

  async leaveChannel() {
    console.log('leaveChannel')
  }
}

const CapacitorPluginAgora = new CapacitorPluginAgoraWeb()

export { CapacitorPluginAgora }
