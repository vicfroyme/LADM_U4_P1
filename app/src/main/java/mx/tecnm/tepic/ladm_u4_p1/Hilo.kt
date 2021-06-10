package mx.tecnm.tepic.ladm_u4_p1

import android.widget.Toast

class Hilo(p: MainActivity) : Thread() {
    var puntero = p
    var tamanoReal = 0
    var tamTemp = 0
    var contador=0

    override fun run() {
        super.run()

        while (true){
            sleep(2000)
            puntero.runOnUiThread {
                tamTemp = puntero.tam
                puntero.displayLog()
                tamanoReal = puntero.tam

                if (tamanoReal > tamTemp){
                    //se compara la lista de perdidas con el numero entrante
                    Toast.makeText(puntero,"Llamada entrante",Toast.LENGTH_SHORT).show()
                    puntero.compararNumeros()


                }else{
                    Toast.makeText(puntero,"Detectando llamadas",Toast.LENGTH_SHORT).show()
                }
            }//runOnUI
        }//while
    }//run

}// class