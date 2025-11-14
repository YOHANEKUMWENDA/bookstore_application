package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class CustomerAccount(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String? = null,
    val createdAt: Long = 0,
    val isActive: Boolean = true,
    val totalOrders: Int = 0,
    val totalSpent: Double = 0.0,
    val role: String = "customer" // customer or admin
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAccountManagementScreen(
    onBackClick: () -> Unit
) {
    var accounts by remember { mutableStateOf<List<CustomerAccount>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("all") } // all, active, inactive
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedAccount by remember { mutableStateOf<CustomerAccount?>(null) }
    var showAccountDetails by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAuthorized by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()
    val snackbarHostState = remember { SnackbarHostState() }

    // Verify admin authorization
    LaunchedEffect(Unit) {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                onBackClick()
                return@LaunchedEffect
            }

            val userDoc = firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .await()

            if (userDoc.getString("role") != "admin") {
                errorMessage = "Unauthorized: Admin access required"
                onBackClick()
                return@LaunchedEffect
            }

            isAuthorized = true
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Authorization check failed"
            onBackClick()
        }
    }

    // Load accounts - refreshes when refreshTrigger changes
    LaunchedEffect(refreshTrigger, isAuthorized) {
        if (!isAuthorized) return@LaunchedEffect

        isLoading = true
        try {
            val snapshot = firestore.collection("users")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            accounts = snapshot.documents.mapNotNull { doc ->
                CustomerAccount(
                    uid = doc.id,
                    name = doc.getString("name") ?: "",
                    email = doc.getString("email") ?: "",
                    phoneNumber = doc.getString("phoneNumber") ?: "",
                    profileImageUrl = doc.getString("profileImageUrl"),
                    createdAt = doc.getLong("createdAt") ?: 0,
                    isActive = doc.getBoolean("isActive") ?: true,
                    totalOrders = doc.getLong("totalOrders")?.toInt() ?: 0,
                    totalSpent = doc.getDouble("totalSpent") ?: 0.0,
                    role = doc.getString("role") ?: "customer"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Failed to load accounts: ${e.message}"
        }
        isLoading = false
    }

    // Show error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            errorMessage = null
        }
    }

    // Filter accounts with proper memoization
    val filteredAccounts = remember(accounts, searchQuery, filterStatus) {
        accounts.filter { account ->
            val matchesSearch = account.name.contains(searchQuery, ignoreCase = true) ||
                    account.email.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (filterStatus) {
                "active" -> account.isActive
                "inactive" -> !account.isActive
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Account Management") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Accounts") },
                            onClick = {
                                filterStatus = "all"
                                showFilterMenu = false
                            },
                            leadingIcon = {
                                if (filterStatus == "all") {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Active Only") },
                            onClick = {
                                filterStatus = "active"
                                showFilterMenu = false
                            },
                            leadingIcon = {
                                if (filterStatus == "active") {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Inactive Only") },
                            onClick = {
                                filterStatus = "inactive"
                                showFilterMenu = false
                            },
                            leadingIcon = {
                                if (filterStatus == "inactive") {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by name or email...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Stats Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Total",
                    value = accounts.size.toString(),
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
                StatCard(
                    title = "Active",
                    value = accounts.count { it.isActive }.toString(),
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF4CAF50)
                )
                StatCard(
                    title = "Inactive",
                    value = accounts.count { !it.isActive }.toString(),
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFF44336)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Accounts List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredAccounts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No accounts found",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredAccounts) { account ->
                        AccountCard(
                            account = account,
                            onClick = {
                                selectedAccount = account
                                showAccountDetails = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Account Details Bottom Sheet
    if (showAccountDetails && selectedAccount != null) {
        AccountDetailsBottomSheet(
            account = selectedAccount!!,
            onDismiss = { showAccountDetails = false },
            onRefresh = { refreshTrigger++ },
            onError = { error -> errorMessage = error }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun AccountCard(
    account: CustomerAccount,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (account.profileImageUrl != null) {
                    AsyncImage(
                        model = account.profileImageUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = account.name.firstOrNull()?.uppercase() ?: "U",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Account Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = account.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (account.role == "admin") {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ADMIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = account.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${account.totalOrders} orders • $${String.format("%.2f", account.totalSpent)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Status Badge
            Surface(
                color = if (account.isActive) Color(0xFF4CAF50) else Color(0xFFF44336),
                shape = CircleShape
            ) {
                Text(
                    text = if (account.isActive) "Active" else "Inactive",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountDetailsBottomSheet(
    account: CustomerAccount,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit,
    onError: (String) -> Unit
) {
    var showDeactivateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (account.profileImageUrl != null) {
                        AsyncImage(
                            model = account.profileImageUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = account.name.firstOrNull()?.uppercase() ?: "U",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = account.email,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Account Details
            DetailRow(icon = Icons.Default.Phone, label = "Phone", value = account.phoneNumber.ifEmpty { "Not provided" })
            DetailRow(icon = Icons.Default.CalendarToday, label = "Member Since", value = formatDate(account.createdAt))
            DetailRow(icon = Icons.Default.ShoppingCart, label = "Total Orders", value = account.totalOrders.toString())
            DetailRow(icon = Icons.Default.AttachMoney, label = "Total Spent", value = "$${String.format("%.2f", account.totalSpent)}")
            DetailRow(
                icon = Icons.Default.CheckCircle,
                label = "Status",
                value = if (account.isActive) "Active" else "Inactive"
            )
            DetailRow(icon = Icons.Default.PersonOutline, label = "Role", value = account.role.capitalizeFirst())

            Spacer(modifier = Modifier.height(24.dp))

            // Actions
            Text(
                text = "Actions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Toggle Status Button
            OutlinedButton(
                onClick = { showDeactivateDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (account.isActive) MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
                )
            ) {
                Icon(
                    imageVector = if (account.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (account.isActive) "Deactivate Account" else "Activate Account")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Delete Account Button
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Account")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Deactivate/Activate Dialog
    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            icon = {
                Icon(
                    imageVector = if (account.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                    contentDescription = null
                )
            },
            title = {
                Text(text = if (account.isActive) "Deactivate Account" else "Activate Account")
            },
            text = {
                Text(
                    text = if (account.isActive) {
                        "Are you sure you want to deactivate ${account.name}'s account? They won't be able to place orders."
                    } else {
                        "Are you sure you want to activate ${account.name}'s account?"
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            isUpdating = true
                            try {
                                firestore.collection("users")
                                    .document(account.uid)
                                    .update("isActive", !account.isActive)
                                    .await()

                                showDeactivateDialog = false
                                onDismiss()
                                onRefresh() // Trigger full refresh
                            } catch (e: Exception) {
                                e.printStackTrace()
                                onError("Failed to update account status: ${e.message}")
                            }
                            isUpdating = false
                        }
                    },
                    enabled = !isUpdating
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (account.isActive) "Deactivate" else "Activate")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeactivateDialog = false },
                    enabled = !isUpdating
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(text = "Delete Account")
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete ${account.name}'s account? This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            isUpdating = true
                            try {
                                firestore.collection("users")
                                    .document(account.uid)
                                    .delete()
                                    .await()

                                showDeleteDialog = false
                                onDismiss()
                                onRefresh() // Trigger full refresh
                            } catch (e: Exception) {
                                e.printStackTrace()
                                onError("Failed to delete account: ${e.message}")
                            }
                            isUpdating = false
                        }
                    },
                    enabled = !isUpdating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Delete")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !isUpdating
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "N/A"
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun String.capitalizeFirst(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    }
}