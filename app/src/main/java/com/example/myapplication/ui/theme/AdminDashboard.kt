package com.example.myapplication.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.BooksData
import kotlinx.coroutines.launch

// Data Models (re-using from BooksData)
typealias Book = com.example.myapplication.data.Book

data class Order(
    val id: String,
    val customerName: String,
    val items: Int,
    val total: Double,
    val status: String,
    val date: String
)

data class Customer(
    val id: String,
    val name: String,
    val email: String,
    val totalOrders: Int,
    val totalSpent: Double
)

enum class AdminScreen(val title: String, val icon: ImageVector) {
    Dashboard("Dashboard", Icons.Default.Dashboard),
    Books("Books Management", Icons.Default.Book),
    Orders("Orders", Icons.Default.ShoppingCart),
    Customers("Customers", Icons.Default.People),
    Analytics("Analytics", Icons.Default.Analytics),
    Settings("Settings", Icons.Default.Settings)
}

// Sample Data Functions
fun getSampleOrders(): List<Order> {
    return listOf(
        Order("101", "John Kumwenda", 3, 45.97, "Delivered", "2024-09-01"),
        Order("102", "Jane Phiri", 1, 12.99, "Pending", "2024-09-02"),
        Order("103", "Peter Banda", 5, 80.50, "Processing", "2024-10-02"),
        Order("104", "Samuel Gondwe", 10, 93.50, "Processing", "2024-09-02"),
        Order("105", "Joseph Malizani", 10, 90.50, "Delivered", "2025-09-010")
    )
}

fun getSampleCustomers(): List<Customer> {
    return listOf(
        Customer("C1", "John Kumwenda", "johnK@gmail.com", 5, 250.75),
        Customer("C2", "Jane Phiri", "janeS@gmail.com", 12, 1200.00),
        Customer("C3", "Peter Banda", "RobertB@gmail.com", 2, 89.90),
        Customer("C4", "Samuel Gondwe", "SamuelB@gmail.com", 4, 68.90),
        Customer("C5", "Joseph Malizani", "JosephM@gmail.com", 4, 56.90)
    )
}


// Main Dashboard
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(onLogout: () -> Unit) {
    var selectedScreen by remember { mutableStateOf(AdminScreen.Dashboard) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val allBooks = remember { mutableStateListOf(*BooksData.allBooks.toTypedArray()) }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.study_stress),
            contentDescription = "background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NavigationDrawerContent(
                    selectedScreen = selectedScreen,
                    onScreenSelected = {
                        selectedScreen = it
                        scope.launch { drawerState.close() }
                    },
                    onLogout = onLogout
                )
            },
            scrimColor = Color.Black.copy(alpha = 0.5f)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(selectedScreen.title) },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent, // Make it transparent
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                },
                containerColor = Color.Transparent // Make scaffold transparent
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    when (selectedScreen) {
                        AdminScreen.Dashboard -> DashboardScreen(allBooks)
                        AdminScreen.Books -> BooksScreen(allBooks, onAddBook = { allBooks.add(it) }, onDeleteBook = { allBooks.remove(it) })
                        AdminScreen.Orders -> OrdersScreen()
                        AdminScreen.Customers -> CustomersScreen()
                        AdminScreen.Analytics -> AnalyticsScreen(allBooks)
                        AdminScreen.Settings -> SettingsScreen()
                    }
                }
            }
        }
    }
}

// Navigation Drawer
@Composable
fun NavigationDrawerContent(
    selectedScreen: AdminScreen,
    onScreenSelected: (AdminScreen) -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f) // Semi-transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Bookstore Admin",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            AdminScreen.values().forEach { screen ->
                NavigationDrawerItem(
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(screen.title) },
                    selected = selectedScreen == screen,
                    onClick = { onScreenSelected(screen) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                label = { Text("Logout") },
                selected = false,
                onClick = onLogout,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

// Dashboard Screen
@Composable
fun DashboardScreen(books: List<Book>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Sales",
                    value = "$45,231",
                    icon = Icons.Default.ShoppingCart,
                    color = Color(0xFF4CAF50)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Orders",
                    value = "156",
                    icon = Icons.Default.List,
                    color = Color(0xFF2196F3)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Books",
                    value = books.size.toString(),
                    icon = Icons.Default.Book,
                    color = Color(0xFFFF9800)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Customers",
                    value = "892",
                    icon = Icons.Default.Person,
                    color = Color(0xFF9C27B0)
                )
            }
        }

        item {
            Text(
                text = "Recent Orders",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
                color = Color.White,



            )
        }

        items(getSampleOrders().take(5)) { order ->
            OrderCard(order)
        }
    }
}

// Books Screen
@Composable
fun BooksScreen(books: List<Book>, onAddBook: (Book) -> Unit, onDeleteBook: (Book) -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }
    var bookToDelete by remember { mutableStateOf<Book?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Books",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, "Add Book")
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(books) { book ->
                BookCard(book, onDeleteClick = { bookToDelete = book })
            }
        }
    }

    if (showAddDialog) {
        AddBookDialog(onDismiss = { showAddDialog = false }, onAddBook = onAddBook)
    }

    bookToDelete?.let { book ->
        DeleteBookDialog(
            book = book,
            onDismiss = { bookToDelete = null },
            onConfirm = {
                onDeleteBook(book)
                bookToDelete = null
            }
        )
    }
}

// Orders Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen() {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Pending", "Processing", "Delivered", "Cancelled")

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = filters.indexOf(selectedFilter),
            modifier = Modifier.fillMaxWidth()
        ) {
            filters.forEach { filter ->
                Tab(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    text = { Text(filter) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(getSampleOrders()) { order ->
                OrderCard(order)
            }
        }
    }
}

// Customers Screen
@Composable
fun CustomersScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "All Customers",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(getSampleCustomers()) { customer ->
            CustomerCard(customer)
        }
    }
}

// Analytics Screen
@Composable
fun AnalyticsScreen(books: List<Book>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Analytics & Reports",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Monthly Revenue", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("$45,231", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    Text("+12.5% from last month", fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnalyticsCard(
                    modifier = Modifier.weight(1f),
                    title = "Best Seller",
                    value = "Fiction",
                    subtitle = "35% of sales"
                )
                AnalyticsCard(
                    modifier = Modifier.weight(1f),
                    title = "Avg. Order",
                    value = "$89.50",
                    subtitle = "Per customer"
                )
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Top Selling Books", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    books.take(5).forEach { book ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(book.title, fontSize = 14.sp)
                           // Text("${book.stock} sold", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// Settings Screen
@Composable
fun SettingsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()){
                Column(modifier = Modifier.padding(16.dp)){
                     Text("App Version", fontWeight = FontWeight.Bold)
                     Text("1.0.0")
                }
            }
        }
    }
}


// Placeholder Composables
@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector, color: Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = title, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order #${order.id}", fontWeight = FontWeight.Bold)
            Text("Customer: ${order.customerName}")
            Text("Total: $${order.total} (${order.items} items)")
            Text("Status: ${order.status}")
        }
    }
}

@Composable
fun BookCard(book: Book, onDeleteClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, fontWeight = FontWeight.Bold)
                Text("by ${book.author}")
                Text("Price: $${book.price}")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, "Delete Book", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddBookDialog(onDismiss: () -> Unit, onAddBook: (Book) -> Unit) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Book") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val newBook = Book(
                    id = (BooksData.allBooks.size + 1),
                    title = title,
                    author = author,
                    price = price.toDoubleOrNull() ?: 0.0,
                    rating = 0f,
                    category = category,
                    description = "",
                    pages = 0,
                    language = "English",
                    publisher = "",
                    publicationYear = 2024,
                    isbn = "",
                    imageRes = R.drawable.mock_book // Default image
                )
                onAddBook(newBook)
                onDismiss()
            }) {
                Text("Add Book")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteBookDialog(book: Book, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Book") },
        text = { Text("Are you sure you want to delete '${book.title}'?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CustomerCard(customer: Customer) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(customer.name, fontWeight = FontWeight.Bold)
            Text(customer.email)
            Text("Total Orders: ${customer.totalOrders}")
            Text("Total Spent: $${customer.totalSpent}")
        }
    }
}

@Composable
fun AnalyticsCard(modifier: Modifier = Modifier, title: String, value: String, subtitle: String) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
