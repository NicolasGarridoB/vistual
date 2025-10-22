package com.example.vistual.view

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.vistual.R
import java.io.File

class PrendasAdapter(private val context: Context, private var prendas: MutableList<Map<String, String>>) : BaseAdapter() {

    override fun getCount(): Int = prendas.size

    override fun getItem(position: Int): Any = prendas[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_prenda, parent, false)

        val prenda = prendas[position]
        val ivPrenda = view.findViewById<ImageView>(R.id.iv_prenda)
        val tvNombre = view.findViewById<TextView>(R.id.tv_nombre_prenda)
        val tvCategoria = view.findViewById<TextView>(R.id.tv_categoria_prenda)
        val tvColor = view.findViewById<TextView>(R.id.tv_color_prenda)

        // Cargar imagen
        val imagePath = prenda["imagen_path"]
        if (!imagePath.isNullOrEmpty() && File(imagePath).exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            ivPrenda.setImageBitmap(bitmap)
        } else {
            ivPrenda.setImageResource(R.drawable.ic_launcher_foreground)
        }

        tvNombre.text = prenda["nombre"]
        tvCategoria.text = "Categoría: ${prenda["categoria"]}"
        tvColor.text = "Color: ${prenda["color"]}"

        return view
    }

    fun actualizarPrendas(nuevasPrendas: List<Map<String, String>>) {
        prendas.clear()
        prendas.addAll(nuevasPrendas)
        notifyDataSetChanged()
    }
}