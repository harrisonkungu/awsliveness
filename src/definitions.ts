export interface AwsLivenessPlugin {
  startLivenessCheck(options: { 
    sessionId: string; 
    region?: string;
  }): Promise<{ 
    success: boolean; 
    status?: string;
    error?: string;
    referenceImage?: string;
  }>;
  
  checkAvailability(): Promise<{ available: boolean; }>;
}