package com.example.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TelaNovaTarefa : AppCompatActivity() {

    private lateinit var icon_user: ImageView
    private lateinit var icon_add: ImageView
    private lateinit var icon_lista: ImageView
    private lateinit var inputTitulo: EditText
    private lateinit var inputDataHora: EditText
    private lateinit var btnSalvar: Button

    private var dataSelecionada: Date? = null
    private val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_nova_tarefa)

        supportActionBar?.hide()
        iniciarComponentes()

        icon_user.setOnClickListener {
            telaPrincipal()
        }

        icon_lista.setOnClickListener {
            telaLista()
        }

        icon_add.setOnClickListener {
            telaNovaTarefa()
        }

        inputDataHora.setOnClickListener {
            abrirDateTimePicker()
        }

        btnSalvar.setOnClickListener {
            val titulo = inputTitulo.text.toString().trim()
            val data = dataSelecionada

            if (titulo.isNotEmpty() && data != null) {
                val novaTarefa = Tarefa(
                    titulo = titulo,
                    data = data,
                    concluida = false,
                    dataConcluida = null
                )
                salvarTarefa(novaTarefa)
                telaLista()
            }
        }
    }

    private fun salvarTarefa(tarefa: Tarefa) {
        val db = FirebaseFirestore.getInstance()
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val dadosTarefa = hashMapOf(
            "titulo" to tarefa.titulo,
            "data" to tarefa.data,
            "concluida" to tarefa.concluida,
            "dataConcluida" to tarefa.dataConcluida,
            "usuarioId" to usuarioId
        )

        db.collection("tarefas")
            .add(dadosTarefa)
            .addOnSuccessListener {
                Log.d("db", "Tarefa salva com sucesso")
            }
            .addOnFailureListener { e ->
                Log.e("db_error", "Erro ao salvar tarefa: ${e.message}")
            }
    }

    private fun abrirDateTimePicker() {
        val calendario = Calendar.getInstance()

        DatePickerDialog(this,
            { _, ano, mes, dia ->
                TimePickerDialog(this,
                    { _, hora, minuto ->
                        calendario.set(ano, mes, dia, hora, minuto)
                        dataSelecionada = calendario.time
                        inputDataHora.setText(formato.format(dataSelecionada!!))
                    },
                    calendario.get(Calendar.HOUR_OF_DAY),
                    calendario.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun telaPrincipal() {
        val intent = Intent(this, TelaPrincipal::class.java)
        startActivity(intent)
        finish()
    }

    private fun telaLista() {
        val intent = Intent(this, TelaLista::class.java)
        startActivity(intent)
        finish()
    }

    private fun telaNovaTarefa() {
        // Atual não faz nada, pois já está na tela
    }

    private fun iniciarComponentes() {
        icon_lista = findViewById(R.id.iconeCheck)
        icon_add = findViewById(R.id.iconeAdd)
        icon_user = findViewById(R.id.iconePerfil)
        inputTitulo = findViewById(R.id.inputTitulo)
        inputDataHora = findViewById(R.id.inputDataHora)
        btnSalvar = findViewById(R.id.bt_salvar)
    }
}
