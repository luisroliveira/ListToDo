package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class TelaPrincipal : AppCompatActivity() {

    private lateinit var email_user: TextView
    private lateinit var nome_user: TextView
    private lateinit var bt_deslogar: Button
    private lateinit var icon_user: ImageView
    private lateinit var icon_add: ImageView
    private lateinit var icon_lista: ImageView

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var usuarioId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_principal)
        supportActionBar?.hide()

        iniciarComponentes()

        bt_deslogar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if (usuarioAtual != null) {
            val email = usuarioAtual.email
            email_user.text = email

            usuarioId = usuarioAtual.uid

            val documentReference: DocumentReference = db.collection("usuarios").document(usuarioId)
            documentReference.addSnapshotListener(
                EventListener<DocumentSnapshot> { snapshot, error ->
                    if (snapshot != null && snapshot.exists()) {
                        nome_user.text = snapshot.getString("nome")
                    }
                }
            )
        }

        icon_user.setOnClickListener {
            telaPrincipal()
        }

        icon_lista.setOnClickListener{
            telaLista()
        }

        icon_add.setOnClickListener {
            telaNovaTarefa()
        }
    }

    private fun telaPrincipal() {
//        val intent = Intent(this, TelaPrincipal::class.java)
//        startActivity(intent)
//        finish() // opcional, para não voltar à tela de login com o botão "voltar"
    }

    private fun telaLista() {
        val intent = Intent(this, TelaLista::class.java)
        startActivity(intent)
        finish() // opcional, para não voltar à tela de login com o botão "voltar"
    }

    private fun telaNovaTarefa() {
//        val intent = Intent(this, TelaPrincipal::class.java)
//        startActivity(intent)
//        finish() // opcional, para não voltar à tela de login com o botão "voltar"
    }

    private fun iniciarComponentes() {
        email_user = findViewById(R.id.textEmailUser)
        nome_user = findViewById(R.id.textNomeUser)
        bt_deslogar = findViewById(R.id.bt_deslogar)
        icon_lista = findViewById(R.id.iconeCheck)
        icon_add = findViewById(R.id.iconeAdd)
        icon_user = findViewById(R.id.iconePerfil)
    }
}
