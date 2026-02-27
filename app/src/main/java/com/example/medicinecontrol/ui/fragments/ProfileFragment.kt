package com.example.medicinecontrol.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.medicinecontrol.HomeActivity
import com.example.medicinecontrol.R
import com.example.medicinecontrol.Repository
import com.example.medicinecontrol.ui.theme.MedicineControlTheme

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ComposeView>(R.id.compose_view).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MedicineControlTheme {
                    ProfileContent(
                        onLogout = {
                            (requireActivity() as? HomeActivity)?.logout()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileContent(onLogout: () -> Unit) {
    val usuario = Repository.usuarioActual
    val userName = usuario?.nombre?.ifBlank { null } ?: "Usuario"
    val userEmail = usuario?.email ?: "Sin correo"
    val medicamentos by Repository.medicamentosFlow.collectAsState()
    val catalogo by Repository.catalogoFlow.collectAsState()
    val totalTomas = medicamentos.sumOf { it.historialTomas.size }

    var notificaciones by remember { mutableStateOf(true) }
    var sonido by remember { mutableStateOf(false) }
    var recordarPrefs by remember { mutableStateOf(true) }
    var selectedTipo by remember { mutableStateOf("Diario") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // -- Perfil de usuario (Card bonito con Hola) --
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Hola, $userName",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // -- Estadísticas --
        Text("Mis Estadísticas", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(
                        progress = { if (medicamentos.isNotEmpty()) (totalTomas.toFloat() / (medicamentos.size * 3).coerceAtLeast(1)).coerceIn(0f, 1f) else 0f },
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 5.dp
                    )
                    Column {
                        Text("Adherencia al tratamiento", style = MaterialTheme.typography.titleSmall)
                        Text("$totalTomas tomas de ${medicamentos.size} medicamentos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { if (medicamentos.isNotEmpty()) (totalTomas.toFloat() / (medicamentos.size * 3).coerceAtLeast(1)).coerceIn(0f, 1f) else 0f },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${medicamentos.size}", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
                        Text("Activos", style = MaterialTheme.typography.labelMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${catalogo.size}", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.secondary)
                        Text("Catálogo", style = MaterialTheme.typography.labelMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$totalTomas", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.tertiary)
                        Text("Tomados", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // -- Preferencias --
        Text("Preferencias", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Switch - Notificaciones
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Notificaciones de medicamentos", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = notificaciones, onCheckedChange = { notificaciones = it })
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Switch - Sonido
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Alarma con sonido", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = sonido, onCheckedChange = { sonido = it })
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = recordarPrefs, onCheckedChange = { recordarPrefs = it })
                    Text("Recordar mis preferencias", style = MaterialTheme.typography.bodyMedium)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // FilterChips - Tipo recordatorio
                Text("Tipo de recordatorio", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Diario", "Semanal", "Mensual").forEach { label ->
                        FilterChip(
                            selected = selectedTipo == label,
                            onClick = { selectedTipo = label },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Accesibilidad info
                if (usuario != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("CONFIGURACIÓN DE CUENTA", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Accesibilidad", style = MaterialTheme.typography.bodyMedium)
                        Text(if (usuario.accesibilidadGrande) "Texto grande" else "Normal", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Recordatorio", style = MaterialTheme.typography.bodyMedium)
                        Text(if (usuario.recordatorioVisual) "Visual" else "Visual + Sonido", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // -- Cerrar Sesión --
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
