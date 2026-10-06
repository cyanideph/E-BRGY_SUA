import { expect, test } from 'e2e';

test('fresh launch shows resident sign-in', async ({ app, screen }) => {
  await app.clearState();
  await app.open();
  await expect(screen.getByText('Welcome to e-Barangay Sua')).toBeVisible();
  await expect(screen.getByText('Email Address')).toBeVisible();
  await expect(screen.getByText('Password')).toBeVisible();
  await expect(screen.getByText('Sign In')).toBeVisible();
  await expect(screen.getByText('Register')).toBeVisible();
});

test('sign-in validates required credentials', async ({ app, screen }) => {
  await app.clearState();
  await app.open();
  await screen.getByText('Sign In').tap();
  await expect(screen.getByText('Please enter both your email address and password.')).toBeVisible();
});

test('registration exposes the complete resident profile form', async ({ app, screen }) => {
  await app.clearState();
  await app.open();
  await screen.getByText('Register').tap();
  await expect(screen.getByText('Resident Registration')).toBeVisible();
  await expect(screen.getByText('Full Name')).toBeVisible();
  await expect(screen.getByText('Registered Address')).toBeVisible();
  await expect(screen.getByText('Use Current GPS Location')).toBeVisible();
  await expect(screen.getByText('Mobile Number')).toBeVisible();
  await expect(screen.getByText('Select Date of Birth')).toBeVisible();
  await expect(screen.getByText('Civil Status')).toBeVisible();
  await expect(screen.getByText('Occupation')).toBeVisible();
  await expect(screen.getByText('Emergency Contact')).toBeVisible();
  await expect(screen.getByText('Contact Name')).toBeVisible();
  await expect(screen.getByText('Relationship')).toBeVisible();
  await expect(screen.getByText('Emergency Contact Number')).toBeVisible();
  await expect(screen.getByText('Email Address')).toBeVisible();
  await expect(screen.getByText('Create Password')).toBeVisible();
  await expect(screen.getByText('Confirm Password')).toBeVisible();
  await expect(screen.getByText('Create Resident Account')).toBeVisible();
});

test('registration validates required fields before touching Appwrite', async ({ app, screen }) => {
  await app.clearState();
  await app.open();
  await screen.getByText('Register').tap();
  await screen.getByText('Create Resident Account').tap();
  await expect(screen.getByText('Please complete all required registration fields.')).toBeVisible();
});
