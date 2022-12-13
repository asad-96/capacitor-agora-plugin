import { WebPlugin } from '@capacitor/core'

import type { CapacitorPluginStarterPlugin } from './definitions'

export class CapacitorPluginStarterWeb
  extends WebPlugin
  implements CapacitorPluginStarterPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    await console.log('ECHO', options)
    return options
  }
}
