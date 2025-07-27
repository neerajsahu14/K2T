package com.app.k2t.ui.presentation.screen.userauth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.k2t.R
import com.app.k2t.firebase.model.User
import com.app.k2t.ui.presentation.viewmodel.UserViewModel
import com.app.k2t.util.PrefsHelper
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegisterScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel = koinViewModel()
) {
    // State variables
    var role by remember { mutableStateOf("table") }
    var roleExpanded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var tableNumber by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Error states
    var emailError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var tableNumberError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val authError by userViewModel.authError.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val roleOptions = listOf(
        "table" to "Table",
        "waiter" to "Waiter",
        "chef" to "Chef",
        "admin" to "Admin"
    )

    // Validation functions
    fun validateEmail(): Boolean {
        return when {
            email.text.isBlank() -> {
                emailError = "Email is required"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches() -> {
                emailError = "Please enter a valid email"
                false
            }
            else -> {
                emailError = null
                true
            }
        }
    }

    fun validateName(): Boolean {
        return when {
            name.text.isBlank() -> {
                nameError = if (role == "table") "Table name is required" else "Name is required"
                false
            }
            name.text.length < 2 -> {
                nameError = "Name must be at least 2 characters"
                false
            }
            else -> {
                nameError = null
                true
            }
        }
    }

    fun validateTableNumber(): Boolean {
        return if (role == "table") {
            when {
                tableNumber.text.isBlank() -> {
                    tableNumberError = "Table number is required"
                    false
                }
                !tableNumber.text.all { it.isDigit() } -> {
                    tableNumberError = "Table number must contain only numbers"
                    false
                }
                else -> {
                    tableNumberError = null
                    true
                }
            }
        } else {
            tableNumberError = null
            true
        }
    }

    fun validatePassword(): Boolean {
        return when {
            password.text.isBlank() -> {
                passwordError = "Password is required"
                false
            }
            password.text.length < 6 -> {
                passwordError = "Password must be at least 6 characters"
                false
            }
            else -> {
                passwordError = null
                true
            }
        }
    }

    fun validateConfirmPassword(): Boolean {
        return when {
            confirmPassword.text.isBlank() -> {
                confirmPasswordError = "Please confirm your password"
                false
            }
            confirmPassword.text != password.text -> {
                confirmPasswordError = "Passwords do not match"
                false
            }
            else -> {
                confirmPasswordError = null
                true
            }
        }
    }

    fun clearFieldsForRole(selectedRole: String) {
        if (selectedRole != "table") {
            tableNumber = TextFieldValue("")
            tableNumberError = null
        }
        if (selectedRole == "table") {
            // For table role, clear name errors when switching
            nameError = null
        }
    }

    fun handleRegistration() {
        val validations = listOf(
            validateEmail(),
            validateName(),
            validateTableNumber(),
            validatePassword(),
            validateConfirmPassword()
        )

        if (validations.all { it }) {
            keyboardController?.hide()

            val user = when (role) {
                "table" -> User(
                    role = "table",
                    tableNumber = tableNumber.text,
                    name = name.text
                )
                else -> User(
                    name = name.text,
                    role = role
                )
            }

            userViewModel.registerUser(
                email.text,
                password.text,
                user
            ) { destination ->
                val context = navController.context
                PrefsHelper.saveUserRole(context, role)
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Fill in your details to get started",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            // Registration Form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                validateEmail()
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        isError = emailError != null,
                        supportingText = emailError?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    // Role Selection
                    ExposedDropdownMenuBox(
                        expanded = roleExpanded,
                        onExpandedChange = { roleExpanded = !roleExpanded }
                    ) {
                        OutlinedTextField(
                            value = roleOptions.find { it.first == role }?.second ?: "Table",
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true),
                            label = { Text("Select Role") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Role",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded)
                            },
                            readOnly = true,
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        DropdownMenu(
                            expanded = roleExpanded,
                            onDismissRequest = { roleExpanded = false }
                        ) {
                            roleOptions.forEach { (value, display) ->
                                DropdownMenuItem(
                                    text = { Text(display) },
                                    onClick = {
                                        role = value
                                        clearFieldsForRole(value)
                                        roleExpanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = when (value) {
                                                "table" -> painterResource(R.drawable.baseline_table_restaurant_24)
                                                "waiter" -> painterResource(R.drawable.baseline_person_24)
                                                "chef" -> painterResource(R.drawable.restaurant)
                                                "admin" -> painterResource(R.drawable.admin_panel_settings)
                                                else -> painterResource(R.drawable.account_circle)
                                            },
                                            contentDescription = display
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Table Number Field (only for table role)
                    if (role == "table") {
                        OutlinedTextField(
                            value = tableNumber,
                            onValueChange = {
                                tableNumber = it
                                if (tableNumberError != null) tableNumberError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Table Number") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.numbers),
                                    contentDescription = "Table Number",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    validateTableNumber()
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            ),
                            isError = tableNumberError != null,
                            supportingText = tableNumberError?.let {
                                { Text(it, color = MaterialTheme.colorScheme.error) }
                            }
                        )
                    }

                    // Name Field
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (nameError != null) nameError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(if (role == "table") "Table Name" else "Full Name")
                        },
                        leadingIcon = {
                            Icon(
                                painter = if (role == "table") painterResource(R.drawable.baseline_table_restaurant_24)
                                    else painterResource(R.drawable.badge),
                                contentDescription = "Name",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                validateName()
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        isError = nameError != null,
                        supportingText = nameError?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError != null) passwordError = null
                            if (confirmPasswordError != null && confirmPassword.text.isNotEmpty()) {
                                confirmPasswordError = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = if (passwordVisible) painterResource(R.drawable.visibility)
                                    else painterResource(R.drawable.visibility_off),
                                    contentDescription = if (passwordVisible) "Hide password"
                                    else "Show password"
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                validatePassword()
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        isError = passwordError != null,
                        supportingText = passwordError?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    // Confirm Password Field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            if (confirmPasswordError != null) confirmPasswordError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirm Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Confirm Password",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    painter = if (passwordVisible) painterResource(R.drawable.visibility)
                                    else painterResource(R.drawable.visibility_off),
                                    contentDescription = if (confirmPasswordVisible) "Hide password"
                                    else "Show password"
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleRegistration() }
                        ),
                        isError = confirmPasswordError != null,
                        supportingText = confirmPasswordError?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Register Button
                    Button(
                        onClick = { handleRegistration() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Creating Account...")
                            }
                        } else {
                            Text(
                                "Create Account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Auth Error Display
                    authError?.let { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Login Link
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Already have an account? ",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(
                            onClick = { navController.navigate("login") },
                            enabled = !isLoading
                        ) {
                            Text(
                                "Sign In",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}