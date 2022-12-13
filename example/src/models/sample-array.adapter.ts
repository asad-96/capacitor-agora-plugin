import { ComputedRef, Ref, ref, watch } from '@nuxtjs/composition-api'

interface ISource {}
interface ITarget {}

export function sampleArrayAdapter(
  source: Ref<ISource[]> | ComputedRef<ISource[]>
) {
  function adapt(source: ISource): ITarget {
    const target = {
      ...source
    }
    return target
  }

  const target = ref<ITarget[]>(source.value.map(adapt))

  watch(source, (newValue) => {
    target.value = newValue.map(adapt)
  })
  return { target, adapt }
}
