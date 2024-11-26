package com.example.proyectofinal.Pantalla_principal

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R

class AdaptadorUsuario(
    var listaUsuarios: ArrayList<Usuario>,
    private val context: Context
) : RecyclerView.Adapter<AdaptadorUsuario.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val imagenlocal: ImageView = itemView.findViewById(R.id.imagen_del_local)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_usuario, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = listaUsuarios[position]

        holder.tvNombre.text = usuario.nombre
        holder.tvTelefono.text = usuario.telefono
        holder.tvEmail.text = usuario.email
        holder.imagenlocal.setImageResource(usuario.imagen)

        // Configurar el click listener para enviar un mensaje de WhatsApp
        holder.itemView.setOnClickListener {
            val message = when (usuario.nombre) {
                "Pintor" -> "Hola Requiero pintor"
                "Electricista" -> "Hola Requiero electricista"
                "Jardinero" -> "Hola Requiero jardinero"
                "Plomero" -> "Hola Requiero plomero"
                "Limpieza" -> "Hola Requiero servicio de limpieza"
                else -> "Requiero sus servicios"
            }
            sendWhatsAppMessage(usuario.telefono, message)
        }
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    fun filtrar(listaFiltrada: ArrayList<Usuario>) {
        this.listaUsuarios = listaFiltrada
        notifyDataSetChanged()
    }

    // Método para enviar un mensaje de WhatsApp
    private fun sendWhatsAppMessage(phoneNumber: String, message: String) {
        val uri = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
        }
    }
}