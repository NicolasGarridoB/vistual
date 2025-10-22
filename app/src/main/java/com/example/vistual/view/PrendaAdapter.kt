package com.example.vistual.view

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.vistual.R
import com.example.vistual.models.DBHelper
import com.example.vistual.models.Prenda
import java.io.File

class PrendaAdapter(
    private val context: Context, 
    private var prendas: MutableList<Prenda>,
    private val dbHelper: DBHelper,
    @LayoutRes private val layoutId: Int // NEW: Layout resource ID
) : RecyclerView.Adapter<PrendaAdapter.PrendaViewHolder>() {

    inner class PrendaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_prenda)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrendaViewHolder {
        // Use the layoutId passed in the constructor
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return PrendaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrendaViewHolder, position: Int) {
        if (prendas.isEmpty()) {
            return
        }

        val realPosition = position % prendas.size
        val prenda = prendas[realPosition]

        // --- Carga de la imagen (sin cambios) ---
        try {
            if (prenda.rutaImagen.startsWith("/")) {
                val file = File(prenda.rutaImagen)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(prenda.rutaImagen)
                    holder.imageView.setImageBitmap(bitmap)
                } else {
                    holder.imageView.setImageResource(R.color.teal_200) 
                }
            } else {
                try {
                    holder.imageView.setImageURI(Uri.parse(prenda.rutaImagen))
                } catch (e: Exception) {
                    holder.imageView.setImageResource(R.color.teal_200)
                }
            }
        } catch (e: Exception) {
            holder.imageView.setImageResource(R.color.teal_200)
        }

        // --- Listener para eliminar la prenda ---
        holder.itemView.setOnLongClickListener {
            val currentInfinitePosition = holder.adapterPosition
            if (currentInfinitePosition == RecyclerView.NO_POSITION || prendas.isEmpty()) {
                return@setOnLongClickListener true
            }

            val currentRealPosition = currentInfinitePosition % prendas.size
            val prendaAEliminar = prendas[currentRealPosition]

            AlertDialog.Builder(context)
                .setTitle("Eliminar Prenda")
                .setMessage("¿Estás seguro de que quieres eliminar esta prenda?")
                .setPositiveButton("Eliminar") { _, _ ->
                    val exito = dbHelper.eliminarPrenda(prendaAEliminar.id)
                    if (exito) {
                        prendas.remove(prendaAEliminar)
                        notifyDataSetChanged()
                        Toast.makeText(context, "Prenda eliminada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al eliminar la prenda", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()

            true 
        }
    }

    override fun getItemCount(): Int {
        return if (prendas.isEmpty()) 0 else Integer.MAX_VALUE
    }

    fun actualizarPrendas(nuevasPrendas: List<Prenda>) {
        prendas.clear()
        prendas.addAll(nuevasPrendas)
        notifyDataSetChanged()
    }
}
