package com.example.myapplication.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class PaymentType {
    MOBILE_MONEY,
    BANK_CARD
}

// A data class to represent a payment method
data class PaymentMethod(
    val name: String,
    val type: PaymentType
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    // List of available payment methods
    val paymentMethods = listOf(
        PaymentMethod("Airtel Money", PaymentType.MOBILE_MONEY),
        PaymentMethod("TNM Mpamba", PaymentType.MOBILE_MONEY),
        PaymentMethod("PayPal", PaymentType.BANK_CARD),
        PaymentMethod("National Bank (NB)", PaymentType.BANK_CARD),
        PaymentMethod("Visa Card", PaymentType.BANK_CARD)
    )

    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var amount by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Methods") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Select a Payment Method",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(paymentMethods.size) { index ->
                val method = paymentMethods[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedMethod = method }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedMethod == method,
                        onClick = { selectedMethod = method }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = method.name, fontSize = 18.sp)
                }
            }

            if (selectedMethod != null) {
                item {
                    when (selectedMethod!!.type) {
                        PaymentType.MOBILE_MONEY -> {
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it.filter { char -> char.isDigit() } },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Phone Number") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true
                            )
                        }
                        PaymentType.BANK_CARD -> {
                            OutlinedTextField(
                                value = accountNumber,
                                onValueChange = { accountNumber = it.filter { char -> char.isDigit() } },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Account Number") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        leadingIcon = { Text("MWK", fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp)) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item {
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedMethod != null && amount.isNotBlank() && (if (selectedMethod?.type == PaymentType.MOBILE_MONEY) phoneNumber.isNotBlank() else accountNumber.isNotBlank())
                ) {
                    Text("Proceed to Confirmation", fontSize = 18.sp)
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Payment") },
            text = {
                Column {
                    Text("Please enter your password to confirm the payment of MWK$amount.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        showSuccessDialog = true
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onPaymentSuccess()
            },
            title = { Text("Payment Successful") },
            text = { Text("Your payment of MWK$amount via ${selectedMethod?.name} was successful.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onPaymentSuccess()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}