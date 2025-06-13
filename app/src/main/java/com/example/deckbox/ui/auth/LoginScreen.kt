package com.example.deckbox.ui.auth

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.deckbox.R
import com.example.deckbox.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var nick by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val sonido = remember { MediaPlayer() }
    val sonido2 = MediaPlayer()

    sonido2.setDataSource(context.resources.openRawResourceFd(R.raw.easter))

    DisposableEffect(Unit) {

        sonido.setDataSource(context.resources.openRawResourceFd(R.raw.tick))
        sonido.prepare()
        sonido2.prepare()

        onDispose {
            sonido.release()
            sonido2.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.icon),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 24.dp)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        Toast.makeText(
                            context,
                            "Ver. 1.2.9 \n ¡Creación de ligas próximamente!🧙‍️)",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        try {
                            if (sonido.isPlaying) {
                                sonido.stop()
                                sonido.prepare()
                            }
                            sonido.start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                context,
                                "Error al reproducir sonido",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    },
                    onLongClick = {
                        Toast.makeText(
                            context,
                            "Hecho por Rodrigo García Prieto. En honor a Dani y Germán 🖤",
                            Toast.LENGTH_LONG
                        ).show()
                        sonido2.start()
                    }
                )
        )

        Text("¡¡Bienvenido a DECKBOX!!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = nick,
            onValueChange = { nick = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(nick, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("register") }
        ) {
            Text("Crear una cuenta")
        }

        viewModel.error.value?.let { error ->
            Text(error, color = Color.Red)
        }
    }

    LaunchedEffect(viewModel.user.value) {
        viewModel.user.value?.let {
            navController.navigate("deckList") {
                popUpTo("login") { inclusive = true }
            }
            Toast.makeText(context, "Bienvenido/a ${it.name}!", Toast.LENGTH_SHORT).show()
        }
    }
}