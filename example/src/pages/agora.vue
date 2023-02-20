<template>
  <div v-if="shouldShowConsult">
    benh an
    {{ meetingConfig }}
    <v-row>
      <v-col><v-btn @click="onOpenChat">open chat</v-btn></v-col>
      <v-col><v-btn @click="onOpenVideoCall">open video call</v-btn></v-col>
      <v-col
        ><v-btn @click="onOpenVideoCall"
          >open video call with meeting state</v-btn
        ></v-col
      >
    </v-row>
  </div>
  <div v-else-if="shouldShowChat">
    chat
    {{ meetingConfig }}
    <v-col><v-btn @click="onOpenVideoCall">open video call</v-btn></v-col>
    <v-col><v-btn @click="onOpenConsult">open consult</v-btn></v-col>
    <v-col
      ><v-btn @click="onOpenVideoCall"
        >open video call with meeting state</v-btn
      ></v-col
    >
  </div>

  <w-video-call-container
    v-else-if="shouldShowVideoCall"
    :auth-user="authUser"
    :app-id="meetingConfig.appId"
    :participants="participants"
    :room-status="meetingConfig.roomStatus"
    :role="meetingConfig.role"
    :room="meetingConfig.room"
    :token="meetingConfig.token"
    @action:chat="onOpenChat"
    @action:leaved="onOpenConsult"
  ></w-video-call-container>
</template>
<script lang="ts">
import {
  defineComponent,
  ref,
  computed,
  useRoute
} from '@nuxtjs/composition-api'
export default defineComponent({
  layout: 'waiting-room',
  setup() {
    const authUser = computed(() => ({
      _id: route.value.query.uid?.toString(),
      name: route.value.query.uid?.toString(),
      avatar: {
        url: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdEM3WslIvUN-HVSvP06Y6JhISiVw5DGb2VQ&usqp=CAU'
      },
      role: route.value.query.role?.toString()
    }))
    const participants = [
      {
        _id: '2',
        name: 'Hoa',
        avatar: {
          url: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRffyTDh4fWE7u1Am9reNoPw7m9vG7EYv489A&usqp=CAU'
        },
        role: 'Doctor'
      }
    ]
    const route = useRoute()
    const meetingConfig = computed(() => {
      const decodeToken = decodeURIComponent(
        route.value.query.token?.toString()
      )
      return {
        appId: route.value.query.appId?.toString(),
        room: route.value.query.room?.toString(),
        token: decodeToken,
        role: route.value.query.role?.toString(),
        roomStatus: route.value.query.roomStatus?.toString()
      }
    })

    const context = ref<'CONSULT' | 'CHAT' | 'CALL'>('CONSULT')
    const shouldShowChat = computed(() => context.value === 'CHAT')
    const shouldShowVideoCall = computed(() => context.value === 'CALL')
    const shouldShowConsult = computed(() => context.value === 'CONSULT')
    const onOpenChat = () => (context.value = 'CHAT')
    const onOpenVideoCall = () => (context.value = 'CALL')
    const onOpenConsult = () => (context.value = 'CONSULT')
    return {
      authUser,
      participants,
      shouldShowChat,
      shouldShowVideoCall,
      shouldShowConsult,
      context,
      onOpenVideoCall,
      onOpenChat,
      onOpenConsult,
      meetingConfig
    }
  }
})
</script>
