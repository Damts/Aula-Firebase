package com.damts.aulafirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.damts.aulafirebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }
    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnExecutar.setOnClickListener {

//            cadastroUsuario()
//            logarUsuario()
//            salvarDados()
//            atualizarRemoverDados()
//            listarDados()
            pesquisarDados()

        }
    }

    private fun pesquisarDados() {

        val refUsuarios = bancoDados
            .collection("usuarios")
//            .whereEqualTo("nome", "Paola")
//            .whereNotEqualTo("nome", "Paola")
//            .whereIn("nome", listOf("Mateus Assis", "Paola"))
//            .whereNotIn("nome", listOf("Mateus Assis", "Paola"))
//            .whereArrayContains("conhecimentos", "kotlin")

//            .whereGreaterThan("idade", "25")
//            .whereGreaterThanOrEqualTo("idade", "25")
//            .whereLessThan("idade", "25")
//            .whereLessThanOrEqualTo("idade", "25")
//            .whereGreaterThanOrEqualTo("idade", "25")
//            .whereLessThanOrEqualTo("idade", "30")
//            .orderBy("idade", Query.Direction.ASCENDING)//Ordenando do maior para menor ou A-Z
            .orderBy("idade", Query.Direction.DESCENDING)//Ordenando do menor para o maior ou Z-A

        refUsuarios

            .addSnapshotListener { querySnapshot, erro ->

                val listaDocuments = querySnapshot?.documents

                var listaResultado = ""
                listaDocuments?.forEach { documentSnapshot ->
                    val dados = documentSnapshot?.data
                    if ( dados != null ) {
                        val nome = dados["nome"]
                        val idade = dados["idade"]

                        listaResultado += "nome: $nome - idade: $idade \n"
                    }
                }
                binding.txtResultado.text = listaResultado

            }

    }

    private fun salvarDadosUsuario( nome: String, idade: String){
        val idUsuarioLogado = autenticacao.currentUser?.uid
        if ( idUsuarioLogado != null ){

            val dados = mapOf(
                "nome" to nome,
                "idade" to idade
            )
            bancoDados
                .collection("usuarios")
                .document( idUsuarioLogado )
                .set(dados)
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
        }
    }

    private fun listarDados() {

//        salvarDadosUsuario("Gabriela Kiyuna", "26")
        val idUsuarioLogado = autenticacao.currentUser?.uid
        if( idUsuarioLogado != null ) {

            val referenciaUsuario = bancoDados
                .collection("usuarios")
//                .document( idUsuarioLogado )

            referenciaUsuario
                .addSnapshotListener { querySnapshot, erro ->

                    val listaDocuments = querySnapshot?.documents

                    var listaResultado = ""
                    listaDocuments?.forEach { documentSnapshot ->
                        val dados = documentSnapshot?.data
                        if ( dados != null ) {
                            val nome = dados["nome"]
                            val idade = dados["idade"]

                            listaResultado += "nome: $nome - idade: $idade \n"
                        }
                    }
                    binding.txtResultado.text = listaResultado

                    //Recuperando um item especifico atraves do id Usuario Logado
                    /*val dados = documentSnapshot?.data
                    if ( dados != null ) {
                        val nome = dados["nome"]
                        val idade = dados["idade"]
                        val texto = "nome: $nome idade: $idade"

                        binding.txtResultado.text = texto
                    }*/
                }

            /*referenciaUsuario
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val dados = documentSnapshot.data
                    if ( dados != null ){
                        val nome = dados["nome"]
                        val idade = dados["idade"]
                        val texto = "nome: $nome idade: $idade"

                        binding.txtResultado.text = texto
                    }
                }
                .addOnFailureListener {  }*/
        }
    }

    private fun atualizarRemoverDados() {

        val dados = mapOf(
            "nome" to "gabriela",
            "idade" to "26"
//            "cpf" to "123..."
        )

        val idUsuarioLogado = autenticacao.currentUser?.uid
       /* if (idUsuarioLogado != null){
            val referenciaUsuario = bancoDados
                .collection( "usuarios" )
//                .document("1")

//        referenciaUsuario.set( dados )
            referenciaUsuario
//            .update("nome", "gabriela kiyuna")
//            .delete()
                .add( dados )
                .addOnSuccessListener {
                    exibirMensagem("Usuario atualizado com sucesso")
                }.addOnFailureListener { exception ->
                    exibirMensagem("Erro ao atualizar usuario")
                }
        }*/

        val referenciaUsuario = bancoDados
            .collection( "usuarios" )
//                .document("1")

//        referenciaUsuario.set( dados )
        referenciaUsuario
//            .update("nome", "gabriela kiyuna")
//            .delete()
            .add( dados )
            .addOnSuccessListener {
                exibirMensagem("Usuario atualizado com sucesso")
            }.addOnFailureListener { exception ->
                exibirMensagem("Erro ao atualizar usuario")
            }

    }

    private fun salvarDados() {

        val dados = mapOf(
            "nome" to "gabriela",
            "idade" to "26",
            "cpf" to "123..."
        )

        bancoDados
            .collection("usuarios")
            .document("2")
            .set( dados )
            .addOnSuccessListener {
                exibirMensagem("Usuario salvo com sucesso")
            }.addOnFailureListener { exception ->
                exibirMensagem("Erro ao salvar usuario")
            }

    }

    override fun onStart() {
        super.onStart()
        logarUsuario()
//        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {

//        autenticacao.signOut()
        val usuario = autenticacao.currentUser
        val id = usuario?.uid
        if( usuario != null ){
            exibirMensagem("Usuario esta logado com id: $id")
            startActivity(
                Intent(this, PrincipalActivity::class.java)
            )
        }else{
            exibirMensagem("Não tem usuario logado")
        }
    }

    private fun logarUsuario() {

        //Dados digitados pelo usuario
        val email = "gabriela.kyiuna@gmail.com"
        val senha = "mateus12@"

        //Estivessse em uma tela de login
        autenticacao.signInWithEmailAndPassword(
            email, senha
        ).addOnSuccessListener { authResult ->
            binding.txtResultado.text = "Sucesso ao logar usuario"
            startActivity(
                Intent(this, UploadImagemActivity::class.java)
            )
        }.addOnFailureListener { exception ->
            binding.txtResultado.text = "Falha ao logar usuario ${exception.message}"
        }
    }

    private fun cadastroUsuario() {

        //Dados digitados pelo usuario
        val email = "Gabriela.Kyiuna@gmail.com"
        val senha = "mateus12@"
        val nome = "Gabriela Kiyuna"
        val idade = "26"

        //Tela de cadastro do seu App
        autenticacao.createUserWithEmailAndPassword(
            email, senha
        ).addOnSuccessListener {authResult ->

            val email = authResult.user?.email
            val id = authResult.user?.uid

            //Salvar mais dados do usuario no banco de dados
            salvarDadosUsuario(nome, idade)


//            exibirMensagem("Sucesso ao cadastrar usuario: $id - $email")
            binding.txtResultado.text = "sucesso: $id - $email"
        }.addOnFailureListener {exception ->
            val mensagemErro = exception.message
            binding.txtResultado.text = "Erro: $mensagemErro"
        }

    }

    private fun exibirMensagem(texto: String) {
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show()
    }
}