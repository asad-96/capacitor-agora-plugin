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

  async updateParticipantLists({
    participants
  }: {
    participants: IParticipant[]
  }): Promise<IParticipant[]> {
    console.log(participants)
    this.unavailable()
    return participants
  }

  async setSpotlight({
    participantId
  }: {
    participantId: string
  }): Promise<UID> {
    console.log(participantId)
    this.unavailable()
    return participantId
  }

  async showAlert({ alertText }: { alertText: string }): Promise<void> {
    console.log(alertText)
    this.unavailable()
  }

  async showRecordingStatus({ isShown }: { isShown: boolean }): Promise<void> {
    console.log(isShown)
    this.unavailable()
  }

  async setCountdown({ seconds }: { seconds: number }): Promise<void> {
    console.log(seconds)
    this.unavailable()
  }

  async joinChannel(options: {
    room: string
    uid: UID
    token: string
    appId: string
  }): Promise<string> {
    console.log('joinChannel', options)
    this.unavailable()
    return options.uid.toString()
  }

  async leaveChannel() {
    console.log('leaveChannel')
  }

  async mute(): Promise<void> {
    this.unimplemented()
  }

  async unmute(): Promise<void> {
    this.unimplemented()
  }

  async enableCamera(): Promise<void> {
    this.unimplemented()
  }

  async disableCamera(): Promise<void> {
    this.unimplemented()
  }
}

const CapacitorPluginAgora = new CapacitorPluginAgoraWeb()

export { CapacitorPluginAgora }
