import type { E2EConfig } from 'e2e';
import { mobile } from '@e2e-dev/mobile';

export default {
  targets: [{
    name: 'android',
    engine: mobile({ platform: 'android' }),
    app: {
      bundleId: 'com.aistudio.ebarangaysua.sjl',
      appPath: '../app/build/outputs/apk/debug/app-debug.apk',
    },
  }],
  workers: 1,
} satisfies E2EConfig;
