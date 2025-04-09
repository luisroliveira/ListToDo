package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class TelaGraficos : AppCompatActivity() {

    private lateinit var icon_user: ImageView
    private lateinit var icon_add: ImageView
    private lateinit var icon_lista: ImageView
    private lateinit var icon_graficos: ImageView
    private lateinit var spinnerAno: Spinner
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_graficos)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        supportActionBar?.hide()

        iniciarComponentes()
        configurarSpinner()
        configurarGrafico()

        icon_user.setOnClickListener { telaPrincipal() }
        icon_lista.setOnClickListener { telaLista() }
        icon_add.setOnClickListener { telaNovaTarefa() }
        icon_graficos.setOnClickListener { /* já está na tela */ }
    }

    private fun configurarSpinner() {
        val anos = (2020..Calendar.getInstance().get(Calendar.YEAR)).toList().reversed()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, anos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAno.adapter = adapter

        spinnerAno.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long
            ) {
                val anoSelecionado = anos[position]
                atualizarGrafico(anoSelecionado)
                atualizarGraficoPizza(anoSelecionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun configurarGrafico() {
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)

        barChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        barChart.axisRight.isEnabled = false

        barChart.xAxis.valueFormatter = object : ValueFormatter() {
            private val meses = arrayOf("JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ")

            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index in 0..11) meses[index] else ""
            }
        }

        barChart.xAxis.granularity = 1f
    }

    private fun atualizarGrafico(ano: Int) {
        carregarTarefasAgrupadasPorMes(ano) { tarefasPorMes ->
            val entradas = tarefasPorMes.mapIndexed { index, valor ->
                BarEntry(index.toFloat(), valor.toFloat())
            }

            val dataSet = BarDataSet(entradas, "Tarefas em $ano").apply {
                color = resources.getColor(R.color.purple_500, null)
                valueTextSize = 12f
            }

            barChart.data = BarData(dataSet)
            barChart.invalidate()
        }
    }

    private fun carregarTarefasAgrupadasPorMes(anoSelecionado: Int, callback: (IntArray) -> Unit) {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("tarefas")
            .whereEqualTo("usuarioId", usuarioId)
            .whereEqualTo("concluida", true)
            .get()
            .addOnSuccessListener { resultado ->
                val tarefasPorMes = IntArray(12) { 0 }

                for (documento in resultado) {
                    val data = documento.getTimestamp("data")?.toDate()

                    if (data != null) {
                        val calendar = Calendar.getInstance().apply { time = data }

                        val ano = calendar.get(Calendar.YEAR)
                        val mes = calendar.get(Calendar.MONTH)

                        if (ano == anoSelecionado) {
                            tarefasPorMes[mes]++
                        }
                    }
                }

                callback(tarefasPorMes)
            }
            .addOnFailureListener { e ->
                Log.e("firebase", "Erro ao buscar tarefas: ${e.message}")
            }
    }

    private fun atualizarGraficoPizza(ano: Int) {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("tarefas")
            .whereEqualTo("usuarioId", usuarioId)
            .get()
            .addOnSuccessListener { resultado ->
                var concluidas = 0
                var pendentes = 0

                for (documento in resultado) {
                    val data = documento.getTimestamp("data")?.toDate()
                    val concluida = documento.getBoolean("concluida") ?: false

                    if (data != null) {
                        val calendar = Calendar.getInstance().apply { time = data }
                        val anoTarefa = calendar.get(Calendar.YEAR)

                        if (anoTarefa == ano) {
                            if (concluida) {
                                concluidas++
                            } else {
                                pendentes++
                            }
                        }
                    }
                }

                val entradas = listOf(
                    PieEntry(concluidas.toFloat(), "Concluídas"),
                    PieEntry(pendentes.toFloat(), "Pendentes")
                )

                val dataSet = PieDataSet(entradas, "").apply {
                    colors = listOf(
                        resources.getColor(R.color.teal_700, null),
                        resources.getColor(R.color.purple_200, null)
                    )
                    valueTextSize = 14f
                }

                val pieData = PieData(dataSet)

                pieChart.data = pieData
                pieChart.centerText = "Status em $ano"
                pieChart.setUsePercentValues(true)
                pieChart.setDrawEntryLabels(true)
                pieChart.description = Description().apply { text = "" }
                pieChart.invalidate()
            }
            .addOnFailureListener { e ->
                Log.e("firebase", "Erro ao buscar tarefas: ${e.message}")
            }
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

    private fun telaPrincipal() {
        startActivity(Intent(this, TelaPrincipal::class.java))
    }

    private fun telaLista() {
        startActivity(Intent(this, TelaNovaTarefa::class.java))
    }

    private fun telaNovaTarefa() {
        startActivity(Intent(this, TelaNovaTarefa::class.java))
    }

    private fun iniciarComponentes() {
        icon_lista = findViewById(R.id.iconeCheck)
        icon_add = findViewById(R.id.iconeAdd)
        icon_user = findViewById(R.id.iconePerfil)
        icon_graficos = findViewById(R.id.iconeGrafico)
        spinnerAno = findViewById(R.id.spinnerAno)
        barChart = findViewById(R.id.barChart)
        pieChart = findViewById(R.id.pieChart)
    }
}
