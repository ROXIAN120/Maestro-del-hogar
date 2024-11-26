package com.example.proyectofinal.Pantalla_principal.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.Pantalla_principal.AdaptadorUsuario
import com.example.proyectofinal.Pantalla_principal.Usuario
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adaptador: AdaptadorUsuario
    private var listaUsuarios = arrayListOf<Usuario>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializar la lista de usuarios y configurar el RecyclerView
        llenarLista()
        setupRecyclerView()

        // Configurar el buscador
        binding.etBuscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                filtrar(p0.toString())
            }
        })

        return root
    }

    /**
     * Rellena la lista de usuarios con datos de ejemplo.
     */
    private fun llenarLista() {
        listaUsuarios.add(Usuario("Pintor", "+591 67952526", "eduardo@gmail.com", R.drawable.icono_de_mujer))
        listaUsuarios.add(Usuario("Electricista", "+591 67952526", "luis@gmail.com", R.drawable.icono_de_hombre))
        listaUsuarios.add(Usuario("Jardinero", "+591 67952526", "Sergio@gmail.com", R.drawable.icono_de_hombre))
        listaUsuarios.add(Usuario("Plomero", "+591 67952526", "Cesar@gmail.com", R.drawable.icono_de_hombre))
        listaUsuarios.add(Usuario("Limpieza", "+591 67952526", "Laura@gmail.com", R.drawable.icono_de_mujer))

    }

    /**
     * Configura el RecyclerView con la lista de usuarios.
     */
    private fun setupRecyclerView() {
        binding.rvLista.layoutManager = LinearLayoutManager(context)
        adaptador = AdaptadorUsuario(listaUsuarios, requireContext())
        binding.rvLista.adapter = adaptador
    }

    /**
     * Filtra la lista de usuarios según el texto introducido en el buscador.
     */
    private fun filtrar(texto: String) {
        val listaFiltrada = arrayListOf<Usuario>()
        listaUsuarios.forEach {
            if (it.nombre.lowercase().contains(texto.lowercase())) {
                listaFiltrada.add(it)
            }
        }
        adaptador.filtrar(listaFiltrada)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}