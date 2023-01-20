export const socketIoConfig = (env) => ({
  sockets: [
    // Required
    {
      // At least one entry is required
      name: 'Video-Conference',
      url: env.SOCKET_ENDPOINT || 'https://socketio.wellcare.vn',
      default: true
    }
  ]
})
