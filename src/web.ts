import { WebPlugin } from '@capacitor/core'

import { UID } from 'agora-rtc-sdk-ng'
import type { CapacitorPluginAgoraPlugin, IParticipant } from './definitions'

export class CapacitorPluginAgoraWeb
  extends WebPlugin
  implements CapacitorPluginAgoraPlugin
{
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
  async showRecordingStatus(isShow: boolean): Promise<void> {
    console.log(isShow)
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
