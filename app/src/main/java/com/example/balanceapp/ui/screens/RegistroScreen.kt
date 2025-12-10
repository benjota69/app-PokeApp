package com.example.balanceapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit, // se llama cuando se registra bien
    onVolverALogin: () -> Unit  // se llama para volver al login
) {
    // Estados de los campos y de la interfaz
    var email by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }
    var mostrarClave by remember { mutableStateOf(false) }
    var mostrarConfirmarClave by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    // Instancia de FirebaseAuth que usaremos para registrar al usuario
    val auth = remember { FirebaseAuth.getInstance() }

    // misma validación que en login
    fun validarEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
    // validación del largo de la clave
    fun validarClave(clave: String): Boolean {
        return clave.length >= 6
    }
    // estructura de la pantalla
    Scaffold(
        topBar = { TopAppBar(title = { Text("Crear cuenta") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // campos del email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; error = null },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = error?.contains("email") == true || error?.contains("ya está registrado") == true
            )

            Spacer(Modifier.height(12.dp))
            // campos de la contraseña
            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it; error = null },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (mostrarClave) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { mostrarClave = !mostrarClave }) {
                        Icon(
                            imageVector = if (mostrarClave) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = error?.contains("contraseña") == true
            )

            Spacer(Modifier.height(12.dp))

            // confirmación de contraseña
            OutlinedTextField(
                value = confirmarClave,
                onValueChange = { confirmarClave = it; error = null },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = if (mostrarConfirmarClave) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { mostrarConfirmarClave = !mostrarConfirmarClave }) {
                        Icon(
                            imageVector = if (mostrarConfirmarClave) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = error?.contains("coinciden") == true
            )
            // mensaje de error
            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(20.dp))
            // botón de registrarse
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                onClick = {
                    // Validaciones básicas antes de crear el usuario.
                    when {
                        email.isBlank() -> error = "El email es requerido"
                        !validarEmail(email) -> error = "El email no es válido"
                        clave.isBlank() -> error = "La contraseña es requerida"
                        !validarClave(clave) -> error = "La contraseña debe tener al menos 6 caracteres"
                        confirmarClave.isBlank() -> error = "Confirma tu contraseña"
                        clave != confirmarClave -> error = "Las contraseñas no coinciden"
                        else -> {
                            cargando = true
                            error = null
                            // Si todo está ok, intentamos registrar al usuario en Firebase
                            auth.createUserWithEmailAndPassword(email, clave)
                                .addOnCompleteListener { task ->
                                    cargando = false
                                    if (task.isSuccessful) {
                                        // Registro OK, pasamos a la siguiente pantalla
                            onRegistroExitoso()
                                    } else {
                                        // Mostramos un mensaje de error amigable
                                        error = task.exception?.localizedMessage
                                            ?: "No se pudo crear la cuenta. Intenta nuevamente."
                                    }
                                }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (cargando) "Registrando..." else "Registrarse")
            }

            Spacer(Modifier.height(12.dp))
            // botón de volver al login
            TextButton(
                onClick = onVolverALogin,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

