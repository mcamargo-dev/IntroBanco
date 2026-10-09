package com.example.introbanco

import android.R.attr.id
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.introbanco.ui.theme.IntroBancoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IntroBancoTheme {

                TelaCadastroFilmes()

            }
        }
    }
}

@Preview
@Composable
fun TelaCadastroFilmes(){

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    var filmes by remember { mutableStateOf<List<Filmes>>(emptyList()) }

    val contex = LocalContext.current
    val db = AppDatabase.getDatabase(contex)
    val filmesDao = db.filmesDao()

    LaunchedEffect(Unit) {
        filmes = buscarFilmes(filmesDao)
        Log.d("Busca ok", "... ${filmes}")
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier.fillMaxWidth()
                .padding(10.dp)
                .background(Color.LightGray, RectangleShape),
            border = BorderStroke(1.dp, Color.Black)
        ) {

            TextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do Filme") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descricao") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if(nome.isNotBlank() && descricao.isNotBlank()){
                        CoroutineScope(Dispatchers.IO).launch {
                            inserirFilme(nome, descricao, filmesDao)
                        }
                    }
                }
            ) {
                Text("Adicionar Filme")
            }

        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn {
            items(filmes){
                    filme ->
                umFilme(filme.id, filme.nome, filme.desc)
            }
        }


    }

}

@Preview
@Composable
fun umFilme(id:Int = 0, nome: String = "Nome aqui", desc: String = "Descricao aqui"){
    Card(
        modifier = Modifier.height(80.dp).fillMaxWidth().padding(5.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Row(
            modifier = Modifier.padding(10.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "$id", style = MaterialTheme.typography.bodyMedium)

            Column {
                Text(text = nome, style = MaterialTheme.typography.titleLarge)
                Text(text = desc, style = MaterialTheme.typography.titleSmall)
            }

            Icon(Icons.Default.Edit, "", modifier = Modifier.size(30.dp))
            Icon(Icons.Default.Close, "", modifier = Modifier.size(30.dp))
        }
    }
}

suspend fun buscarFilmes(filmesDao: FilmesDAO): List<Filmes> {
    return try {
        filmesDao.buscarTodosFilmes()
    } catch (e: Exception) {
        Log.e("Erro ao buscar", "${e.message}")
        emptyList()
    }
}

suspend fun inserirFilme(nome: String, desc: String, filmesDao: FilmesDAO){
    try{
        filmesDao.inserir(Filmes(id = 0, nome = nome, desc = desc))
    }catch (e: Exception){
        Log.e("Erro ao adicionar", "Msg: ${e.message}")
    }
}
