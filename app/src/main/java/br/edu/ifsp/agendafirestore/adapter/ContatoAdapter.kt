package br.edu.ifsp.agendafirestore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.agendafirestore.R
import br.edu.ifsp.agendafirestore.model.Contato
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ContatoAdapter(options: FirestoreRecyclerOptions<Contato>)
    : FirestoreRecyclerAdapter<Contato, ContatoAdapter.ContatoViewHolder>(options)
{
    var clickListener: ContatoClickListener?=null

    inner class ContatoViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val nomeVH = view.findViewById<TextView>(R.id.nome)
        val foneVH = view.findViewById<TextView>(R.id.fone)
        init {
            view.setOnClickListener { clickListener?.onItemClick(bindingAdapterPosition) }
        }
    }

    interface  ContatoClickListener
    {
        fun onItemClick(pos:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contato_celula, parent, false)
        return ContatoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContatoViewHolder, position: Int, model: Contato) {
        holder.nomeVH.text = model.nome
        holder.foneVH.text = model.fone

    }


}