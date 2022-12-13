// https://github.com/shakee93/vue-toasted#nuxt--officially-uses-vue-toasted-for-their-toast-module
export const toastConfig = {
  position: 'top-center',
  // iconPack: 'material',
  singleton: true,
  keepOnHover: true,
  theme: 'outline',
  icon: false,
  closeOnSwipe: true,
  className: 'w-toast',
  register: [
    // Register custom toasts
    {
      name: 'appError',
      message: (payload: any) => {
        // if there is a message show it with the message
        return payload?.message || 'Đã có lỗi xảy ra'
      },
      options: {
        type: 'error',
        duration: 2500
        // icon: {
        //   name: 'info'
        // }
      }
    },
    {
      name: 'appWarning',
      message: (payload: any) => {
        // if there is a message show it with the message
        return payload?.message || 'Đã có lỗi xảy ra'
      },
      options: {
        type: 'warning',
        duration: 1500,
        className: ['w-toast', 'w-warning-toast']
        // icon: {
        //   icon : "<svg width='100' height='100'><circle cx='50' cy='50' r='40' stroke='green' stroke-width='4' fill='yellow' /></svg>"
        // }
      }
    },
    {
      name: 'appInfo',
      message: (payload: any) => {
        // if there is a message show it with the message
        return payload?.message || 'Đã có lỗi xảy ra'
      },
      options: {
        type: 'success',
        duration: 2500,
        className: ['w-toast', 'w-info-toast']
        // icon: {
        //   name: 'info'
        // }
      }
    }
  ]
}
