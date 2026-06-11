package com.example.varahanest.presentation.support

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.varahanest.domain.model.SupportTicket

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportCenterScreen(
    viewModel: SupportCenterViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val tickets by viewModel.tickets.collectAsState()
    val submissionState by viewModel.submissionState.collectAsState()
    val scrollState = rememberScrollState()

    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(submissionState) {
        if (submissionState is TicketSubmissionState.Success) {
            showSuccessDialog = true
            subject = ""
            description = ""
            viewModel.resetSubmissionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Support Center", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // WhatsApp quick contact
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "WhatsApp Icon",
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Contact Support via WhatsApp", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Available 24/7 for urgent listing questions", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val uri = Uri.parse("https://api.whatsapp.com/send?phone=919876543210&text=Hi, I need assistance with my listing on Varaha Nest.")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Text("Open WhatsApp Support", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Ticketing form
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Create a Support Ticket", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Issue Subject") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Description") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        maxLines = 5
                    )
                    Button(
                        onClick = {
                            if (subject.isNotEmpty() && description.isNotEmpty()) {
                                viewModel.submitTicket(subject, description)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = submissionState !is TicketSubmissionState.Loading
                    ) {
                        if (submissionState is TicketSubmissionState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Submit Support Ticket", fontWeight = FontWeight.Bold)
                        }
                    }
                    if (submissionState is TicketSubmissionState.Error) {
                        Text(
                            text = (submissionState as TicketSubmissionState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Active tickets list
                if (tickets.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Active Support Tickets", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        tickets.forEach { ticket ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(ticket.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(ticket.description, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (ticket.status == "OPEN") MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color(0xFF4CAF50).copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            ticket.status,
                                            color = if (ticket.status == "OPEN") MaterialTheme.colorScheme.primary else Color(0xFF4CAF50),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // FAQ Accordion
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Frequently Asked Questions", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    FAQItem(
                        q = "How do I list my property?",
                        a = "Click on the 'Sell/Rent' button in the bottom navigation. Follow the 3-step listing wizard to input basic info, property details, and select photos. Save drafts at any point and publish when ready."
                    )
                    FAQItem(
                        q = "Is my listing automatically verified?",
                        a = "All property listings undergo manual verification by our security team within 24 hours of submission to prevent spam. Verified properties display a green 'Verified' badge."
                    )
                    FAQItem(
                        q = "How do I contact support directly?",
                        a = "You can initiate a chat with our automated WhatsApp assistant using the button at the top of the Support Center page, or submit a support ticket below for direct email resolution."
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Success dialog overlay
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Ticket Submitted Successfully", fontWeight = FontWeight.Bold) },
                text = { Text("Your support request has been logged. Our customer service representative will respond via email within 24 hours.") },
                confirmButton = {
                    Button(onClick = { showSuccessDialog = false }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}

@Composable
fun FAQItem(q: String, a: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(q, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand FAQ",
                    tint = Color.Gray
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(a, fontSize = 13.sp, lineHeight = 18.sp, color = Color.DarkGray)
            }
        }
    }
}
