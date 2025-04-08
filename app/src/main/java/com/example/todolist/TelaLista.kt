package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TelaLista : AppCompatActivity() {

    private lateinit var icon_user: ImageView
    private lateinit var icon_add: ImageView
    private lateinit var icon_lista: ImageView

    private lateinit var recyclerView: RecyclerView
    private lateinit var tarefaAdapter: TarefaAdapter
    private val tarefas = mutableListOf<Tarefa>()
    private val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_lista)

        supportActionBar?.hide()
        iniciarComponentes()

        recyclerView = findViewById(R.id.minhaRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tarefaAdapter = TarefaAdapter(tarefas)
        recyclerView.adapter = tarefaAdapter

        carregarTarefasDoFirebase()

        icon_user.setOnClickListener {
            telaPrincipal()
        }

        icon_lista.setOnClickListener {
            telaLista()
        }

        icon_add.setOnClickListener {
            telaNovaTarefa()
        }
    }

    private fun carregarTarefasDoFirebase() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("tarefas")
            .whereEqualTo("usuarioId", usuarioId)
            .get()
            .addOnSuccessListener { resultado ->
                tarefas.clear()
                for (documento in resultado) {
                    val titulo = documento.getString("titulo") ?: ""
                    val dataTimestamp = documento.getTimestamp("data")?.toDate()
                    val concluida = documento.getBoolean("concluida") ?: false
                    val dataConcluidaTimestamp = documento.getTimestamp("dataConcluida")?.toDate()

                    if (dataTimestamp != null) {
                        val tarefa = Tarefa(
                            titulo = titulo,
                            data = dataTimestamp,
                            concluida = concluida,
                            dataConcluida = dataConcluidaTimestamp,
                            id = documento.id
                        )
                        tarefas.add(tarefa)
                    }
                }
                tarefaAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("firebase", "Erro ao buscar tarefas: ${e.message}")
            }
    }

    private fun telaPrincipal() {
        val intent = Intent(this, TelaPrincipal::class.java)
        startActivity(intent)
        finish()
    }

    private fun telaLista() {
        // já estamos nesta tela
    }

    private fun telaNovaTarefa() {
        val intent = Intent(this, TelaNovaTarefa::class.java)
        startActivity(intent)
        finish()
    }

    private fun iniciarComponentes() {
        icon_lista = findViewById(R.id.iconeCheck)
        icon_add = findViewById(R.id.iconeAdd)
        icon_user = findViewById(R.id.iconePerfil)
    }
}
