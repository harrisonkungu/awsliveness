import { WebPlugin } from '@capacitor/core';

import type { AwsLivenessPlugin } from './definitions';

export class AwsLivenessWeb extends WebPlugin implements AwsLivenessPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
