import { WebPlugin } from '@capacitor/core'

import type { CapacitorPluginAgoraPlugin } from './definitions'

export class CapacitorPluginAgoraWeb
  extends WebPlugin
  implements CapacitorPluginAgoraPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    await console.log('ECHO', options)
    return options
  }
}
