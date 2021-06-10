package mx.tecnm.tepic.ladm_u4_p1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.*

class Main2Activity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var dataLista = ArrayList<String>()
    var listaID = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        mostrarTodo()
        btnBuscar.setOnClickListener {
            if(radioDeseado.isChecked){
                mostrarDeseados()
            }//deseado
            if (radioNoDeseado.isChecked){
                mostrarNoDeseados()
            }//nodeseado
            if (radioTodos.isChecked){
                mostrarTodo()
            }//todos
        }//buscar

    }//onCreate

    fun mostrarTodo() {
        baseRemota.collection("contactos")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    //si hay error
                    Toast.makeText(this, "Error no se puede acceder a consulta", Toast.LENGTH_LONG)
                        .show()
                    return@addSnapshotListener
                }//if
                dataLista.clear()
                listaID.clear()
                for (document in querySnapshot!!) {
                    var cadena = "Nombre: " +document.getString("name") + "\n" +
                            "Teléfono: "+document.getString("phone") + "\n"+
                            "Estado: " + document.getString("estado")
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }//for
                if (dataLista.size == 0) {
                    dataLista.add("No hay data")
                }//if
                var adaptador =
                    ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataLista)
                lista.adapter = adaptador
            }//addsnap

        lista.setOnItemClickListener { parent, view, position, id ->
            if (listaID.size == 0) {
                return@setOnItemClickListener
            }//if

        }//lista
    }//mostrar todos

    fun mostrarDeseados() {
        baseRemota.collection("contactos")
            .whereEqualTo("estado","deseado")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    //si hay error
                    Toast.makeText(this, "Error no se puede acceder a consulta", Toast.LENGTH_LONG)
                        .show()
                    return@addSnapshotListener
                }//if
                dataLista.clear()
                listaID.clear()
                for (document in querySnapshot!!) {
                    var cadena = "Nombre: " +document.getString("name") + "\n" +
                            "Teléfono: "+document.getString("phone") + "\n"+
                            "Estado: " + document.getString("estado")
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }//for
                if (dataLista.size == 0) {
                    dataLista.add("No hay data")
                }//if
                var adaptador =
                    ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataLista)
                lista.adapter = adaptador
            }//addsnap

        lista.setOnItemClickListener { parent, view, position, id ->
            if (listaID.size == 0) {
                return@setOnItemClickListener
            }//if

        }//lista
    }//mostrar deseados

    fun mostrarNoDeseados() {
        baseRemota.collection("contactos")
            .whereEqualTo("estado","No deseado")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    //si hay error
                    Toast.makeText(this, "Error no se puede acceder a consulta", Toast.LENGTH_LONG)
                        .show()
                    return@addSnapshotListener
                }//if
                dataLista.clear()
                listaID.clear()
                for (document in querySnapshot!!) {
                    var cadena = "Nombre: " +document.getString("name") + "\n" +
                            "Teléfono: "+document.getString("phone") + "\n"+
                            "Estado: " + document.getString("estado")
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }//for
                if (dataLista.size == 0) {
                    dataLista.add("No hay data")
                }//if
                var adaptador =
                    ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataLista)
                lista.adapter = adaptador
            }//addsnap

        lista.setOnItemClickListener { parent, view, position, id ->
            if (listaID.size == 0) {
                return@setOnItemClickListener
            }//if

        }//lista
    }//mostrar no deseados
}