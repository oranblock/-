package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FlipCardView
import com.example.ui.theme.CinemaBg
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.PhonoFlicksViewModel

@Composable
fun DeckScreen(
    viewModel: PhonoFlicksViewModel,
    modifier: Modifier = Modifier
) {
    val currentCard by viewModel.currentCard.collectAsState()
    val filteredMovies by viewModel.filteredMovies.collectAsState()
    val deckIndex by viewModel.deckIndex.collectAsState()
    val isFlipped by viewModel.isFlipped.collectAsState()
    val isHintRevealed by viewModel.isHintRevealed.collectAsState()
    val userGuess by viewModel.userGuess.collectAsState()
    val guessFeedback by viewModel.guessFeedback.collectAsState()
    val selectedFilter by viewModel.selectedGenreFilter.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val solvedIds by viewModel.solvedIds.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val speechRate by viewModel.ttsManager.speechRate.collectAsState()

    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpInputText by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    val filterOptions = listOf(
        "All", "Favorites", "Unsolved", "Solved",
        "Sci-Fi", "Action", "Comedy", "Horror", "Drama", "Animation", "Romance"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBg)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NeonIndigo.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonIndigo.copy(alpha = 0.4f)),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "PhonoFlicks",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Mad-Gab Cinema",
                        color = NeonCyan,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Card Counter Pill
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                val totalCount = filteredMovies.size
                val currentNum = if (totalCount > 0) deckIndex + 1 else 0
                Text(
                    text = "Card $currentNum / $totalCount",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("deck_counter_label")
                )
            }
        }

        // Progress Bar
        val progress = if (filteredMovies.isNotEmpty()) {
            (deckIndex + 1).toFloat() / filteredMovies.size.toFloat()
        } else 0f

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .padding(vertical = 1.dp),
            color = NeonCyan,
            trackColor = Color.White.copy(alpha = 0.08f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter chips horizontal scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filterOptions.forEach { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.setGenreFilter(filter)
                    },
                    label = {
                        Text(
                            text = filter,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White.copy(alpha = 0.05f),
                        selectedContainerColor = NeonIndigo,
                        labelColor = TextSecondary,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color.White.copy(alpha = 0.1f),
                        selectedBorderColor = NeonIndigo,
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.testTag("filter_chip_$filter")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Card Scene with horizontal swipe gesture
        var totalDrag by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 440.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (totalDrag > 80f) {
                                viewModel.prevCard()
                            } else if (totalDrag < -80f) {
                                viewModel.nextCard()
                            }
                            totalDrag = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            totalDrag += dragAmount
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            val card = currentCard
            if (card != null) {
                FlipCardView(
                    movie = card,
                    isFlipped = isFlipped,
                    isHintRevealed = isHintRevealed,
                    isFavorite = favoriteIds.contains(card.id),
                    isSolved = solvedIds.contains(card.id),
                    isSpeaking = isSpeaking,
                    speechRate = speechRate,
                    userGuess = userGuess,
                    guessResult = guessFeedback,
                    onFlip = { viewModel.flipCard() },
                    onToggleHint = { viewModel.toggleHint() },
                    onToggleFavorite = { viewModel.toggleFavoriteCurrent() },
                    onToggleSolved = { viewModel.toggleSolvedCurrent() },
                    onSpeak = { viewModel.speakCurrentMisheard() },
                    onCycleSpeechRate = { viewModel.ttsManager.cycleSpeed() },
                    onGuessChange = { viewModel.updateGuessText(it) },
                    onCheckGuess = { viewModel.checkGuess() }
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = CinemaSurface,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No movies match this filter",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try switching to 'All' or clearing filters.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.setGenreFilter("All") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo)
                        ) {
                            Text("Show All 200 Movies")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Button
            IconButton(
                onClick = { viewModel.prevCard() },
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                    .testTag("nav_prev_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Card",
                    tint = TextPrimary
                )
            }

            // Jump to Card # Button
            IconButton(
                onClick = {
                    jumpInputText = ""
                    showJumpDialog = true
                },
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                    .testTag("jump_to_card_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Dialpad,
                    contentDescription = "Jump to Card",
                    tint = NeonCyan
                )
            }

            // Shuffle Button
            Button(
                onClick = { viewModel.shuffleDeck() },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonIndigo
                ),
                modifier = Modifier
                    .height(54.dp)
                    .padding(horizontal = 4.dp)
                    .testTag("nav_shuffle_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Shuffle", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            // Next Button
            IconButton(
                onClick = { viewModel.nextCard() },
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                    .testTag("nav_next_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Card",
                    tint = TextPrimary
                )
            }
        }
    }

    // Jump to Card Dialog
    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = {
                Text(
                    text = "Jump to Card (1 - 200)",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter any card number from 1 to 200:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = jumpInputText,
                        onValueChange = { jumpInputText = it.filter { ch -> ch.isDigit() } },
                        placeholder = { Text("e.g. 42", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = jumpInputText.toIntOrNull()
                        if (num != null && num in 1..200) {
                            viewModel.jumpToCardById(num)
                            showJumpDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Go", color = CinemaBg, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CinemaSurface
        )
    }
}
