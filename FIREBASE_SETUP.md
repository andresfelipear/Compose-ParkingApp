# Firebase Setup Guide for Vancouver Parking App

This guide will help you set up Firebase Authentication for the Vancouver Parking app.

## Prerequisites

- A Google account
- Android Studio installed
- The Vancouver Parking app project

## Step 1: Create a Firebase Project

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"**
3. Enter your project name (e.g., "Vancouver Parking")
4. Choose whether to enable Google Analytics (recommended)
5. Click **"Create project"**

## Step 2: Register Your Android App

1. In your Firebase project dashboard, click the **Android icon** to add an Android app
2. Enter the package name: `com.aarevalo.parking`
3. Enter an app nickname (optional): "Vancouver Parking"
4. Enter the SHA-1 certificate fingerprint (required for some auth methods):

### Getting SHA-1 Fingerprint

**Debug certificate (for development):**

```bash
# On macOS/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# On Windows
keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
```

**Release certificate (for production):**

```bash
keytool -list -v -keystore your-release-key.keystore -alias your-key-alias
```

5. Click **"Register app"**

## Step 3: Download and Add Configuration File

1. Download the `google-services.json` file
2. Move it to your app module directory:
   ```
   app/google-services.json
   ```
3. The file should be at the same level as your `build.gradle.kts`

## Step 4: Enable Authentication Methods

1. In the Firebase Console, go to **Authentication** > **Sign-in method**
2. Enable **Email/Password** authentication:
   - Click on "Email/Password"
   - Toggle "Enable" to ON
   - Optionally enable "Email link (passwordless sign-in)"
   - Click **"Save"**

### Optional: Enable Additional Auth Providers

You can also enable:
- **Google Sign-In** (requires SHA-1 fingerprint)
- **Phone Authentication**
- **Anonymous Authentication**

## Step 5: Configure Password Reset Email (Optional)

1. Go to **Authentication** > **Templates**
2. Customize the password reset email template
3. Add your app's domain to the authorized domains list

## Step 6: Security Rules (Firestore - if using)

If you plan to use Firestore, set up security rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Parking meters are read-only for authenticated users
    match /parkingMeters/{meterId} {
      allow read: if request.auth != null;
      allow write: if false;
    }
  }
}
```

## Project Structure After Setup

```
app/
├── google-services.json  <- Add this file here
├── build.gradle.kts
└── src/
    └── main/
        └── ...
```

## Verification

After completing the setup:

1. Sync your Gradle files
2. Build the project
3. Run the app
4. Try creating an account on the Sign Up screen
5. Verify you receive the email verification
6. Check the Firebase Console > Authentication > Users to see the registered user

## Troubleshooting

### "Default FirebaseApp is not initialized"

Make sure:
- `google-services.json` is in the `app/` directory
- The `google-services` plugin is applied in `build.gradle.kts`
- You've synced Gradle after adding the file

### "This app is not authorized to use Firebase Authentication"

- Verify your package name matches exactly: `com.aarevalo.parking`
- Check that SHA-1 fingerprint is correctly added in Firebase Console

### "Network error"

- Ensure your device/emulator has internet access
- Check if Firebase services are accessible from your network

### Build Errors

If you encounter dependency conflicts:

```kotlin
// In app/build.gradle.kts, add:
configurations.all {
    resolutionStrategy {
        force("com.google.firebase:firebase-common:20.4.2")
    }
}
```

## Testing Authentication

The app supports the following auth features:

1. **Sign Up** - Create new account with email/password
2. **Sign In** - Login with existing credentials
3. **Password Reset** - Send password reset email
4. **Sign Out** - Logout current user
5. **Email Verification** - Automatic email verification on sign up

## Security Best Practices

1. Never commit `google-services.json` to public repositories
2. Use different Firebase projects for development and production
3. Enable email enumeration protection in Firebase Console
4. Set up proper Firestore security rules
5. Monitor authentication activity in Firebase Console

## Need Help?

- [Firebase Authentication Documentation](https://firebase.google.com/docs/auth)
- [Firebase Android Setup Guide](https://firebase.google.com/docs/android/setup)
- [Firebase Troubleshooting](https://firebase.google.com/support/troubleshooting)
