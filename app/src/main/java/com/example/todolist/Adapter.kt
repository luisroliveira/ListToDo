package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Tarefa(
    val titulo: String,
    val data: String,
    var concluida: Boolean
)

class TarefaAdapter(private val listaTarefas: List<Tarefa>) :
    RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder>() {

    class TarefaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox = itemView.findViewById<CheckBox>(R.id.checkboxTarefa)
        val titulo = itemView.findViewById<TextView>(R.id.textTituloTarefa)
        val data = itemView.findViewById<TextView>(R.id.textDataTarefa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarefaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarefa, parent, false)
        return TarefaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TarefaViewHolder, position: Int) {
        val tarefa = listaTarefas[position]
        holder.titulo.text = tarefa.titulo
        holder.data.text = tarefa.data
        holder.checkbox.isChecked = tarefa.concluida

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            tarefa.concluida = isChecked
        }
    }

    override fun getItemCount(): Int = listaTarefas.size
}