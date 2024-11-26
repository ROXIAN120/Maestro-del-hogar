package com.example.proyectofinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.Pantalla_principal.Pantalla_Principal
import com.example.proyectofinal.Pantalla_principal.ProviderType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private lateinit var pantalla21: ConstraintLayout
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var btnGoogleSignIn: Button
    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.authlayout2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setTheme(R.style.SplashTheme)

        // Verificar si el usuario ya está autenticado
        checkIfUserIsLoggedIn()

        pantalla21 = findViewById(R.id.authlayout2)
        btnGoogleSignIn = findViewById(R.id.btn_google_sign_in)

        setupGoogleSignIn()
        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    /**
     * Verifica si el usuario ya está autenticado y redirige a Pantalla_Principal si es así.
     */
    private fun checkIfUserIsLoggedIn() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Si el usuario ya está autenticado, ir a Pantalla_Principal
            goToMainScreen(currentUser.email ?: "", ProviderType.GOOGLE)
        }
    }

    /**
     * Configura el cliente de inicio de sesión de Google.
     */
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("558748247284-2ni64ca3972m0l8otuh57co19im4vluu.apps.googleusercontent.com") // Reemplaza con tu ID de cliente de Google
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    /**
     * Inicia el proceso de inicio de sesión con Google.
     */
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    /**
     * Maneja el resultado de actividades iniciadas para resultado.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("MainActivity", "Google sign in failed", e)
                showAlert("Error al iniciar sesión con Google")
            }
        }
    }

    /**
     * Autentica al usuario con Firebase utilizando la cuenta de Google.
     * @param account La cuenta de Google del usuario.
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Si la autenticación es exitosa, redirige a Pantalla_Principal
                    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
                    goToMainScreen(userEmail, ProviderType.GOOGLE)
                } else {
                    // Si falla, muestra un error
                    Log.w("MainActivity", "signInWithCredential:failure", task.exception)
                    showAlert("Error al iniciar sesión con Google")
                }
            }
    }

    private fun goToMainScreen(email: String, provider: ProviderType) {
        val intent = Intent(this, Pantalla_Principal::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(intent)
        finish() // Finaliza la actividad actual para evitar volver atrás
    }

    /**
     * Muestra un mensaje de alerta.
     * @param message El mensaje a mostrar.
     */
    private fun showAlert(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("Aceptar", null)
            create().show()
        }
    }
}