# Cloud Messaging Architecture

The application uses a hybrid notification architecture based on **Firebase Cloud Messaging (FCM)** and **Supabase Edge Functions**. Instead of communicating directly with Firebase Cloud Messaging, the Android client sends a notification request to a secure Supabase Edge Function, which is responsible for delivering the notification through FCM.

## Architecture

```text
Sender Device
      │
      │ Notification Request
      ▼
Android Application
      │
      │ HTTP POST
      ▼
Supabase Edge Function
      │
      │ 
      ▼
Firebase Cloud Messaging (FCM)
      │
      ▼
Recipient Device
      │
      ▼
FCMTokenService
      │
      ▼
Android Notification
```

## Workflow

1. The sender performs an action (e.g., sends a chat message).
2. The application retrieves the recipient's FCM token from Firebase Realtime Database.
3. `NotificationSender` sends an HTTP request to the Supabase Edge Function containing the notification information.
4. The Edge Function authenticates with Firebase using the Firebase Admin SDK and forwards the notification to Firebase Cloud Messaging.
5. FCM delivers the notification to the recipient's device.
6. `FCMTokenService` receives the notification, creates the appropriate notification channel, and displays it to the user.

This architecture keeps Firebase credentials secure on the backend while providing a scalable and maintainable notification delivery mechanism.
<img width="394" height="452" alt="image" src="https://github.com/user-attachments/assets/9402a379-06f0-4fab-b591-74c24c1d3491" />   <img width="392" height="156" alt="image" src="https://github.com/user-attachments/assets/2169a56a-fef4-4a3b-8e6b-d5d232143ded" />

