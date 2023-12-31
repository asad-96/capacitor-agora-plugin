// import colors from 'vuetify/es5/util/colors'
import { IconsOptions } from 'vuetify/types/services/icons'
import {
  mdiWeatherSunny,
  mdiWeatherNight,
  mdiAccountCircle,
  mdiArrowLeft,
  mdiClose,
  mdiEmoticon,
  mdiHeart,
  mdiInformation,
  mdiMagnify,
  mdiHome,
  mdiShieldBugOutline,
  mdiHomeOutline,
  mdiShieldBug,
  mdiAccountCircleOutline,
  mdiMore,
  mdiDotsHorizontal,
  mdiDotsHorizontalCircle,
  mdiAccountOutline,
  mdiSettingsHelper,
  mdiMicrophoneOutline,
  mdiMicrophoneOff,
  mdiCameraOutline,
  mdiCameraFlipOutline,
  mdiCameraOff,
  mdiPhoneOff,
  mdiPhoneOutline,
  mdiPhone,
  mdiVolumeHigh,
  mdiVideoOutline,
  mdiTune,
  mdiCellphoneSound,
  mdiCamera,
  mdiCameraFlip,
  mdiSignalCellular1,
  mdiSignalCellular2,
  mdiSignalCellular3,
  mdiSignalCellularOutline,
  mdiSpeaker,
  mdiViewDashboardOutline,
  mdiOrbitVariant,
  mdiMessageText,
  mdiMicrophone,
  mdiChevronLeft,
  mdiCheck,
  mdiCard,
  mdiViewGridOutline,
  mdiViewGalleryOutline,
  mdiSend,
  mdiDotsVertical
} from '@mdi/js'

export default {
  icons: {
    iconfont: 'mdiSvg', // default
    values: {
      phone: mdiPhone,
      phoneOutline: mdiPhoneOutline,
      phoneOff: mdiPhoneOff,
      cameraOff: mdiCameraOff,
      cameraOutline: mdiCameraOutline,
      cameraFlipOutline: mdiCameraFlipOutline,
      microphoneOutline: mdiMicrophoneOutline,
      microphoneOff: mdiMicrophoneOff,
      settingsHelper: mdiSettingsHelper,
      accountOutline: mdiAccountOutline,
      more: mdiMore,
      dotsHorizontal: mdiDotsHorizontal,
      dotsHorizontalOutline: mdiDotsHorizontalCircle,
      accountCircle: mdiAccountCircle,
      accountCircleOutline: mdiAccountCircleOutline,
      arrowLeft: mdiArrowLeft,
      close: mdiClose,
      emoticon: mdiEmoticon,
      shieldBugOutline: mdiShieldBugOutline,
      heart: mdiHeart,
      home: mdiHome,
      shieldBug: mdiShieldBug,
      homeOutline: mdiHomeOutline,
      information: mdiInformation,
      magnify: mdiMagnify,
      weatherNight: mdiWeatherNight,
      weatherSunny: mdiWeatherSunny,
      volumnHigh: mdiVolumeHigh,
      videoOutline: mdiVideoOutline,
      tune: mdiTune,
      cellphoneSound: mdiCellphoneSound,
      camera: mdiCamera,
      cameraFlip: mdiCameraFlip,
      signalLow: mdiSignalCellular1,
      signalMed: mdiSignalCellular2,
      signalHigh: mdiSignalCellular3,
      signalNone: mdiSignalCellularOutline,
      speaker: mdiSpeaker,
      viewDashboardOutline: mdiViewDashboardOutline,
      orbitVariant: mdiOrbitVariant,
      messageText: mdiMessageText,
      microphone: mdiMicrophone,
      chevronLeft: mdiChevronLeft,
      check: mdiCheck,
      card: mdiCard,
      viewGridOutline: mdiViewGridOutline,
      viewGalleryOutline: mdiViewGalleryOutline,
      send: mdiSend,
      dotsVertical: mdiDotsVertical
    }
  } as IconsOptions,
  theme: {
    dark: false,
    default: false,
    disable: false,
    options: {
      customProperties: true
    },
    themes: {
      light: {
        primary: '#009688',
        secondary: '#ff9800',
        accent: '#ff5722',
        error: '#f44336',
        warning: '#ffc107',
        info: '#2196f3',
        success: '#4caf50'
      },
      dark: {
        primary: '#009688',
        secondary: '#ff9800',
        accent: '#ff5722',
        error: '#f44336',
        warning: '#ffc107',
        info: '#2196f3',
        success: '#4caf50'
      }
    }
  }
}
