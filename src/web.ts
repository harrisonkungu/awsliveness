import { WebPlugin } from '@capacitor/core';
import type { AwsLivenessPlugin } from './definitions';

export class AwsLivenessWeb extends WebPlugin implements AwsLivenessPlugin {
  
  async startLivenessCheck(): Promise<any> {
    console.warn('AwsLiveness: Web platform not supported');
    
    // Provide a mock response for web development
    return {
      success: false,
      status: 'WEB_NOT_SUPPORTED',
      error: 'Face Liveness is only supported on native iOS and Android platforms. Please run on a mobile device or emulator.',
      referenceImage: ''
    };
  }

  async checkAvailability(): Promise<{ available: boolean }> {
    console.warn('AwsLiveness: Web platform not supported');
    return { available: false };
  }

 

  // Optional: Add browser-based camera access for web preview
  async startWebCameraCheck(): Promise<any> {
    console.warn('AwsLiveness: Using web camera fallback');
    
    try {
      // Check if browser supports getUserMedia
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        throw new Error('Camera not supported in this browser');
      }

      // Request camera access
      const stream = await navigator.mediaDevices.getUserMedia({ 
        video: { 
          facingMode: 'user',
          width: { ideal: 1280 },
          height: { ideal: 720 }
        } 
      });

      // Create video element
      const video = document.createElement('video');
      video.srcObject = stream;
      video.play();

      // Take a photo after 3 seconds
      return new Promise((resolve) => {
        setTimeout(() => {
          const canvas = document.createElement('canvas');
          canvas.width = video.videoWidth;
          canvas.height = video.videoHeight;
          const ctx = canvas.getContext('2d');
          
          if (ctx) {
            ctx.drawImage(video, 0, 0);
            const imageData = canvas.toDataURL('image/jpeg', 0.8);
            
            // Stop the camera
            stream.getTracks().forEach(track => track.stop());
            
            resolve({
              success: true,
              status: 'WEB_CAMERA_SUCCESS',
              referenceImage: imageData,
              error: undefined
            });
          } else {
            stream.getTracks().forEach(track => track.stop());
            resolve({
              success: false,
              status: 'FAILED',
              error: 'Could not capture image from camera',
              referenceImage: ''
            });
          }
        }, 3000);
      });

    } catch (error: any) {
      return {
        success: false,
        status: 'FAILED',
        error: error.message || 'Failed to access camera',
        referenceImage: ''
      };
    }
  }
}