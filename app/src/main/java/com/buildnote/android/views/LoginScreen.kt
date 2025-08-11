package com.buildnote.android.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildnote.android.R


@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Beispieldaten für den Login
    val sampleEmail = ""
    val samplePassword = ""

    // Die Login-Seite nimmt den gesamten Bildschirm ein und erhält einen dezenten, hellgrauen Hintergrund.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(333))  // Heller Grauton
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // BuildNote-Logo oben zentriert
            Image(
                painter = painterResource(id = R.drawable.buildnote_icon),
                contentDescription = "BuildNote Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Email-Eingabefeld
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Passwort-Eingabefeld
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Passwort") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Rechtsbündiger Text-Button "Passwort vergessen"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Passwort vergessen",
                    color = Color(0xFFFFA500),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        // Hier später die Funktion einfügen
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Optional: Fehlermeldung, falls die Zugangsdaten falsch sind
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Login-Button in Orange mit abgerundeten Ecken
            Button(
                onClick = {
                    if (email.trim() == sampleEmail && password == samplePassword) {
                        errorMessage = ""
                        onLoginSuccess()
                    } else {
                        errorMessage = "Ungültige Email oder Passwort."
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA500) // Orange
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
