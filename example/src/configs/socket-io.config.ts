export const socketIoConfig = (env) => ({
  sockets: [
    // Required
    {
      // At least one entry is required
      name: 'Video-Conversation',
      url: env.SOCKET_ENDPOINT || 'https://socketio.wellcare.vn',
      default: true
    }
  ]
})
