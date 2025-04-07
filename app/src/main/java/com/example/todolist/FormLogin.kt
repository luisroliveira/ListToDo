package com.example.todolist

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityFormLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class FormLogin : AppCompatActivity() {

    private lateinit var binding: ActivityFormLoginBinding

    private lateinit var edit_email: EditText
    private lateinit var edit_senha: EditText
    private lateinit var bt_entrar: Button
    private lateinit var progress_bar: ProgressBar
    private val mensagens = arrayOf("Preencha todos os campos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        iniciarComponentes()

        binding.textTelaCadastro.setOnClickListener {
            val intent = Intent(this, FormCadastro::class.java)
            startActivity(intent)
        }

        bt_entrar.setOnClickListener { view ->
            val email = edit_email.text.toString()
            val senha = edit_senha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                val snackbar = Snackbar.make(view, mensagens[0], Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.WHITE)
                snackbar.setTextColor(Color.BLACK)
                snackbar.show()
            } else {
                autenticarUsuario(email, senha, view)
            }
        }
    }

    private fun autenticarUsuario(email: String, senha: String, view: View) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progress_bar.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({
                        telaLista()
                    }, 3000)
                } else {
                    val snackbar = Snackbar.make(view, "Erro ao fazer login", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.setTextColor(Color.WHITE)
                    snackbar.show()
                }
            }
    }

    override fun onStart() {
        super.onStart()

        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if (usuarioAtual != null) {
            telaLista()
        }
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun telaLista() {
        val intent = Intent(this, TelaLista::class.java)
        startActivity(intent)
        finish() // opcional, para não voltar à tela de login com o botão "voltar"
    }

    private fun telaPrincipal() {
        val intent = Intent(this, TelaPrincipal::class.java)
        startActivity(intent)
        finish() // opcional, para não voltar à tela de login com o botão "voltar"
    }

    private fun iniciarComponentes() {
        edit_email = findViewById(R.id.edit_email)
        edit_senha = findViewById(R.id.edit_senha)
        bt_entrar = findViewById(R.id.bt_entrar)
        progress_bar = findViewById(R.id.progress_bar)
    }
}
