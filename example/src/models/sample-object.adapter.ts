import { ComputedRef, Ref, ref, watch } from '@nuxtjs/composition-api'

interface ISource {}
interface ITarget {}

export function sampleObjectAdapter(
  source: Ref<ISource> | ComputedRef<ISource>
) {
  function adapt(source: ISource): ITarget {
    const target = {
      ...source
    }
    return target
  }

  const target = ref<ITarget>(adapt(source))

  watch(source, (newValue) => {
    target.value = adapt(newValue)
  })
  return { target, adapt }
}
