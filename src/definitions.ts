export interface AwsLivenessPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
