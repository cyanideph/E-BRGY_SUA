# e-Barangay Sua Android E2E

This suite uses the open-source TesterArmy e2e framework against the real debug APK.

## Run locally

1. Build the APK with ./gradlew :app:assembleDebug
2. Start an Android emulator.
3. Run: cd e2e && npm install && npm run test:android

The config targets the real application id: com.aistudio.ebarangaysua.sjl.

The current suite uses deterministic locators and no AI model, so PR validation does not require an external model API key.

Real Appwrite registration should be added as a separate authenticated E2E scenario once dedicated test credentials are configured in GitHub Actions. Never commit Appwrite passwords or session tokens.
