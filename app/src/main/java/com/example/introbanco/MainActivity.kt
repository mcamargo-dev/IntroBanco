package com.example.introbanco

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.introbanco.ui.theme.IntroBancoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IntroBancoTheme {
                TelaPrincipal()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPrincipal() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val filmesDao = remember { db.filmesDAO() }
    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var listaFilmes by remember { mutableStateOf<List<Filmes>>(emptyList()) }

    // Função para carregar os filmes do banco
    fun atualizarLista() {
        scope.launch {
            listaFilmes = buscarFilmes(filmesDao)
        }
    }

    // Carrega a lista ao iniciar
    LaunchedEffect(Unit) {
        atualizarLista()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CineList 2025", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card de Entrada
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Novo Filme", style = MaterialTheme.typography.titleMedium)
                    
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Button(
                        onClick = {
                            if (nome.isNotBlank()) {
                                scope.launch {
                                    inserirFilme(nome, desc, filmesDao)
                                    nome = ""
                                    desc = ""
                                    atualizarLista()
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Salvar")
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Minha Coleção",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Lista Fluida
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(listaFilmes) { filme ->
                    FilmeItem(filme)
                }
            }
        }
    }
}

@Composable
fun FilmeItem(filme: Filmes) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = filme.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (filme.desc.isNotBlank()) {
                Text(text = filme.desc, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

suspend fun buscarFilmes(filmesDao: FilmesDAO): List<Filmes> = withContext(Dispatchers.IO) {
    try {
        filmesDao.buscarTodosFilmes()
    } catch (e: Exception) {
        Log.e("DB_ERROR", "Erro ao buscar: ${e.message}")
        emptyList()
    }
}

suspend fun inserirFilme(nome: String, desc: String, filmesDao: FilmesDAO) = withContext(Dispatchers.IO) {
    try {
        filmesDao.inserir(Filmes(0, nome, desc))
    } catch (e: Exception) {
        Log.e("DB_ERROR", "Erro ao inserir: ${e.message}")
    }
}
