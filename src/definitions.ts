export interface CapacitorPluginStarterPlugin {
  /**
   * echo input value
   */
  echo(options: { value: string }): Promise<{ value: string }>

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
