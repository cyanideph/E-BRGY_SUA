package com.example.ui.admin

import androidx.compose.foundation.ListItem
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui unit.dp
import com.example.R

/** Admin Dashboard - Barangay Official's main dashboard.
 * 
 *  Displays overview of service requests, emergency reports, and allows
 *  approval/rejection of service requests. Follows constitution principles:
 *  - II: Clean architecture with clear separation between UI, domain, data
 *  - VI: Data integrity (validation at repository layer)
 *  - VII: Accessibility (touch targets, contentDescription, contrast)
 *  - VIII: Testing (tests required before merge)
 *  - IX: Compatibility (min SDK 24)
 */
@Composable
fun AdminDashboard(
    navController: NavHostController,
    viewModel: AdminViewModel = AdminViewModel(),
    onRequestAction: ((String, String) -> Unit)? = null
) { // (requestId, action: "approve" | "reject")
    val (serviceRequests, error) = viewModel.serviceRequests.collect { requests ->
        Pair(requests, error)
    }


// Status badge for visual hierarchy
@Composable
fun statusBadge(status: String, requestId: String) = Box(
    modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth(),
    contentAlignment = Alignment.Center,
    backgroundColor = when(status) {
        "APPROVED" -> MaterialTheme.colorScheme.success
        "REJECTED" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.warning
    },
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
    shape = MaterialTheme.shapes.small,
    text = {
        Text(
            text = status,
            contentDescription = "Request $requestId status: $status",
            style = MaterialTheme.typography.bodySmall
        )
    }
)


    when {
        isEmpty(serviceRequests) && error == null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalPadding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.BaseIcons.admin_panel_settings,
                    contentDescription = "Admin dashboard",
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Admin Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Text(
                    text = "No service requests at this time",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else {
            AdminRequestList(
                serviceRequests = serviceRequests,
                onAction = { requestId, action ->
                    onRequestAction?.invoke(requestId, action)
                }
            )
        }
    }
}

/** Admin Service Request List - displays pending requests for review. */
@Composable
fun AdminRequestList(
    serviceRequests: List<ServiceRequest>,
    onAction: ((String, String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var refresh by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalPadding(16.dp),
        verticalArrangement = Arrangement.Start
    ) {
        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            arrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Service Requests (${
                    serviceRequests.size
                })",
                style = MaterialTheme.typography.headlineSmall
            )
            if (refresh) {
                CircularProgressIndicator(
                    modifier = Modifier.wrapContentSize(),
                    strokeWidth = 2.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (serviceRequests.isEmpty()) {
            Text(
                text = "No pending requests",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(serviceRequests.size) { index ->
                    adminRequestCard(
                        request = serviceRequests[index],
                        onApprove = { onAction?.invoke(serviceRequests[index].id, "approve") },
                        onReject = { onAction?.invoke(serviceRequests[index].id, "reject") }
                    )
                }
            }
        }
    }
}

/** Individual admin request card with approve/reject actions. */
@Composable
fun adminRequestCard(
    request: ServiceRequest,
    onApprove: (() -> Unit)?,
    onReject: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        elevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {
            // Request info
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Request: ${request.type?.takeIf { it.isNotEmpty() } ?: "Unknown"}",
                    style = MaterialTheme.typography.titleSmall,
                    overflow = Overflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "Status: ${request.status?.takeIf { it.isNotEmpty() } ?: "PENDING"}",
                    style = MaterialTheme.typography.body1,
                    color = when (request.status) {
                        "APPROVED" -> MaterialTheme.colorScheme.primary
                        "REJECTED" -> MaterialTheme.colorScheme.error
                        "PENDING" -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.secondary
                    }
                )
                Text(
                    text = "Requested: ${request.requestDate?.takeIf { it.isNotEmpty() } ?: ""}",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                arrangement = Arrangement.End
            ) {
                if (onApprove != null) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.padding(end = 4.dp),
                        enabled = request.status == "PENDING"
                    ) {
                        Text("Approve", style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (onReject != null) {
                    Button(
                        onClick = onReject,
                        modifier = Modifier.padding(end = 4.dp),
                        enabled = request.status == "PENDING"
                    ) {
                        Text("Reject", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

/** Admin ViewModel - handles service request data fetching. */
class AdminViewModel : ViewModel() {

    private val _serviceRequests = MutableStateFlow<List<ServiceRequest>>(emptyList())
    val serviceRequests: Flow<List<ServiceRequest>> = _serviceRequests

    private val _isLoading = MutableStateFlow(true)
    val isLoading: Flow<bool> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: Flow<String?> = _error

    private val authProvider: AuthProvider = AuthProvider(AppwriteDataSource())

    /** Fetch service requests for admin */
    init {
        viewModelScope.launch {
            fetchServiceRequests()
        }
    }

    private fun fetchServiceRequests() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val requests = withContext(Dispatchers.IO) {
                    authProvider.getServiceRequests("admin").firstOrNull()
                        ?: emptyList()
                }

                // Transform from Appwrite data model to domain model
                val serviceRequests = requests.map { req ->
                    ServiceRequest(
                        type = req.type ?: "",
                        status = req.status ?: "PENDING",
                        requestedBy = req.requestedBy ?: "",
                        description = req.description ?: "",
                        requiredDocuments = req.requiredDocuments ?: emptyList(),
                        requestDate = req.requestDate ?: java.time.LocalDate.now().toString()
                    )
                }

                _serviceRequests.value = serviceRequests
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load service requests. Please try again."
                _isLoading.value = false
                ErrorHandler.logError("fetchServiceRequests", e, mapOf())
            }
        }
    }

    /** Refresh requests */
    fun refresh() {
        viewModelScope.launch {
            fetchServiceRequests()
        }
    }
}