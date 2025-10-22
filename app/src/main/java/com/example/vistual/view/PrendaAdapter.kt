package com.example.vistual.view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.vistual.R
import com.example.vistual.models.Prenda

class PrendaAdapter(private var prendas: MutableList<Prenda>) : RecyclerView.Adapter<PrendaAdapter.PrendaViewHolder>() {


    inner class PrendaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_prenda)
    }

    // Se llama para crear un nuevo ViewHolder (cuando no hay uno reciclable).
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrendaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prenda_carrusel, parent, false)
        return PrendaViewHolder(view)
    }

    // Se llama para vincular los datos de una prenda a un ViewHolder.
    override fun onBindViewHolder(holder: PrendaViewHolder, position: Int) {
        val prenda = prendas[position]
        // Carga la imagen desde la URI guardada en la base de datos.
        holder.imageView.setImageURI(Uri.parse(prenda.rutaImagen))
    }

    // Devuelve el número total de items en la lista.
    override fun getItemCount() = prendas.size

    // Una función útil para actualizar la lista de prendas desde MainActivity.
    fun actualizarPrendas(nuevasPrendas: List<Prenda>) {
        prendas.clear()
        prendas.addAll(nuevasPrendas)
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado y debe redibujarse.
    }
}
    