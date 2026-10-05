package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BarangayRepository
import com.example.ui.components.SoftSkeuomorphicCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

private data class ChatMessage(
    val sender: String,
    val text: String,
    val isBot: Boolean,
    val timestamp: String = "Just now"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    onNavigateBack: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val services by repository.services.collectAsState()
    val announcements by repository.announcements.collectAsState()
    val events by repository.events.collectAsState()
    val officials by repository.officials.collectAsState()
    val hotlines by repository.hotlines.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "Barangay Assistant",
                text = "Hello! I am your Barangay Assistant for e-Barangay Sua. I can help answer questions regarding our available public services, documentary requirements, scheduled community events, and emergency contacts. How may I assist you today?",
                isBot = true
            )
        )
    }

    val sampleQueries = listOf(
        "What are the requirements for Barangay Clearance?",
        "How do I apply for Certificate of Indigency?",
        "When is the next community cleanup or assembly?",
        "What is the emergency hotline for San Juan Police?"
    )

    fun answerQuery(query: String) {
        val q = query.lowercase()
        val response = when {
            q.contains("clearance") -> {
                val s = services.firstOrNull { it.id == "srv_clearance" }
                "For a ${s?.name ?: "Barangay Clearance"}, the requirements are: ${s?.requirements?.joinToString { it.title }}. Processing time is ${s?.processingDays} with an official fee of ${s?.feeDescription}. First-time jobseekers are free of charge under RA 11261."
            }
            q.contains("residency") -> {
                val s = services.firstOrNull { it.id == "srv_residency" }
                "To get a Certificate of Residency in Barangay Sua: You must present a valid government ID, endorsement from your designated Purok leader, and proof of at least 6 months continuous domicile. Fee is ${s?.feeDescription}."
            }
            q.contains("indigency") -> {
                val s = services.firstOrNull { it.id == "srv_indigency" }
                "A Certificate of Indigency is issued free of charge (Gratis) for medical or financial aid. Requirements include a valid ID and proof of need (such as a hospital prescription, bill, or enrollment form)."
            }
            q.contains("business") -> {
                val s = services.firstOrNull { it.id == "srv_business_clearance" }
                "For a Business Clearance: Requirements include a DTI Business Name registration, location & sanitary inspection check, and lease/title verification. Turnaround is ${s?.processingDays}."
            }
            q.contains("event") || q.contains("assembly") || q.contains("cleanup") -> {
                val upcoming = events.joinToString(separator = "\n• ") { "${it.title} on ${it.date} (${it.location})" }
                "Here are the upcoming official events in Barangay Sua:\n• $upcoming"
            }
            q.contains("announcement") || q.contains("gale") || q.contains("weather") -> {
                val latest = announcements.firstOrNull()
                "Latest official announcement: '${latest?.title}' published on ${latest?.publishedDate}. Description: ${latest?.description}"
            }
            q.contains("police") || q.contains("hotline") || q.contains("emergency") || q.contains("hospital") -> {
                val h = hotlines.joinToString(separator = "\n• ") { "${it.name}: ${it.number} (${it.agency})" }
                "Official emergency contact hotlines:\n• $h"
            }
            q.contains("official") || q.contains("captain") -> {
                val cap = officials.firstOrNull { it.position.contains("Captain") }
                "The Punong Barangay (Barangay Captain) is ${cap?.name}. Office hours are ${cap?.officeHours}. Note: Official records in this portal are currently maintained as verified demo listings."
            }
            else -> {
                "I don't have reliable information about that. Please contact the Barangay Sua office for official information or visit the Multipurpose Hall during regular office hours (Monday to Friday, 8:00 AM to 5:00 PM)."
            }
        }

        messages.add(ChatMessage(sender = "Resident", text = query, isBot = false))
        messages.add(ChatMessage(sender = "Barangay Assistant", text = response, isBot = true))
        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Barangay Assistant", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Grounded in official Barangay Sua data", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
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
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask about services, requirements, events...") },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val q = inputText.trim()
                                inputText = ""
                                answerQuery(q)
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = DeepOceanBlue)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Quick suggestion chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                sampleQueries.take(2).forEach { sample ->
                    Surface(
                        onClick = { answerQuery(sample) },
                        shape = RoundedCornerShape(12.dp),
                        color = SouthernSeaTealContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = sample,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SouthernSeaTealDark,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isBot) Arrangement.Start else Arrangement.End
                    ) {
                        if (msg.isBot) {
                            Surface(
                                shape = CircleShape,
                                color = DeepOceanBlue,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (msg.isBot) 4.dp else 16.dp,
                                bottomEnd = if (msg.isBot) 16.dp else 4.dp
                            ),
                            color = if (msg.isBot) CoastalSurfaceVariant else DeepOceanBlue,
                            shadowElevation = 1.dp,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = msg.sender,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (msg.isBot) SouthernSeaTealDark else WarmSunGoldLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text,
                                    fontSize = 13.sp,
                                    color = if (msg.isBot) MaterialTheme.colorScheme.onSurface else Color.White,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
