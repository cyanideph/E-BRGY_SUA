package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BarangayRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.example.model.ResidentProfile
import com.example.model.UserRole
import com.example.ui.components.SoftSkeuomorphicCard
import com.example.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var attempted by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val isEmailValid = remember(email) {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    // Single submit path shared by the Sign In button and the password IME "Done" action.
    val doLogin: () -> Unit = {
        attempted = true
        when {
            isLoading -> Unit
            email.isBlank() || password.isBlank() -> {
                errorMessage = "Please enter both your email address and password."
            }
            !isEmailValid -> {
                errorMessage = "Enter a valid email, e.g. name@example.com"
            }
            else -> {
                isLoading = true
                scope.launch {
                    repository.login(email.trim(), password).fold(
                        onSuccess = { isLoading = false; onLoginSuccess() },
                        onFailure = { isLoading = false; errorMessage = it.message ?: "Sign in failed. Please check your credentials." }
                    )
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = CoastalSurface,
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_sua_logo),
                        contentDescription = "Barangay Sua Emblem",
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to e-Barangay Sua",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Sign in to access resident services and official updates",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SoftSkeuomorphicCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        isError = attempted && !isEmailValid,
                        supportingText = {
                            if (attempted && !isEmailValid) {
                                Text("Enter a valid email, e.g. name@example.com")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { doLogin() }),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { doLogin() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New resident in Barangay Sua?",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Register",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var fullName by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var mobile by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    var civilStatus by rememberSaveable { mutableStateOf("") }
    var occupation by rememberSaveable { mutableStateOf("") }
    var emergencyContactName by rememberSaveable { mutableStateOf("") }
    var emergencyContactRelationship by rememberSaveable { mutableStateOf("") }
    var emergencyContactPhone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var latitude by remember { mutableStateOf<Double?>(null) } // restore from GPS on re-entry
    var longitude by remember { mutableStateOf<Double?>(null) } // restore from GPS on re-entry
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var locationMessage by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var civilExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var attempted by rememberSaveable { mutableStateOf(false) }
    var passwordVisible2 by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val hasGpsFix = latitude != null && longitude != null
    val fullNameError = attempted && fullName.isBlank()
    val addressError = attempted && address.isBlank() && !hasGpsFix
    val mobileError = attempted && mobile.filter(Char::isDigit).length !in 10..11
    val dobError = attempted && dateOfBirth.isBlank()
    val civilError = attempted && civilStatus.isBlank()
    val occupationError = attempted && occupation.isBlank()
    val emergencyContactNameError = attempted && emergencyContactName.isBlank()
    val emergencyContactRelationshipError = attempted && emergencyContactRelationship.isBlank()
    val emergencyContactPhoneError = attempted && emergencyContactPhone.isBlank()
    val emailError = attempted && !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val passwordError = attempted && password.length < 8
    val confirmPasswordError = attempted && confirmPassword != password

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!granted) {
            locationMessage = "Location permission was denied. You can enter your registered address instead."
            return@rememberLauncherForActivityResult
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return@rememberLauncherForActivityResult
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    locationMessage = "Current location captured."
                    errorMessage = null
                } else {
                    locationMessage = "Could not get a GPS fix. Please enter your registered address."
                }
            }
            .addOnFailureListener { locationMessage = "Unable to get your current location. Please enter your registered address." }
    }

    val civilStatuses = listOf("Single", "Married", "Widowed", "Separated", "Divorced", "Annulled")

    // Single submit path shared by the Create Account button and the confirm-password IME "Done" action.
    val doRegister: () -> Unit = {
        attempted = true
        if (!isLoading) {
            val hasLocation = latitude != null && longitude != null
            when {
                fullName.isBlank() || mobile.isBlank() || email.isBlank() || password.isBlank() ||
                    confirmPassword.isBlank() || dateOfBirth.isBlank() || civilStatus.isBlank() ||
                    occupation.isBlank() || emergencyContactName.isBlank() ||
                    emergencyContactRelationship.isBlank() || emergencyContactPhone.isBlank() -> {
                    errorMessage = "Please complete all required registration fields."
                }
                address.isBlank() && !hasLocation -> {
                    errorMessage = "Enter your registered address or capture your current GPS location."
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                    errorMessage = "Please enter a valid email address."
                }
                mobile.filter(Char::isDigit).length < 10 -> {
                    errorMessage = "Please enter a valid mobile number."
                }
                password.length < 8 -> {
                    errorMessage = "Password must be at least 8 characters."
                }
                password != confirmPassword -> {
                    errorMessage = "Passwords do not match."
                }
                else -> {
                    isLoading = true
                    scope.launch {
                        repository.register(
                            fullName = fullName.trim(),
                            email = email.trim(),
                            password = password,
                            mobile = mobile.trim(),
                            address = address.trim(),
                            dateOfBirth = dateOfBirth,
                            civilStatus = civilStatus,
                            occupation = occupation.trim(),
                            emergencyContactName = emergencyContactName.trim(),
                            emergencyContactRelationship = emergencyContactRelationship.trim(),
                            emergencyContactPhone = emergencyContactPhone.trim(),
                            latitude = latitude,
                            longitude = longitude
                        ).fold(
                            onSuccess = { isLoading = false; onRegisterSuccess() },
                            onFailure = { isLoading = false; errorMessage = it.message ?: "Registration failed. Please try again." }
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = CoastalSurface,
                shadowElevation = 5.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_sua_logo),
                        contentDescription = "Barangay Sua Emblem",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Resident Registration",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Create your official Barangay Sua resident record",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it; errorMessage = null },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        isError = fullNameError,
                        supportingText = {
                            Text(if (fullNameError) "Full name is required." else "Enter your full legal name")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it; errorMessage = null },
                        label = { Text("Registered Address") },
                        supportingText = {
                            Text(
                                if (addressError) "Enter your registered address or capture your current GPS location."
                                else "Enter your barangay address or capture your current GPS location."
                            )
                        },
                        isError = addressError,
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            locationLauncher.launch(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (latitude != null && longitude != null) "GPS Location Captured" else "Use Current GPS Location")
                    }
                    locationMessage?.let {
                        Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it; errorMessage = null },
                        label = { Text("Mobile Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        isError = mobileError,
                        supportingText = {
                            if (mobileError) Text("Enter a valid 10–11 digit mobile number.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (dateOfBirth.isBlank()) "Select Date of Birth" else "Date of Birth: $dateOfBirth")
                    }
                    if (dobError) {
                        Text(
                            text = "Required.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = civilExpanded,
                        onExpandedChange = { civilExpanded = !civilExpanded }
                    ) {
                        OutlinedTextField(
                            value = civilStatus,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Civil Status") },
                            isError = civilError,
                            supportingText = {
                                if (civilError) Text("Required.")
                            },
                            leadingIcon = { Icon(Icons.Default.FamilyRestroom, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = civilExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = civilExpanded, onDismissRequest = { civilExpanded = false }) {
                            civilStatuses.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = { civilStatus = status; civilExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = occupation,
                        onValueChange = { occupation = it },
                        label = { Text("Occupation") },
                        leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                        isError = occupationError,
                        supportingText = {
                            if (occupationError) Text("Required.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Emergency Contact", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = emergencyContactName,
                        onValueChange = { emergencyContactName = it },
                        label = { Text("Contact Name") },
                        leadingIcon = { Icon(Icons.Default.ContactPhone, contentDescription = null) },
                        isError = emergencyContactNameError,
                        supportingText = {
                            if (emergencyContactNameError) Text("Required.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = emergencyContactRelationship,
                        onValueChange = { emergencyContactRelationship = it },
                        label = { Text("Relationship") },
                        leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                        isError = emergencyContactRelationshipError,
                        supportingText = {
                            if (emergencyContactRelationshipError) Text("Required.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = emergencyContactPhone,
                        onValueChange = { emergencyContactPhone = it },
                        label = { Text("Emergency Contact Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        isError = emergencyContactPhoneError,
                        supportingText = {
                            if (emergencyContactPhoneError) Text("Required.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        isError = emailError,
                        supportingText = {
                            if (emailError) Text("Enter a valid email address.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("Create Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible2 = !passwordVisible2 }) {
                                Icon(
                                    imageVector = if (passwordVisible2) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible2) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible2) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError,
                        supportingText = {
                            if (passwordError) Text("At least 8 characters.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorMessage = null },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                Icon(
                                    imageVector = if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = confirmPasswordError,
                        supportingText = {
                            if (confirmPasswordError) Text("Passwords do not match.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { doRegister() }),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { doRegister() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Create Resident Account", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already registered?", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = onNavigateToLogin) {
                    Text("Sign In", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        dateOfBirth = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)
                            .format(
                                java.time.Instant.ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.of("Asia/Manila"))
                                    .toLocalDate()
                            )
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
