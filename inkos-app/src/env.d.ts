/// <reference types="@dcloudio/types" />
/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare module '@dcloudio/uni-app' {
  export const onLaunch: (callback: () => void) => void
  export const onShow: (callback: () => void) => void
  export const onHide: (callback: () => void) => void
}
