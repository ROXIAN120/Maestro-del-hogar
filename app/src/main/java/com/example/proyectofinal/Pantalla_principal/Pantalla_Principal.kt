package com.example.proyectofinal.Pantalla_principal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.NavigationUI
import com.example.proyectofinal.MainActivity
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.ActivityPantallaPrincipalBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    GOOGLE
}

class Pantalla_Principal : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPantallaPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarPantallaPrincipal.toolbar)


        // Obtener el correo electrónico del usuario actualmente autenticado
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        // Establecer el correo electrónico del usuario en el TextView del encabezado de la barra de navegación
        val headerView = binding.navView.getHeaderView(0)
        val userEmailTextView: TextView = headerView.findViewById(R.id.nav_gmail)
        userEmailTextView.text = userEmail

        // Guardar el correo electrónico en SharedPreferences para persistencia de datos
        val sharedPreferences = getSharedPreferences(getString(R.string.Guardar_datos), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", userEmail ?: "") // Si userEmail es nulo, se guarda una cadena vacía
        editor.apply()
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_pantalla_principal)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Configuración del listener para manejar la selección de ítems en la barra de navegación
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_cerrar_sesion -> {
                    signOut()
                    true
                }
                else -> {
                    // Permite que NavigationUI maneje automáticamente los otros ítems del menú.
                    NavigationUI.onNavDestinationSelected(menuItem, navController) || super.onOptionsItemSelected(menuItem)
                }
            }
        }
    }

    /**
     * Cierra sesión del usuario tanto de Firebase como de Google, y redirige a la pantalla de inicio de sesión.
     */
    private fun signOut() {
        // Limpiar datos de inicio de sesión en SharedPreferences
        val grd = getSharedPreferences(getString(R.string.Guardar_datos), Context.MODE_PRIVATE).edit()
        grd.clear()
        grd.apply()

        // Cerrar sesión de Firebase
        FirebaseAuth.getInstance().signOut()

        // Cerrar sesión de Google
        val googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        )
        googleSignInClient.signOut().addOnCompleteListener {
            // Redirigir a MainActivity para volver a iniciar sesión
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Cerrar esta actividad para evitar regresar a la pantalla principal
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_pantalla_principal)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}