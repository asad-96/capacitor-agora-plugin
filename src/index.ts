import { registerPlugin } from '@capacitor/core'

import type { CapacitorPluginAgoraPlugin } from './definitions'

const CapacitorPluginAgora = registerPlugin<CapacitorPluginAgoraPlugin>(
  'CapacitorPluginAgora',
  {
    web: () => import('./web').then((m) => new m.CapacitorPluginAgoraWeb())
  }
)

export * from './definitions'
export { CapacitorPluginAgora }
