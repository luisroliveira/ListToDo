package com.example.todolist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.text.SimpleDateFormat
import java.util.*
import android.widget.AdapterView
import android.app.DatePickerDialog
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class TelaLista : AppCompatActivity() {

    private lateinit var icon_user: ImageView
    private lateinit var icon_add: ImageView
    private lateinit var icon_lista: ImageView
    private lateinit var icon_graficos: ImageView
    private lateinit var switchFiltro: Switch
    private lateinit var layoutFiltro: LinearLayout
    private lateinit var btnSelecionarData: Button
    private lateinit var textoData: TextView

    private var filtroAtivo = false
    private var dataSelecionada: Calendar? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var tarefaAdapter: TarefaAdapter
    private val tarefas = mutableListOf<Tarefa>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_lista)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        supportActionBar?.hide()
        iniciarComponentes()
        configurarFirebase()

        ativarFiltroAoAbrir()
        atualizarTextoData()

        recyclerView = findViewById(R.id.minhaRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tarefaAdapter = TarefaAdapter(tarefas) {
            carregarTarefasDoFirebase()
        }
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

        icon_graficos.setOnClickListener {
            telaGraficos()
        }

        switchFiltro.setOnCheckedChangeListener { _, isChecked ->
            filtroAtivo = isChecked
            layoutFiltro.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                dataSelecionada = null
                carregarTarefasDoFirebase()
                atualizarTextoData()
            }
        }

        btnSelecionarData.setOnClickListener {
            val calendarioInicial = dataSelecionada ?: Calendar.getInstance()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    dataSelecionada = Calendar.getInstance().apply {
                        set(year, month, day, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    carregarTarefasDoFirebase()
                    atualizarTextoData()
                },
                calendarioInicial.get(Calendar.YEAR),
                calendarioInicial.get(Calendar.MONTH),
                calendarioInicial.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
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
                        if (!filtroAtivo || (dataSelecionada != null && mesmaData(dataTimestamp, dataSelecionada!!.time))) {
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
                }
                tarefas.sortWith(compareBy<Tarefa> { it.concluida }.thenBy { it.data })
                tarefaAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("firebase", "Erro ao buscar tarefas: ${e.message}")
            }
    }

    private fun mesmaData(data1: Date, data2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = data1 }
        val cal2 = Calendar.getInstance().apply { time = data2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    private fun atualizarTextoData() {
        if (dataSelecionada != null && filtroAtivo) {
            val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            textoData.text = "Filtrando por: ${formatoData.format(dataSelecionada!!.time)}"
            textoData.visibility = View.VISIBLE
        } else {
            textoData.text = ""
            textoData.visibility = View.GONE
        }
    }

    private fun ativarFiltroAoAbrir() {
        dataSelecionada = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        filtroAtivo = true
        switchFiltro.isChecked = true

        layoutFiltro.visibility = View.VISIBLE
    }

    private fun configurarFirebase() {
        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
    }

    private fun telaPrincipal() {
        val intent = Intent(this, TelaPrincipal::class.java)
        startActivity(intent)
    }

    private fun telaLista() {
        // já estamos nesta tela
    }

    private fun telaGraficos() {
        val intent = Intent(this, TelaGraficos::class.java)
        startActivity(intent)
    }

    private fun telaNovaTarefa() {
        val intent = Intent(this, TelaNovaTarefa::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        val usuario = FirebaseAuth.getInstance().currentUser
        if (usuario == null) {
            val intent = Intent(this, FormLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun iniciarComponentes() {
        icon_lista = findViewById(R.id.iconeCheck)
        icon_add = findViewById(R.id.iconeAdd)
        icon_user = findViewById(R.id.iconePerfil)
        icon_graficos = findViewById(R.id.iconeGrafico)
        switchFiltro = findViewById(R.id.switchFiltro)
        layoutFiltro = findViewById(R.id.layoutFiltro)
        btnSelecionarData = findViewById(R.id.btnSelecionarData)
        textoData = findViewById(R.id.textoData)
    }
}
