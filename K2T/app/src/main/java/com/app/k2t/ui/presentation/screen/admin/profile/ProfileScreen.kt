package com.app.k2t.ui.presentation.screen.admin.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController, userName: String, userEmail: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Name: $userName", modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        Text(text = "Email: $userEmail", modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))
        Button(onClick = { navController.navigate("OrdersScreen") }) {
            Text(text = "View Orders")
        }
    }
}