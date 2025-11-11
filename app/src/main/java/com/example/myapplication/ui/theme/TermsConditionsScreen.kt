package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsConditionsScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Conditions") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Last Updated: January 2024",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Please read these Terms and Conditions carefully before using our app. By accessing or using the Bookstore App, you agree to be bound by these terms.",
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            TermsSection(
                title = "1. Acceptance of Terms",
                content = "By creating an account or making a purchase, you accept these Terms and Conditions in full. If you disagree with any part of these terms, you must not use our services."
            )

            TermsSection(
                title = "2. Use of Service",
                content = "You agree to:\n\n• Provide accurate and complete information\n• Maintain the security of your account\n• Not use the service for any illegal purposes\n• Not attempt to harm or exploit our systems\n• Respect intellectual property rights\n• Not engage in fraudulent activities"
            )

            TermsSection(
                title = "3. Orders and Payment",
                content = "When you place an order:\n\n• All prices are in USD unless stated otherwise\n• We reserve the right to refuse any order\n• Payment must be received before shipment\n• You are responsible for providing correct shipping information\n• Orders are subject to availability"
            )

            TermsSection(
                title = "4. Shipping and Delivery",
                content = "• Delivery times are estimates and not guaranteed\n• Risk of loss passes to you upon delivery\n• We are not liable for delays caused by shipping carriers\n• International orders may be subject to customs fees\n• You must be available to receive deliveries"
            )

            TermsSection(
                title = "5. Returns and Refunds",
                content = "Our return policy allows:\n\n• Returns within 30 days of purchase\n• Items must be in original condition\n• Refunds processed within 5-10 business days\n• Customer pays return shipping unless item is defective\n• Some items may be non-returnable"
            )

            TermsSection(
                title = "6. Intellectual Property",
                content = "All content on the Bookstore App, including text, graphics, logos, and software, is the property of Bookstore App or its licensors and is protected by copyright and other intellectual property laws."
            )

            TermsSection(
                title = "7. User Content",
                content = "If you submit reviews, comments, or other content:\n\n• You grant us a license to use your content\n• You are responsible for your content\n• We may remove inappropriate content\n• You must not violate others' rights"
            )

            TermsSection(
                title = "8. Limitation of Liability",
                content = "To the fullest extent permitted by law, Bookstore App shall not be liable for any indirect, incidental, special, consequential, or punitive damages resulting from your use of our services."
            )

            TermsSection(
                title = "9. Disclaimer",
                content = "Our services are provided 'as is' without warranties of any kind. We do not guarantee that our services will be uninterrupted, secure, or error-free."
            )

            TermsSection(
                title = "10. Changes to Terms",
                content = "We reserve the right to modify these Terms and Conditions at any time. Continued use of our services after changes constitutes acceptance of the modified terms."
            )

            TermsSection(
                title = "11. Governing Law",
                content = "These Terms and Conditions are governed by and construed in accordance with the laws of the United States, and you submit to the exclusive jurisdiction of the courts located therein."
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Contact Us",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "If you have any questions about these Terms and Conditions, please contact us at legal@bookstore.com",
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TermsSection(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}