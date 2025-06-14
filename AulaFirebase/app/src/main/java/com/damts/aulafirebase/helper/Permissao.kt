package com.damts.aulafirebase.helper

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permissao {

    companion object{

        fun requisitarPermissoes(activity: Activity, permissoes: List<String>, requestCode: Int){

            //Verificar permissoes negadas, para entao solicictar
            val permissoesNegadas = mutableListOf<String>()
            permissoes.forEach{permissao ->
                val temPermissao = ContextCompat.checkSelfPermission(
                    activity, permissao
                ) == PackageManager.PERMISSION_GRANTED

                if ( !temPermissao )
                    permissoesNegadas.add(permissao)
            }


            //Requisitar permissoes negadas pelo usuario
           if (permissoesNegadas.isNotEmpty()){
               ActivityCompat.requestPermissions(
                   activity, permissoesNegadas.toTypedArray(), requestCode
               )
           }
        }

    }

}