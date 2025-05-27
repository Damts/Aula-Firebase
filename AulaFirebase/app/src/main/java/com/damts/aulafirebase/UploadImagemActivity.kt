package com.damts.aulafirebase

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.damts.aulafirebase.databinding.ActivityUploadImagemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.damts.aulafirebase.databinding.ActivityPrincipalBinding
import com.damts.aulafirebase.helper.Permissao
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.UUID

class UploadImagemActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityUploadImagemBinding.inflate( layoutInflater )
    }

    private val autenticao by lazy {
        FirebaseAuth.getInstance()
    }

    private val armazenamento by lazy {
        FirebaseStorage.getInstance()
    }

    private var uriImagemSelecionada: Uri? = null
    private var bitmapImagemSelecionada: Bitmap? = null

    private val abrirGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){ uri ->
        if(uri != null){
            binding.imageSelecionada.setImageURI( uri )
            uriImagemSelecionada = uri
            Toast.makeText(this, "imagem selecionada", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
        }
    }

    private val abrirCamera = registerForActivityResult(
        //ActivityResultContracts.GetContent()//Você (pintor)
        ActivityResultContracts.StartActivityForResult()//Jamilton (mecânico)
    ){ resultadoActvity ->
        //if( resultadoActvity.resultCode == RESULT_OK ){ }else{}
        bitmapImagemSelecionada = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            resultadoActvity.data?.extras
                ?.getParcelable("data", Bitmap::class.java)
        }else{
            resultadoActvity.data?.extras
                ?.getParcelable("data")
        }
        binding.imageSelecionada.setImageBitmap( bitmapImagemSelecionada )

    }
    private val permissoes = listOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.i("permissao_app", "requestCode: $requestCode")

        permissions.forEachIndexed { indice, valor ->
            Log.i("permissao_app", "permission: $indice) $valor")
        }

        grantResults.forEachIndexed { indice, valor ->
            Log.i("permissao_app", "concedida: $indice) $valor")
        }


    }*/



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        solicitarPermissoes()

        /*Permissao.requisitarPermissoes(
            this, permissoes, 100
        )*/
        //100 camera - 200 galeria

        binding.btnGaleria.setOnClickListener {
            if( temPermissaoGaleria ){
                abrirGaleria.launch("image/*")//Mime Type
            }else{
                Toast.makeText(this, "Você não tem permissão de galeria", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCamera.setOnClickListener {
            if( temPermissaoCamera ){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                abrirCamera.launch( intent )
            }else{
                Toast.makeText(this, "Você não tem permissão de galeria", Toast.LENGTH_SHORT).show()
            }


        }

        binding.btnUpload.setOnClickListener {
            //uploadGaleria()
            uploadCamera()
        }

        binding.btnRecuperar.setOnClickListener {
            recuperarImagemFirebase()
        }


    }

    private fun solicitarPermissoes() {

        //Verificar permissões que o usuário já tem
        val permissoesNegadas = mutableListOf<String>()
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if ( !temPermissaoCamera )
            permissoesNegadas.add(android.Manifest.permission.CAMERA)

        if ( !temPermissaoGaleria )
            permissoesNegadas.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        //Solicitar permissões
        if( permissoesNegadas.isNotEmpty() ){

            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){ permissoes: Map<String, Boolean> ->
                //camera - true
                Log.i("novas_permissoes", "permissoes: $permissoes")
                temPermissaoCamera = permissoes[android.Manifest.permission.CAMERA]
                    ?: temPermissaoCamera

                temPermissaoGaleria = permissoes[android.Manifest.permission.READ_EXTERNAL_STORAGE]
                    ?: temPermissaoGaleria

            }
            gerenciadorPermissoes.launch( permissoesNegadas.toTypedArray() )

        }

    }

    private fun recuperarImagemFirebase() {

        val idUsuarioLogado = autenticao.currentUser?.uid
        if( idUsuarioLogado != null ){

            armazenamento
                .getReference("fotos")
                .child(idUsuarioLogado)
                .child("foto.jpg")
                .downloadUrl
                .addOnSuccessListener { urlFirebase ->
                    //binding.imageRecuperada.setImageURI( urlFirebase )
                    Picasso.get()
                        .load( urlFirebase )
                        .into(binding.imageRecuperada)
                }

        }

    }

    private fun uploadCamera() {

        /*
        documentos
        videos
        fotos
            <id_usuario_logado>
                + foto.jpg
        * */
        val idUsuarioLogado = autenticao.currentUser?.uid
        //val nomeImagem = UUID.randomUUID().toString()

        val outputStream = ByteArrayOutputStream()
        bitmapImagemSelecionada?.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            outputStream
        )

        if(  bitmapImagemSelecionada != null && idUsuarioLogado != null ){
            armazenamento
                .getReference("fotos")
                .child(idUsuarioLogado)
                .child("foto.jpg")
                .putBytes( outputStream.toByteArray() )
                .addOnSuccessListener { task ->
                    Toast.makeText(this, "Sucessoao fazer upload da imagem", Toast.LENGTH_SHORT).show()
                    task.metadata?.reference?.downloadUrl
                        ?.addOnSuccessListener { urlFirebase ->
                            Toast.makeText(this, urlFirebase.toString(), Toast.LENGTH_SHORT).show()
                        }

                }.addOnFailureListener{ erro ->
                    Toast.makeText(this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show()
                }

        }



    }

    private fun uploadGaleria() {

        /*
        documentos
        videos
        fotos
            <id_usuario_logado>
                + foto.jpg
        * */
        val idUsuarioLogado = autenticao.currentUser?.uid
        val nomeImagem = UUID.randomUUID().toString()
        if(  uriImagemSelecionada != null && idUsuarioLogado != null ){
            armazenamento
                .getReference("fotos")
                .child(idUsuarioLogado)
                .child("foto.jpg")
                .putFile( uriImagemSelecionada!! )
                .addOnSuccessListener { task ->
                    Toast.makeText(this, "Sucessoao fazer upload da imagem", Toast.LENGTH_SHORT).show()
                    task.metadata?.reference?.downloadUrl
                        ?.addOnSuccessListener { urlFirebase ->
                            Toast.makeText(this, urlFirebase.toString(), Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener{ erro ->
                    Toast.makeText(this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show()
                }

        }



    }
}