<template>
  <v-container>
    <v-text-field v-model="options.appId" clearable label="appId" />
    <v-text-field v-model="options.room" clearable label="room" />
    <v-text-field v-model="options.token" clearable label="token" />
    <v-text-field v-model="options.room" clearable label="room" />
    <v-text-field v-model="options.uid" clearable label="uid" />
    <v-btn
      :to="`/room/${options.room}/waiting?uid=${options.uid}&token=${options.token}&appId=${options.appId}`"
      >ENTER WAITING ROOM</v-btn
    >
  </v-container>
</template>
<script lang="ts">
import { defineComponent, reactive } from '@nuxtjs/composition-api'

export default defineComponent({
  setup() {
    const options = reactive({
      appId: 'ba4643f2b3a145f29575b8783d3a5ec1',
      room: 'test',
      uid: '0',
      token:
        '007eJxTYAjN9zmzlFV9xbrQO3O2n1GPjVi0veyAuv9UnTfly85IKGUrMCQlmpiZGKcZJRknGpqYphlZmpqbJlmYWxinGCeapiYbruE/mtwQyMiw8bMqAyMUgvgsDCWpxSUMDAA71h9Y'
    })
    return { options }
  }
})
</script>
