# Plan: Phone/SMS OTP Verification Feature

## Plan ID: plan-otp-verification-001
## Feature: Phone/SMS OTP verification for login/reset flow
## Priority: P1 (US1 - Resident Login)
## Estimated Effort: 2-4 days

## Plan Description
Add phone/SMS One-Time Password verification as an alternative authentication method for resident login and password reset flows. This enhances accessibility for residents who may not have consistent internet access but can receive SMS messages.

## Plan Components
1. **OTP Service Integration**: Integration with SMS provider (Twilio, Africa's Talking, or similar)
2. **OTP Generation**: Secure OTP generation and validation logic
3. **Login with OTP**: Login flow using OTP instead of password
4. **Password Reset with OTP**: Password reset flow using OTP verification
5. **Resend OTP**: Mechanism to request a new OTP if the first one expires or is missed

## Plan Components Detail

### OTP Service Integration
- Choose SMS provider (Twilio recommended for reliability, Africa's Talking for regional availability)
- Configure API keys and credentials
- Implement API client for OTP requests
- Handle provider fallback on service failure

### OTP Generation
- Secure OTP generation (6-8 digits, time-based or random)
- OTP expiration (typically 5-10 minutes)
- OTP storage (encrypted, tied to resident session)
- OTP rate limiting (prevent brute-force attacks)

### Login with OTP
- Phone number input screen with country code selection
- OTP delivery via SMS upon request
- OTP input screen with auto-focus and keyboard optimization
- Validation of OTP against stored value
- Successful login transition to resident dashboard
- Failure handling with clear error messages and resend option

### Password Reset with OTP
- Forgot password screen with phone number input
- OTP delivery for verification identity
- OTP input and validation
- New password screen with confirmation
- Successful password reset transition to login screen
- Failure handling with clear error messages

### Resend OTP Mechanism
- Timer display showing time until new OTP can be requested
- Resend button with cooldown period (e.g., 30 seconds)
- Accessibility considerations for repeated OTP requests

## Plan Tasks
- T086: Add phone/SMS OTP verification for login/reset flow
- T087: Add password reset with email verification screen

## Dependencies
- Requires SMS provider account and credentials
- Requires phone number validation library
- Requires updated AuthRepository and AuthProvider modules
- Must follow Constitution VII (accessibility) - OTP screens must have contentDescription
- Must follow Constitution IX (compatibility) - must work on min SDK 24 and varying network conditions
- Must follow Constitution IV (environment-based secrets) - SMS credentials must be via Gradle Secrets Plugin

## Acceptance Criteria
- Resident can request OTP via SMS for login
- OTP is delivered within 30 seconds
- OTP validation succeeds with correct code
- Login succeeds with valid OTP
- OTP expires after 10 minutes (configurable)
- Resend OTP is available after 30-second cooldown
- Password reset with OTP works end-to-end
- All OTP screens have proper contentDescription for accessibility
- OTP flows work on varying network conditions (2G, 3G, 4G, offline)
- No OTP tokens stored in plaintext or logged
