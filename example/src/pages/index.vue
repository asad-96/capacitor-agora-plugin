<template>
  <v-container>
    <H1>Hello</H1>
    <p>my name is {{ $t('name') }}</p>
    <h2>Logs:</h2>
    <p v-for="(item, index) in logs" :key="index">{{ item }}</p>
  </v-container>
</template>

<script lang="ts">
import { defineComponent, ref } from '@nuxtjs/composition-api'
import { CapacitorPluginStarter } from 'capacitor-plugin-starter'

export default defineComponent({
  name: 'HomePage',
  setup() {
    const logs = ref(['setup...'])
    if (process.client) {
      logs.value.push('loading client')
      document.addEventListener(
        'deviceready',
        () => {
          logs.value.push('deviceready')
        },
        false
      )
    }
    CapacitorPluginStarter.echo({ value: 'hello' }).then((res) =>
      logs.value.push(res.value)
    )

    return { logs }
  }
})
</script>
