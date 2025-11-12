package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.myapplication.data.Book
import com.example.myapplication.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            MyApplicationTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        isDarkMode = isDarkMode,
                        onDarkModeToggle = { isDarkMode = !isDarkMode }
                    )
                }
            }
        }
    }
}

sealed class Screen {
    object Landing : Screen()
    object Login : Screen()
    object Signup : Screen()
    object Home : Screen()
    object AdminDashboard : Screen()
    data class BookDetail(val book: Book) : Screen()
    data class CategoryBooks(val categoryName: String) : Screen()
    object Cart : Screen()
    object Profile : Screen()
    object EditProfile : Screen()
    object HelpSupport : Screen()
    object About : Screen()
    object PrivacyPolicy : Screen()
    object TermsConditions : Screen()
    object PaymentMethods : Screen()
    object Search : Screen()
    object Favorites : Screen()
}

@Composable
fun AppNavigation(
    isDarkMode: Boolean,
    onDarkModeToggle: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Determine initial screen based on authentication state
    val initialScreen = if (currentUser != null) {
        // User is logged in - check if admin
        val isAdmin = currentUser.email == "admin@gmail.com"
        if (isAdmin) Screen.AdminDashboard else Screen.Home
    } else {
        Screen.Landing
    }

    var navigationStack by remember { mutableStateOf(listOf<Screen>(initialScreen)) }

    // Store user profile data - sync with Firebase user
    var userProfile by remember {
        mutableStateOf(
            UserProfile(
                name = currentUser?.displayName ?: "User",
                email = currentUser?.email ?: "user@example.com",
                phone = "+265 984518884",
                address = "ZOMBA STREET 123",
                city = "ZOMBA CITY",
                country = "MALAWI",
                bio = "Book lovers of all times"
            )
        )
    }

    val currentScreen = navigationStack.last()

    fun navigateTo(screen: Screen) {
        navigationStack = navigationStack + screen
    }

    fun navigateBack() {
        if (navigationStack.size > 1) {
            navigationStack = navigationStack.dropLast(1)
        }
    }

    fun navigateToHome(isAdmin: Boolean) {
        val homeScreen = if (isAdmin) Screen.AdminDashboard else Screen.Home

        // Update user profile with Firebase user data
        val user = auth.currentUser
        if (user != null) {
            userProfile = userProfile.copy(
                name = user.displayName ?: "User",
                email = user.email ?: "user@example.com"
            )
        }

        // Clear stack and go to home
        navigationStack = listOf(homeScreen)
    }

    fun logout() {
        // Sign out from Firebase
        auth.signOut()
        // Clear stack and go to login
        navigationStack = listOf(Screen.Login)
    }

    when (currentScreen) {
        is Screen.Landing -> LandingScreen(
            onGetStarted = { navigateTo(Screen.Login) }
        )

        is Screen.Login -> LoginScreen(
            onLoginSuccess = { isAdmin -> navigateToHome(isAdmin) },
            onSignupClick = { navigateTo(Screen.Signup) }
        )

        is Screen.Signup -> SignupScreen(
            onSignupSuccess = { navigateToHome(false) },
            onBackToLogin = { navigateBack() }
        )

        is Screen.Home -> BookstoreHomeScreen(
            onBookClick = { book -> navigateTo(Screen.BookDetail(book)) },
            onCategoryClick = { category -> navigateTo(Screen.CategoryBooks(category)) },
            onCartClick = { navigateTo(Screen.Cart) },
            onProfileClick = { navigateTo(Screen.Profile) },
            onSearchClick = { navigateTo(Screen.Search) },
            onFavoritesClick = { navigateTo(Screen.Favorites) }
        )

        is Screen.AdminDashboard -> AdminDashboard(onLogout = { logout() })

        is Screen.BookDetail -> BookDetailScreen(
            book = currentScreen.book,
            onBackClick = { navigateBack() },
            onCartClick = { navigateTo(Screen.Cart) }
        )

        is Screen.CategoryBooks -> CategoryBooksScreen(
            categoryName = currentScreen.categoryName,
            onBackClick = { navigateBack() },
            onBookClick = { book -> navigateTo(Screen.BookDetail(book)) },
            onCartClick = { navigateTo(Screen.Cart) }
        )

        is Screen.Cart -> CartScreen(
            onBackClick = { navigateBack() },
            onCheckout = {
                // Handle checkout - for now just go back to home
                navigateToHome(false)
            }
        )

        is Screen.Profile -> ProfileScreen(
            onBackClick = { navigateBack() },
            onLogout = { logout() },
            isDarkMode = isDarkMode,
            onDarkModeToggle = { onDarkModeToggle() },
            onEditProfileClick = { navigateTo(Screen.EditProfile) },
            userProfile = userProfile,
            onHelpSupportClick = { navigateTo(Screen.HelpSupport) },
            onAboutClick = { navigateTo(Screen.About) },
            onPrivacyPolicyClick = { navigateTo(Screen.PrivacyPolicy) },
            onTermsConditionsClick = { navigateTo(Screen.TermsConditions) },
            onPaymentMethodsClick = { navigateTo(Screen.PaymentMethods) }
        )

        is Screen.EditProfile -> EditProfileScreen(
            onBackClick = { navigateBack() },
            onSaveClick = { updatedProfile ->
                // Save the updated profile locally
                userProfile = updatedProfile

                // Also update Firebase user profile
                val user = auth.currentUser
                if (user != null) {
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(updatedProfile.name)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnSuccessListener {
                            // Profile updated successfully
                        }
                        .addOnFailureListener { e ->
                            // Handle error
                        }
                }

                // Navigate back to profile screen
                navigateBack()
            },
            initialProfile = userProfile
        )

        is Screen.HelpSupport -> HelpSupportScreen(
            onBackClick = { navigateBack() }
        )

        is Screen.About -> AboutScreen(
            onBackClick = { navigateBack() }
        )

        is Screen.PrivacyPolicy -> PrivacyPolicyScreen(
            onBackClick = { navigateBack() }
        )

        is Screen.TermsConditions -> TermsConditionsScreen(
            onBackClick = { navigateBack() }
        )

        is Screen.PaymentMethods -> PaymentMethodsScreen(
            onBackClick = { navigateBack() },
            onPaymentSuccess = { navigateBack() }
        )

        is Screen.Search -> SearchScreen(
            onBackClick = { navigateBack() },
            onBookClick = { book -> navigateTo(Screen.BookDetail(book)) }
        )

        is Screen.Favorites -> FavoritesScreen(
            onBackClick = { navigateBack() },
            onBookClick = { book -> navigateTo(Screen.BookDetail(book)) }
        )
    }
}