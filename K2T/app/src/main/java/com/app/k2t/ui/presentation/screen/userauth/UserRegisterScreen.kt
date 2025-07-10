package com.app.k2t.ui.presentation.screen.userauth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
    var role by remember { mutableStateOf("table") }
    var roleExpanded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var tableNumber by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    val authError by userViewModel.authError.collectAsState()
    var registrationError by remember { mutableStateOf<String?>(null) }

    val roleOptions = listOf("table", "waiter", "chef", "admin")

    fun clearFieldsForRole(selectedRole: String) {
        if (selectedRole != "table") tableNumber = TextFieldValue("")
        if (selectedRole == "table") {
            name = TextFieldValue("")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.widthIn(max = 500.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Register User",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Role selection
                ExposedDropdownMenuBox(
                    expanded = roleExpanded,
                    onExpandedChange = { roleExpanded = !roleExpanded }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(type = MenuAnchorType.PrimaryEditable , enabled= true),
                        label = { Text("Role") },
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    DropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        roleOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    role = option
                                    clearFieldsForRole(option)
                                    roleExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Table fields
                if (role == "table") {
                    OutlinedTextField(
                        value = tableNumber,
                        onValueChange = { tableNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Table Number") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Table Name") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Name") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // Password fields for all
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        registrationError = null
                        // Validation by role
                        if (email.text.isBlank()) {
                            registrationError = "Email is required."
                            return@Button
                        }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
                            registrationError = "Invalid email format."
                            return@Button
                        }
                        when (role) {
                            "table" -> {
                                if (tableNumber.text.isBlank() || name.text.isBlank() || password.text.isBlank() || confirmPassword.text.isBlank()) {
                                    registrationError = "All fields are required."
                                    return@Button
                                }
                            }
                            else -> {
                                if (name.text.isBlank() || password.text.isBlank() || confirmPassword.text.isBlank()) {
                                    registrationError = "All fields are required."
                                    return@Button
                                }
                            }
                        }
                        if (password.text.length < 6) {
                            registrationError = "Password must be at least 6 characters."
                            return@Button
                        }
                        if (password.text != confirmPassword.text) {
                            registrationError = "Passwords do not match."
                            return@Button
                        }
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
                        val username = when (role) {
                            "table" -> tableNumber.text
                            else -> name.text // Use name as username for non-table roles
                        }
                        userViewModel.registerUser(
                            email.text, // Use email for registration
                            password.text,
                            user
                        ) { destination ->
                            val context = navController.context
                            PrefsHelper.saveUserRole(context, role)
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Register", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                registrationError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, fontSize = 14.sp)
                }
                authError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Already have an account? Login", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
