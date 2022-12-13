import { registerPlugin } from '@capacitor/core'

import type { CapacitorPluginStarterPlugin } from './definitions'

const CapacitorPluginStarter = registerPlugin<CapacitorPluginStarterPlugin>(
  'CapacitorPluginStarter',
  {
    web: () => import('./web').then((m) => new m.CapacitorPluginStarterWeb())
  }
)

export * from './definitions'
export { CapacitorPluginStarter }
