# Plan: OAuth Login Feature

## Plan ID: plan-oauth-login-001
## Feature: OAuth Login (Google, GitHub) alongside resident login
## Priority: P1 (US1 - Resident Login)
## Estimated Effort: 3-5 days

## Plan Description
Add OAuth authentication providers (Google, GitHub) alongside the existing resident login flow. This enhances accessibility and provides residents with convenient authentication options while maintaining the existing Appwrite-based authentication system.

## Plan Components
1. **Google OAuth Provider**: Integration with Google Sign-In API
2. **GitHub OAuth Provider**: Integration with GitHub authentication
3. **Login Flow Management**: Coordination between resident login and OAuth providers
4. **User Profile Sync**: Sync OAuth profile data with resident model
5. **Session Management**: Consistent session handling across auth methods

## Plan Components Detail

### Google OAuth Integration
- Configure OAuth client ID from Google Cloud Console
- Implement redirect URI handling
- Extract user profile (name, email, profile picture)
- Auto-create or link resident account with Google ID

### GitHub OAuth Integration
- Configure OAuth client ID from GitHub Developer Settings
- Implement redirect URI handling  
- Extract user profile (name, email, public repo info)
- Auto-create or link resident account with GitHub ID

### Login Flow Management
- Display authentication method selection (Resident, Google, GitHub)
- Handle existing account linking
- Maintain login state across app restarts
- Provide fallback to resident login if OAuth fails

### User Profile Sync
- Map OAuth profile fields to resident model fields
- Update resident name, photo, email from OAuth data
- Preserve resident-specific data (household, location, registration status)
- Handle profile changes across auth methods

### Session Management
- Consistent token storage and refresh
- Logout from all sessions option
- Remember me functionality
- Cross-device session restoration

## Plan Tasks
- T085: Implement OAuth login (Google, GitHub) alongside resident login
- T086: Add phone/SMS OTP verification for login/reset flow
- T087: Add password reset with email verification screen

## Dependencies
- Requires Google Cloud Console OAuth configuration
- Requires GitHub Developer Settings OAuth configuration
- Requires updated AuthRepository and AuthProvider modules
- Must follow Constitution III (Appwrite as exclusive backend) - OAuth is supplementary
- Must follow Constitution VII (accessibility) - all auth flows must have contentDescription
- Must follow Constitution IX (compatibility) - must work on min SDK 24

## Acceptance Criteria
- Resident login continues to work as before
- Google Sign-In successfully authenticates and creates/links resident account
- GitHub Sign-In successfully authenticates and creates/links resident account
- User profile data is correctly synced from OAuth providers
- Session state is consistent across app restarts
- Fallback to resident login works when OAuth services are unavailable
- All auth screens have proper contentDescription for accessibility
- Authentication flows work on min SDK 24 devices
