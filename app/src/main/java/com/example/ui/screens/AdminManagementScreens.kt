package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BarangayRepository
import com.example.model.*
import com.example.ui.components.PriorityBadge
import com.example.ui.components.RequestStatusBadge
import com.example.ui.components.SoftSkeuomorphicCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 1. REQUEST MANAGEMENT
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRequestsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRequestDetails: (String) -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val requests by repository.requests.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "Submitted", "Under Review", "Processing", "Ready", "Completed")

    val filteredList = remember(requests, searchQuery, selectedFilter) {
        requests.filter { req ->
            val matchStatus = when (selectedFilter) {
                "Submitted" -> req.status == RequestStatus.SUBMITTED
                "Under Review" -> req.status == RequestStatus.UNDER_REVIEW
                "Processing" -> req.status == RequestStatus.PROCESSING
                "Ready" -> req.status == RequestStatus.READY
                "Completed" -> req.status == RequestStatus.COMPLETED
                else -> true
            }
            val matchSearch = req.referenceNumber.contains(searchQuery, ignoreCase = true) ||
                    req.residentName.contains(searchQuery, ignoreCase = true) ||
                    req.serviceName.contains(searchQuery, ignoreCase = true)
            matchStatus && matchSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepOceanBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by reference # or resident name...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                ScrollableTabRow(
                    selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                    edgePadding = 0.dp,
                    divider = {},
                    containerColor = Color.Transparent
                ) {
                    filters.forEach { filter ->
                        val isSelected = filter == selectedFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepOceanBlue,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { req ->
                    SoftSkeuomorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigateToRequestDetails(req.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(req.referenceNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SouthernSeaTealDark)
                            RequestStatusBadge(status = req.status)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(req.serviceName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Applicant: ${req.residentName} (${req.residentAddress})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (req.officialRemarks.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Note: ${req.officialRemarks}", fontSize = 11.sp, color = DeepNavySecondary)
                        }
                    }
                }
            }
        }
    }
}

// 2. RESIDENT MANAGEMENT (Staff/Admin Protected)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminResidentsScreen(
    onNavigateBack: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val residents by repository.residents.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(residents, searchQuery) {
        residents.filter {
            it.fullName.contains(searchQuery, ignoreCase = true) ||
                    it.residentId.contains(searchQuery, ignoreCase = true) ||
                    it.address.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resident Registry", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepOceanBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search resident by name, ID, or purok...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = WarmSunGoldContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = OnWarmSunGoldContainer, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confidential LGU Registry: Authorized Personnel Only (RA 10173).",
                        fontSize = 11.sp,
                        color = OnWarmSunGoldContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { res ->
                    SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = DeepOceanContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = DeepOceanBlue)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(res.fullName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("${res.residentId} • ${res.civilStatus}", fontSize = 12.sp, color = SouthernSeaTealDark)
                                Text(res.address, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Contact: ${res.mobileNumber}", fontSize = 11.sp, color = DeepNavySecondary)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NaturalGreenContainer
                            ) {
                                Text(
                                    text = res.registrationStatus,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. HOUSEHOLD MANAGEMENT
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHouseholdsScreen(
    onNavigateBack: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val households by repository.households.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Household Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepOceanBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(households) { hh ->
                SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(hh.householdNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepOceanBlue)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SouthernSeaTealContainer
                        ) {
                            Text("${hh.memberCount} Members", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SouthernSeaTealDark, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Head: ${hh.headName}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Address: ${hh.address}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Members: ${hh.memberNames.joinToString(", ")}", fontSize = 11.sp, color = DeepNavySecondary)
                    if (hh.emergencyNotes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Disaster Note: ${hh.emergencyNotes}", fontSize = 11.sp, color = WarmAmber, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

// 4. ANNOUNCEMENT MANAGEMENT
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnnouncementsScreen(
    onNavigateBack: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val scope = rememberCoroutineScope()
    val announcements by repository.announcements.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(AnnouncementCategory.GENERAL) }
    var priority by remember { mutableStateOf(AnnouncementPriority.NORMAL) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Announcements", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepOceanBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = DeepOceanBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Publish Announcement")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(announcements) { ann ->
                SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PriorityBadge(priority = ann.priority)
                        Text(ann.publishedDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(ann.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(ann.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Author: ${ann.authorName} (${ann.authorRole})", fontSize = 11.sp, color = SouthernSeaTealDark)
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Publish New Announcement", fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Announcement Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Advisory Details") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Priority:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row {
                            AnnouncementPriority.values().forEach { p ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    RadioButton(selected = (priority == p), onClick = { priority = p })
                                    Text(p.displayName, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank() && desc.isNotBlank()) {
                                scope.launch {
                                    runCatching {
                                        repository.publishAnnouncement(
                                            title = title,
                                            description = desc,
                                            category = category,
                                            priority = priority,
                                            isPinned = (priority == AnnouncementPriority.EMERGENCY)
                                        )
                                    }.onSuccess {
                                        showCreateDialog = false
                                        title = ""
                                        desc = ""
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Publish")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// 5. EVENT MANAGEMENT
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEventsScreen(
    onNavigateBack: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val scope = rememberCoroutineScope()
    val events by repository.events.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Events", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepOceanBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = DeepOceanBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(events) { ev ->
                SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ev.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("${ev.rsvpCount} Attending", fontSize = 11.sp, color = SouthernSeaTealDark, fontWeight = FontWeight.Bold)
                    }
                    Text("${ev.date} • ${ev.time}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Location: ${ev.location}", fontSize = 11.sp, color = DeepNavySecondary)
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Schedule Barangay Event", fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Event Title") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank() && date.isNotBlank() && time.isNotBlank() && location.isNotBlank()) {
                                scope.launch {
                                    runCatching {
                                        repository.createEvent(
                                            title = title,
                                            description = desc,
                                            date = date,
                                            time = time,
                                            location = location,
                                            organizer = repository.currentUser.value.profile.fullName,
                                            category = "Community"
                                        )
                                    }.onSuccess {
                                        showCreateDialog = false
                                        title = ""
                                        desc = ""
                                        date = ""
                                        time = ""
                                        location = ""
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Save Event")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// 6. EMERGENCY MANAGEMENT (SOS)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEmergenciesScreen(
    onNavigateBack: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val scope = rememberCoroutineScope()
    val emergencies by repository.emergencyReports.collectAsState()

    var selectedReport by remember { mutableStateOf<EmergencyReport?>(null) }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency SOS Dispatch", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RestrainedCoralRed,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(emergencies) { emg ->
                val statusColor = when (emg.status) {
                    EmergencyStatus.RECEIVED -> RestrainedCoralRed
                    EmergencyStatus.RESPONDING -> WarmAmber
                    EmergencyStatus.RESOLVED -> NaturalGreen
                }

                SoftSkeuomorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = statusColor.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emg.type.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = statusColor)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = statusColor.copy(alpha = 0.15f)
                        ) {
                            Text(emg.status.label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(emg.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Caller: ${emg.residentName} (${emg.residentContact})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Location: ${emg.locationDescription}", fontSize = 11.sp, color = SouthernSeaTealDark)
                    if (emg.responseNotes.isNotEmpty()) {
                        Text("Responder Note: ${emg.responseNotes}", fontSize = 11.sp, color = DeepNavySecondary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (emg.status == EmergencyStatus.RECEIVED) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        runCatching {
                                            repository.updateEmergencyStatus(emg.id, EmergencyStatus.RESPONDING, "", "")
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber)
                            ) {
                                Text("Dispatch Response", fontSize = 12.sp)
                            }
                        } else if (emg.status == EmergencyStatus.RESPONDING) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        runCatching {
                                            repository.updateEmergencyStatus(emg.id, EmergencyStatus.RESOLVED, emg.assignedResponder, "")
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NaturalGreen)
                            ) {
                                Text("Mark Resolved", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 7. REPORTS & ANALYTICS
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportsScreen(
    onNavigateBack: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val requests by repository.requests.collectAsState()
    val residents by repository.residents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Barangay LGU Reports", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepOceanBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Quarterly Public Service Summary", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeepOceanBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total Document Applications: ${requests.size}", fontSize = 13.sp)
                Text("Completed & Released: ${requests.count { it.status == RequestStatus.COMPLETED }}", fontSize = 13.sp)
                Text("Pending Action: ${requests.count { it.status != RequestStatus.COMPLETED && it.status != RequestStatus.REJECTED }}", fontSize = 13.sp)
                Text("Average Processing Duration: 1.4 Working Days", fontSize = 13.sp)
            }

            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Purok Demographic Distribution", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeepOceanBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Purok 1 (Beachfront / Fishery Zone): 32% of households", fontSize = 12.sp)
                Text("• Purok 2 (Coastal Commercial): 28% of households", fontSize = 12.sp)
                Text("• Purok 3 (Hillside & Agriculture): 24% of households", fontSize = 12.sp)
                Text("• Purok 4 (Mangrove Sanctuary Boundary): 16% of households", fontSize = 12.sp)
            }

            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Top Requested Certifications", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeepOceanBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Text("1. Barangay Clearance (44% of total volume)", fontSize = 12.sp)
                Text("2. Certificate of Residency (28% of total volume)", fontSize = 12.sp)
                Text("3. Certificate of Indigency (18% of total volume)", fontSize = 12.sp)
                Text("4. Business Clearance & Good Moral (10% of total volume)", fontSize = 12.sp)
            }
        }
    }
}

// 8. AUDIT LOGS
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAuditLogsScreen(
    onNavigateBack: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val logs by repository.auditLogs.collectAsState()
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd • HH:mm:ss", Locale.US) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Audit Trail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepOceanBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(logs) { log ->
                SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(log.action, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SouthernSeaTealDark)
                        Text(dateFormat.format(Date(log.timestamp)), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Actor: ${log.actorName} (${log.actorRole})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Target: ${log.targetType} [${log.targetId}]", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (log.previousState != null || log.newState != null) {
                        Text("Transition: ${log.previousState ?: "None"} → ${log.newState ?: "None"}", fontSize = 11.sp, color = DeepOceanBlue, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
