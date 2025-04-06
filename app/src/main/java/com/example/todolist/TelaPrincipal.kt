package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
    }

    private fun iniciarComponentes() {
        email_user = findViewById(R.id.textEmailUser)
        nome_user = findViewById(R.id.textNomeUser)
        bt_deslogar = findViewById(R.id.bt_deslogar)
    }
}
