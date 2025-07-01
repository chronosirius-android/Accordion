package xyz.chronosirius.accordion.servers

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import sv.lib.squircleshape.SquircleShape
import xyz.chronosirius.accordion.R
import xyz.chronosirius.accordion.data.DataArray
import xyz.chronosirius.accordion.getDefaultAvatar
import xyz.chronosirius.accordion.global_models.GuildUIFolder
import xyz.chronosirius.accordion.viewmodels.ServerListViewModel
import kotlin.math.exp

@OptIn(ExperimentalSharedTransitionApi::class)
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
        var expandedFolderId by remember { mutableStateOf<Long?>(null) }
        val gridState = rememberLazyGridState()
        BackHandler(enabled = expandedFolderId != null) {
            // When the back button is pressed and a folder is expanded,
            // set expandedFolderId to null to trigger the collapse animation.
            expandedFolderId = null
        }
        SharedTransitionLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            AnimatedContent(
                targetState = expandedFolderId,
                label = "server_folder_expansion",
            ) { targetExpandedFolderId ->
                if (targetExpandedFolderId == null) {
                    FullGrid(
                        folders = state.folders,
                        animatedContent = this@AnimatedContent,
                        gridState = gridState,
                        onExpandClick = { folder ->
                            if (folder.id != null) {
                                expandedFolderId = folder.id
                            } else {
                                Log.d("ServerScreen", "Attempted to expand a folder with null ID, opening server")
                                //navController.navigate("server/${folder.guilds.firstOrNull()?.id ?: 0}")
                            }
                        }
                    )
                } else {
                    val expandedFolder = state.folders.firstOrNull { it.id == targetExpandedFolderId }
                    if (expandedFolder != null) {
                        OpenedFolder(
                            folder = expandedFolder,
                            animatedContent = this,
                            onCollapseClick = {
                                expandedFolderId = null
                            }
                        )
                    } else {
                        throw IllegalStateException("Expanded folder with ID $targetExpandedFolderId not found in state")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FullGrid(
    folders: List<GuildUIFolder>,
    gridState: LazyGridState,
    animatedContent: AnimatedVisibilityScope,
    onExpandClick: (GuildUIFolder) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.Center,
        state = gridState,
    ) {
        items(folders) { folder ->
            if (folder.id == null) {
                val guild = folder.guilds[0]
                OutlinedCard(
                    modifier = Modifier.fillMaxSize()
                        .padding(10.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (folder.color != null) Color(folder.color) else CardDefaults.outlinedCardColors().containerColor,
                    ),
                    onClick = {
                        //navController.navigate("server/${guild.id}")
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        guild.icon?.let { icon ->
                            AsyncImage(
                                model = "https://cdn.discordapp.com/icons/${guild.id}/${icon}.webp?size=512",
                                placeholder = painterResource(getDefaultAvatar(guild.id)), // illegal activities
                                error = painterResource(getDefaultAvatar(guild.id)),
                                contentDescription = "Guild Icon",
                                modifier = Modifier
                                    .size(100.dp)
                                    .aspectRatio(1f)
                                    .padding(7.dp)
                                    .sharedElement(
                                        rememberSharedContentState("guild_icon_${guild.id}"),
                                        animatedVisibilityScope = animatedContent,
                                    )
                                    .clip(SquircleShape(40)),
                            )
//                            Image(
//                                painter = painterResource(getDefaultAvatar(guild.id)),
//                                contentDescription = "Guild Icon",
//                                modifier = Modifier
//                                    .size(100.dp)
//                                    .aspectRatio(1f)
//                                    .padding(7.dp)
//                                    .clip(SquircleShape(40))
//                                    .sharedElement(
//                                        rememberSharedContentState("guild_icon_${guild.id}"),
//                                        animatedVisibilityScope = animatedContent,
//                                    ),
//                            )
                        }
                        Text(
                            text = guild.name,
                            modifier = Modifier.padding(5.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                ClosedFolder(
                    folder,
                    animatedContent = animatedContent,
                ) {
                    onExpandClick(folder)
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ClosedFolder(
    folder: GuildUIFolder,
    animatedContent: AnimatedVisibilityScope,
    onExpandClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxSize()
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (folder.color != null) Color(folder.color).copy(alpha = .4f) else CardDefaults.outlinedCardColors().containerColor,
        ),
        onClick = onExpandClick,
        border = BorderStroke(2.dp, if (folder.color != null) Color(folder.color) else MaterialTheme.colorScheme.outline),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .heightIn(max = 100.dp)
                .padding(10.dp)
                .sharedBounds(
                    rememberSharedContentState("inner_folder_${folder.id ?: throw IllegalStateException("ClosedFolder should not be called with fake folder")}"),
                    animatedVisibilityScope = animatedContent,
                ),

            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            items(folder.guilds, key = { it.id }) { guild ->
                AsyncImage(
                    model = "https://cdn.discordapp.com/icons/${guild.id}/${guild.icon}.webp?size=512",
                    placeholder = painterResource(getDefaultAvatar(guild.id)), // illegal activities
                    error = painterResource(getDefaultAvatar(guild.id)),
                    contentDescription = "Guild Icon",
                    modifier = Modifier
                        .size(100.dp)
                        .aspectRatio(1f)
                        .padding(7.dp)
                        .sharedElement(
                            rememberSharedContentState("guild_icon_${guild.id}"),
                            animatedVisibilityScope = animatedContent,
                        )
                        .clip(SquircleShape(40)),
                )
//                Image(
//                    painter = painterResource(getDefaultAvatar(guild.id)),
//                    contentDescription = "Guild Icon",
//                    modifier = Modifier
//                        .size(100.dp)
//                        .aspectRatio(1f)
//                        .padding(7.dp)
//                        .clip(SquircleShape(40))
//                        .sharedElement(
//                            rememberSharedContentState("guild_icon_${guild.id}"),
//                            animatedVisibilityScope = animatedContent,
//                        ),
//                )
            }
        }
        Text(
            text = folder.name ?: "Unnamed Folder",
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.OpenedFolder(
    folder: GuildUIFolder,
    animatedContent: AnimatedVisibilityScope,
    onCollapseClick: () -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .sharedBounds(
                rememberSharedContentState("inner_folder_${folder.id ?: throw IllegalStateException("OpenedFolder should not be called with fake folder")}"),
                animatedVisibilityScope = animatedContent,
            ),
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        stickyHeader {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (folder.color != null) Color(folder.color).copy(alpha = .4f) else CardDefaults.outlinedCardColors().containerColor,
                ),
                onClick = onCollapseClick,
                border = BorderStroke(2.dp, if (folder.color != null) Color(folder.color) else MaterialTheme.colorScheme.outline),
            ) {
                Text(
                    text = folder.name ?: "Unnamed Folder",
                    modifier = Modifier.padding(10.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        items(folder.guilds, key = { it.id }) { guild ->
            OutlinedCard(
                modifier = Modifier.fillMaxSize()
                    .padding(10.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (folder.color != null) Color(folder.color) else CardDefaults.outlinedCardColors().containerColor,
                ),
                onClick = {
                    //navController.navigate("server/${guild.id}")
                }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    guild.icon?.let { icon ->
                        AsyncImage(
                            model = "https://cdn.discordapp.com/icons/${guild.id}/${icon}.webp?size=512",
                            placeholder = painterResource(getDefaultAvatar(guild.id)), // illegal activities
                            error = painterResource(getDefaultAvatar(guild.id)),
                            contentDescription = "Guild Icon",
                            modifier = Modifier
                                .size(100.dp)
                                .aspectRatio(1f)
                                .padding(7.dp)
                                .sharedElement(
                                    rememberSharedContentState("guild_icon_${guild.id}"),
                                    animatedVisibilityScope = animatedContent,
                                )
                                .clip(SquircleShape(40)),
                        )
//                        Image(
//                            painter = painterResource(getDefaultAvatar(guild.id)),
//                            contentDescription = "Guild Icon",
//                            modifier = Modifier
//                                .size(100.dp)
//                                .aspectRatio(1f)
//                                .padding(7.dp)
//                                .clip(SquircleShape(40))
//                                .sharedElement(
//                                    rememberSharedContentState("guild_icon_${guild.id}"),
//                                    animatedVisibilityScope = animatedContent,
//                                ),
//                        )
                    }
                    Text(
                        text = guild.name,
                        modifier = Modifier.padding(5.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}