package mx.tecnm.tepic.ladm_u4_p1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var isDeseado = false
    var estado = ""
    var dataLista = ArrayList<String>()
    var dataListaND = ArrayList<String>()
    var listaID = ArrayList<String>()
    var listaLlamadas = ArrayList<String>()
    var idA = ""
    var numero = ""
    var dataListaNumero = ArrayList<String>()
    var dataListaEstado = ArrayList<String>()
    val READCALLLOG = 1
    val MENSAJESENVIAR=2
    var tam = 0
    var cols =
        listOf<String>(CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.TYPE).toTypedArray()
    var listaAux = ArrayList<String>()

    var listaMensajesDeseados = ArrayList<String>()
    var listaMensajesIndeseados = ArrayList<String>()
    var hilo: Hilo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //---------------------------PERMISOS------------------
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                Array(1) { Manifest.permission.READ_CALL_LOG },
                READCALLLOG)
        }//READ_CALL_LOG
        else {
            displayLog()
        }//else
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS),MENSAJESENVIAR)
        }
//INICIAR HILO

        hilo = Hilo(this)
        hilo!!.start()


        //carga los mensajes personalizados
        obtenerMensajes()
///////////////////////////BOTONES/////////////////////
        btnGuardar.setOnClickListener {
            insertarcontacto()
        }//btnGuardar

        btnMensajes.setOnClickListener {

            insertarMensajes()

        }//btnMensaje


        btnLista.setOnClickListener {
            var v = Intent(this, Main2Activity::class.java)
            startActivity(v)
        }//verLista
/////////////////// ///////BOTONES///////////////////////////
    }//onCreate

    private fun insertarMensajes() {
        var datosInsertar = hashMapOf(
            "deseado" to editDeseado.text.toString(),
            "noDeseado" to editNoDeseado.text.toString()
        )//datosInsertar

        baseRemota.collection("mensajes")
            .add(datosInsertar)
            .addOnSuccessListener {
                Toast.makeText(this, "Mensajes guardados", Toast.LENGTH_LONG)
                    .show()
            }//success
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo guardar el mensaje", Toast.LENGTH_LONG)
                    .show()
            }//fail
    }

    private fun insertarcontacto() {
        isDeseado = checkDeseado.isChecked
        if (isDeseado) {
            estado = "deseado"
        } else {
            estado = "No deseado"
        }//else
        var datosInsertar = hashMapOf(
            "name" to editNombre.text.toString(),
            "phone" to editTelefono.text.toString(),
            "estado" to estado
        )//datosInsertar

        baseRemota.collection("contactos")
            .add(datosInsertar)
            .addOnSuccessListener {
                Toast.makeText(this, "Se registró contacto", Toast.LENGTH_LONG)
                    .show()
            }//success
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo registrar el contacto", Toast.LENGTH_LONG)
                    .show()
            }//fail
    }//insertarContacto

    fun obtenerMensajes() {
        baseRemota.collection("mensajes")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    //si hay error
                    Toast.makeText(this, "Error no se puede acceder a consulta", Toast.LENGTH_LONG)
                        .show()
                    return@addSnapshotListener
                }//if
                dataLista.clear()
                dataListaND.clear()
                for (document in querySnapshot!!) {
                    var deseado = document.getString("deseado").toString()
                    var noDeseado = "" + document.getString("noDeseado")
                    dataLista.add(deseado)
                    dataListaND.add(noDeseado)
                    listaID.add(document.id)
                }//for
                if (dataListaEstado.size == 0) {

                }//if
                var adaptador =
                    ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataLista)
                listamsg.adapter = adaptador
                var adaptador2 =
                    ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataListaND)
                listaND.adapter = adaptador2
            }//addsnap

        listamsg.setOnItemClickListener { parent, view, position, id ->
            if (listaID.size == 0) {
                return@setOnItemClickListener
            }//if
            editDeseado.setText("" + dataLista[position])
        }//lista
        listaND.setOnItemClickListener { parent, view, position, id ->
            if (listaID.size == 0) {
                return@setOnItemClickListener
            }//if
            editNoDeseado.setText("" + dataListaND[position])
        }//lista


    }//obtener mensajes


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READCALLLOG && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            displayLog()
        }//IF
    }

    @SuppressLint("MissingPermission")
    fun displayLog() {
        var listaLlamadasPerdidas = ArrayList<String>()
        var tipo = "3"
        var lista = ""
        listaAux.clear()


        var cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, cols, CallLog.Calls.TYPE + " = ?", arrayOf<String>(tipo.toString()), null)
        if (cursor!!.moveToFirst()) {
            var posTelephone = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            do {
                val telephone = cursor.getString(posTelephone)
                numero = "" + telephone
                lista += "" + telephone
                listaLlamadasPerdidas.add(numero)
                listaAux.add(lista)
            } while (cursor.moveToNext())
        }//if
        tam = listaAux.size


    }//display

    fun compararNumeros() {
        baseRemota.collection("contactos")
            .whereEqualTo("phone", numero)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    //si hay error
                    Toast.makeText(this, "Error no se puede acceder a consulta", Toast.LENGTH_LONG)
                        .show()
                    return@addSnapshotListener
                }//if
                dataListaEstado.clear()
                listaID.clear()
                for (document in querySnapshot!!) {
                    dataListaEstado.add(document.getString("estado")!!)
                }//for
                if (dataListaEstado.size != 0) {
                    (0..dataListaEstado.size - 1).forEach {

                        if (dataListaEstado[it] == "deseado") {
                            Toast.makeText(this, "Contacto encontrado en la lista de deseados", Toast.LENGTH_SHORT).show()
                            mandarMensaje(1, numero)
                        }
                        if (dataListaEstado[it] == "No deseado") {
                            Toast.makeText(this, "El contacto es no deseado", Toast.LENGTH_SHORT)
                                .show()
                            mandarMensaje(2, numero)
                        }

                    }//forEach
                }//if
            }//addsnap
    }//compararnums

    private fun mandarMensaje(i: Int, number: String) {

        var mensaje = ""
        var tamaño = 0
        when (i) {
            1 -> {
                baseRemota.collection("mensajes")
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (firebaseFirestoreException != null) {
                            //si hay error
                            Toast.makeText(this, "Error no se puede acceder a consulta", Toast.LENGTH_LONG)
                                .show()
                            return@addSnapshotListener
                        }//if
                        listaMensajesDeseados.clear()
                        for (document in querySnapshot!!) {
                            var deseado = document.getString("deseado").toString()
                            listaMensajesDeseados.add(deseado)

                        }//for
                        tamaño =  listaMensajesDeseados.size-1

                        mensaje =  listaMensajesDeseados[tamaño]
                        Toast.makeText(this,mensaje, Toast.LENGTH_SHORT).show()

                        ///////////////////////////// ENVIO DE MENSAJE/////////////////////////////////
                        SmsManager.getDefault()
                            .sendTextMessage(number, null, mensaje, null, null)
                        Toast.makeText(this, "Se envio sms", Toast.LENGTH_SHORT).show()


                    }//addsnap

            }//1
            2 -> {
                baseRemota.collection("mensajes")
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (firebaseFirestoreException != null) {
                            //si hay error
                            Toast.makeText(this, "Error no se puede acceder a consulta", Toast.LENGTH_LONG)
                                .show()
                            return@addSnapshotListener
                        }//if
                        listaMensajesIndeseados.clear()
                        for (document in querySnapshot!!) {
                            var indeseado = document.getString("noDeseado").toString()
                            listaMensajesIndeseados.add(indeseado)

                        }//for
                        tamaño = listaMensajesIndeseados.size-1
                        //Toast.makeText(this,"${tamaño}", Toast.LENGTH_SHORT).show()
                        mensaje = listaMensajesIndeseados[tamaño]
                        Toast.makeText(this,mensaje, Toast.LENGTH_SHORT).show()
                        ///////////////////////////// ENVIO DE MENSAJE/////////////////////////////////
                        SmsManager.getDefault()
                            .sendTextMessage(number, null, mensaje, null, null)
                        Toast.makeText(this, "Se envio sms", Toast.LENGTH_SHORT).show()
                    }//addsnap
            }//2
        }//when

    }//mandarMensaje
}//main



