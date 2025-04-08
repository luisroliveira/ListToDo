package com.example.todolist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class Tarefa(
    val titulo: String,
    val data: Date, // com hora
    var concluida: Boolean,
    var dataConcluida: Date? = null, // com hora, pode ser nula
    val id: String? = null
)

class TarefaAdapter(private val listaTarefas: MutableList<Tarefa>) :
    RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder>() {

    class TarefaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox = itemView.findViewById<CheckBox>(R.id.checkboxTarefa)
        val titulo = itemView.findViewById<TextView>(R.id.textTituloTarefa)
        val data = itemView.findViewById<TextView>(R.id.textDataTarefa)
        val iconeDelete = itemView.findViewById<ImageView>(R.id.ic_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarefaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarefa, parent, false)
        return TarefaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TarefaViewHolder, position: Int) {
        val tarefa = listaTarefas[position]
        holder.titulo.text = tarefa.titulo

        val formatoCompleto = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.data.text = formatoCompleto.format(tarefa.data)

        holder.checkbox.setOnCheckedChangeListener(null) // evita bug ao reaproveitar view
        holder.checkbox.isChecked = tarefa.concluida

        holder.iconeDelete.setOnClickListener {
            tarefa.id?.let { id ->
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                db.collection("tarefas").document(id)
                    .delete()
                    .addOnSuccessListener {
                        listaTarefas.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, listaTarefas.size)
                    }
            }
        }

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            tarefa.concluida = isChecked
            tarefa.dataConcluida = if (isChecked) Date() else null

            tarefa.id?.let { id ->
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val tarefaAtualizada = mapOf(
                    "concluida" to isChecked,
                    "dataConcluida" to tarefa.dataConcluida
                )

                db.collection("tarefas").document(id)
                    .update(tarefaAtualizada)
                    .addOnSuccessListener {
                        // Log.d("firebase", "Tarefa atualizada com sucesso")
                    }
                    .addOnFailureListener {
                        // Log.e("firebase", "Erro ao atualizar tarefa: ${it.message}")
                    }
            }
        }
    }

    override fun getItemCount(): Int = listaTarefas.size

    fun deletarTarefa(position: Int) {
        val tarefa = listaTarefas[position]
        tarefa.id?.let { id ->
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            db.collection("tarefas").document(id)
                .delete()
                .addOnSuccessListener {
                    listaTarefas.removeAt(position)
                    notifyItemRemoved(position)
                }
                .addOnFailureListener {
                     Log.e("firebase", "Erro ao deletar tarefa: ${it.message}")
                }
        }
    }
}