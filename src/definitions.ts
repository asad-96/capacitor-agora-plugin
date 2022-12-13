export interface CapacitorPluginStarterPlugin {
  /**
   * echo input value
   */
  echo(options: { value: string }): Promise<{ value: string }>
}
