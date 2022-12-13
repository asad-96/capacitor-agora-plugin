import { getField, updateField } from 'vuex-composition-map-fields'

const state = () => ({})

const getters = {
  getField
}

const mutations = {
  updateField
}

export default {
  namespaced: false,
  strict: true,
  state,
  getters,
  mutations
}
