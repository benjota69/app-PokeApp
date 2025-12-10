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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
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
fun LoginScreen(
    onLoginExitoso: () -> Unit,  // Se llama cuando el login está OK
    onIrARegistro: () -> Unit = {} // Se llama cuando el usuario quiere registrarse
) {
    // Estados que usa esta pantalla
    var email by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var mostrarClave by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    // Instancia de FirebaseAuth que usaremos para loguear al usuario
    val auth = remember { FirebaseAuth.getInstance() }

    // validación básica de correo
    fun validarEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
    // estructura de la pantalla
    Scaffold(
        topBar = { TopAppBar(title = { Text("Iniciar sesión") }) }
    ) { padding ->
        // Usamos Column para ordenar las cosas una debajo de la otra.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // campo del email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it;
                    error = null }, // limpiamos el error para no molestar
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = error?.contains("email") == true
            )

            Spacer(Modifier.height(12.dp))
            // campo contraseñas
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
                isError = error?.contains("contraseña") == true || error?.contains("credenciales") == true
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
            // botón entrar
            Button(
                // Aquí va toda la lógica de validación del login con Firebase.
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                onClick = {
                    when {
                        email.isBlank() -> error = "El email es requerido"
                        !validarEmail(email) -> error = "El email no es válido"
                        clave.isBlank() -> error = "La contraseña es requerida"
                        else -> {
                            cargando = true
                            error = null
                            // Llamada a Firebase Auth para iniciar sesión
                            auth.signInWithEmailAndPassword(email, clave)
                                .addOnCompleteListener { task ->
                                    cargando = false
                                    if (task.isSuccessful) {
                                        // Login OK, llamamos a la función de éxito.
                            onLoginExitoso()
                                    } else {
                                        // Mostramos un mensaje de error amigable.
                                        error = task.exception?.localizedMessage
                                            ?: "No se pudo iniciar sesión. Revisa tus credenciales."
                                    }
                                }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (cargando) "Entrando..." else "Entrar")
            }

            Spacer(Modifier.height(12.dp))
            // botón para ir al registro
            TextButton(
                onClick = onIrARegistro,
                // Usamos el color primario para que se vea bien en claro y oscuro
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}


