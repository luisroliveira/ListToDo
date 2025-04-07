package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TelaLista : AppCompatActivity() {

    private lateinit var icon_user: ImageView
    private lateinit var icon_add: ImageView
    private lateinit var icon_lista: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_lista)

        supportActionBar?.hide()

        iniciarComponentes()

        val recyclerView = findViewById<RecyclerView>(R.id.minhaRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val tarefas = listOf(
            Tarefa("Estudar Android", "06/04/2025", false),
            Tarefa("Revisar projeto", "07/04/2025", true),
            Tarefa("Enviar relatório", "08/04/2025", false),
            Tarefa("Enviar teste 1", "08/04/2025", false),
            Tarefa("Enviar teste 1", "08/04/2025", false),
            Tarefa("Enviar teste 1", "08/04/2025", false),
            Tarefa("Enviar teste 1", "08/04/2025", false),
            Tarefa("Enviar teste 1", "08/04/2025", false)
        )

        val adapter = TarefaAdapter(tarefas)
        recyclerView.adapter = adapter

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
        val intent = Intent(this, TelaPrincipal::class.java)
        startActivity(intent)
        finish() // opcional, para não voltar à tela de login com o botão "voltar"
    }

    private fun telaLista() {
//        val intent = Intent(this, TelaLista::class.java)
//        startActivity(intent)
//        finish() // opcional, para não voltar à tela de login com o botão "voltar"
    }

    private fun telaNovaTarefa() {
//        val intent = Intent(this, TelaPrincipal::class.java)
//        startActivity(intent)
//        finish() // opcional, para não voltar à tela de login com o botão "voltar"
    }

    private fun iniciarComponentes() {
        icon_lista = findViewById(R.id.iconeCheck)
        icon_add = findViewById(R.id.iconeAdd)
        icon_user = findViewById(R.id.iconePerfil)
    }
}