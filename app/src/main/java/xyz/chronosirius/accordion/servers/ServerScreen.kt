package xyz.chronosirius.accordion.servers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import xyz.chronosirius.accordion.R
import xyz.chronosirius.accordion.data.DataArray
import xyz.chronosirius.accordion.viewmodels.ServerListViewModel

@Composable
fun ServerScreen(
    navController: NavController,
    vm: ServerListViewModel
) {
    val state by vm.uiState.collectAsState()
    Scaffold(
        topBar = {
            Text("Servers")
            Spacer(modifier = Modifier.fillMaxWidth(0.8f))
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = "Search"
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid (
            modifier = Modifier.padding(paddingValues),
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items(state.folders.count()) { i ->
                val folder = state.folders[i]
                if (folder.guilds.count() == 1) {
                    val guild = folder.guilds[0]
                    Card(
                        modifier = Modifier.fillMaxSize()
                            .padding(10.dp),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Text(guild.name, modifier = Modifier.padding(bottom = 5.dp))
                        guild.icon?.let { icon ->
                            AsyncImage(
                                model = "https://cdn.discordapp.com/icons/${guild.id}/${icon}.webp?size=512",
                                contentDescription = "Guild Icon",
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(bottom = 5.dp)
                                    .clip(CircleShape),
                            )
                        }
                    }
                }



            }
        }
    }
}