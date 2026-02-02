import { registerPlugin } from '@capacitor/core';

import type { AwsLivenessPlugin } from './definitions';

const AwsLiveness = registerPlugin<AwsLivenessPlugin>('AwsLiveness', {
  web: () => import('./web').then((m) => new m.AwsLivenessWeb()),
});

export * from './definitions';
export { AwsLiveness };
