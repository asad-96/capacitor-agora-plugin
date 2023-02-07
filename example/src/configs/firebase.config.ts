export const firebaseConfig = {
  customEnv: true,
  config: {
    production: {
      apiKey: 'AIzaSyCp1bVO4c12-8pGS0jRv6_SrG5fbCgEv_c',
      authDomain: 'mhealth-14bf6.firebaseapp.com',
      databaseURL: 'https://mhealth-14bf6.firebaseio.com',
      projectId: 'mhealth-14bf6',
      storageBucket: 'mhealth-14bf6.appspot.com',
      messagingSenderId: '657226244500',
      appId: '1:657226244500:web:6c048fb460e3b9ac9fef81',
      measurementId: 'G-VW3ZYT68SQ'
    },
    sandbox: {
      apiKey: 'AIzaSyCfHhU1zMiXBLE5jMRxzYUjVgDuQp6svtM',
      authDomain: 'staging-a931c.firebaseapp.com',
      databaseURL: 'https://staging-a931c.firebaseio.com',
      projectId: 'staging-a931c',
      storageBucket: 'staging-a931c.appspot.com',
      messagingSenderId: '473530783488',
      appId: '1:473530783488:web:9da01774030069600385e5',
      measurementId: 'G-JZWYLLBTS4'
    }
  },
  services: {
    database: true,
    auth: true
  },
  lazy: true
  // terminateDatabasesAfterGenerate: true
}
