package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CinemaBg
import com.example.ui.theme.CinemaCardFront
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.PhonoFlicksViewModel

@Composable
fun PartyRushScreen(
    viewModel: PhonoFlicksViewModel,
    modifier: Modifier = Modifier
) {
    val partyActive by viewModel.partyActive.collectAsState()
    val partyFinished by viewModel.partyFinished.collectAsState()
    val partyTimeLeft by viewModel.partyTimeLeft.collectAsState()
    val partyDuration by viewModel.partyDuration.collectAsState()
    val partyScore by viewModel.partyScore.collectAsState()
    val partyPassed by viewModel.partyPassed.collectAsState()
    val partyList by viewModel.partyList.collectAsState()
    val partyIndex by viewModel.partyIndex.collectAsState()
    val partyHighScore by viewModel.partyHighScore.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()

    var selectedSeconds by remember { mutableIntStateOf(60) }
    var showPartyHint by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Not playing yet: Lobby / Setup
            !partyActive && !partyFinished -> {
                PartyLobby(
                    highScore = partyHighScore,
                    selectedSeconds = selectedSeconds,
                    onSelectSeconds = { selectedSeconds = it },
                    onStart = { viewModel.startPartyRush(selectedSeconds) }
                )
            }

            // Game is currently active!
            partyActive -> {
                val currentMovie = if (partyIndex in partyList.indices) partyList[partyIndex] else null
                if (currentMovie != null) {
                    PartyActiveGame(
                        movie = currentMovie,
                        timeLeft = partyTimeLeft,
                        totalDuration = partyDuration,
                        score = partyScore,
                        passes = partyPassed,
                        showHint = showPartyHint,
                        isSpeaking = isSpeaking,
                        onToggleHint = { showPartyHint = !showPartyHint },
                        onSpeak = { viewModel.speakPartyMisheard() },
                        onCorrect = {
                            showPartyHint = false
                            viewModel.partyAnswerCorrect()
                        },
                        onPass = {
                            showPartyHint = false
                            viewModel.partyAnswerPass()
                        },
                        onQuit = { viewModel.cancelPartyRush() }
                    )
                }
            }

            // Game finished: Score summary
            partyFinished -> {
                PartyResults(
                    score = partyScore,
                    passes = partyPassed,
                    highScore = partyHighScore,
                    onPlayAgain = { viewModel.startPartyRush(selectedSeconds) },
                    onDone = { viewModel.cancelPartyRush() }
                )
            }
        }
    }
}

@Composable
private fun PartyLobby(
    highScore: Int,
    selectedSeconds: Int,
    onSelectSeconds: (Int) -> Unit,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(28.dp), ambientColor = NeonIndigo),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface)
    ) {
        Column(
            modifier = Modifier.padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = NeonIndigo.copy(alpha = 0.2f),
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PARTY RUSH",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "One player reads the phonetic clue aloud fast. Teammates shout out the real movie title before the buzzer!",
                color = TextSecondary,
                fontSize = 13.5.sp,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // High Score Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.06f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = NeonGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Best Record",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "$highScore Solved",
                        color = NeonGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Time Selector
            Text(
                text = "ROUND DURATION",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(45, 60, 90).forEach { sec ->
                    val isSelected = selectedSeconds == sec
                    Button(
                        onClick = { onSelectSeconds(sec) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) NeonIndigo else Color.White.copy(alpha = 0.08f),
                            contentColor = if (isSelected) Color.White else TextSecondary
                        )
                    ) {
                        Text("${sec}s", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStart,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_party_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = CinemaBg,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "START PARTY ROUND",
                    color = CinemaBg,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun PartyActiveGame(
    movie: com.example.model.MoviePuzzle,
    timeLeft: Int,
    totalDuration: Int,
    score: Int,
    passes: Int,
    showHint: Boolean,
    isSpeaking: Boolean,
    onToggleHint: () -> Unit,
    onSpeak: () -> Unit,
    onCorrect: () -> Unit,
    onPass: () -> Unit,
    onQuit: () -> Unit
) {
    val progress = timeLeft.toFloat() / totalDuration.toFloat()
    val timerColor = when {
        timeLeft <= 10 -> NeonCoral
        timeLeft <= 20 -> NeonGold
        else -> NeonCyan
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonEmerald.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Score: $score",
                        color = NeonEmerald,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Passes: $passes",
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = onQuit,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Exit Round", tint = TextMuted)
            }
        }

        // Circular Timer Visual
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress },
                strokeWidth = 6.dp,
                color = timerColor,
                trackColor = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.size(92.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$timeLeft",
                    color = timerColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "SEC",
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Current Clue Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(26.dp), ambientColor = NeonIndigo)
                .border(1.5.dp, NeonIndigo.copy(alpha = 0.5f), RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = CinemaCardFront)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movie.genre.uppercase(),
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isSpeaking) NeonIndigo else Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak aloud",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = movie.misheardTitle,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Answer for the reader (in subtle card so reader knows what teammates are guessing)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CinemaBg.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TARGET MOVIE (FOR READER)",
                            color = NeonGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = movie.actualTitle,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Clue toggle
                Text(
                    text = if (showHint) movie.hint else "Tap for movie hint",
                    color = if (showHint) Color(0xFFE2E8F0) else TextMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (showHint) Color.White.copy(alpha = 0.06f) else Color.Transparent)
                        .padding(6.dp)
                )
            }
        }

        // Action Buttons: PASS and GOT IT!
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Button(
                onClick = onPass,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCoral),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .testTag("party_pass_button")
            ) {
                Text(
                    text = "PASS ⏭",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }

            Button(
                onClick = onCorrect,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                modifier = Modifier
                    .weight(1.3f)
                    .height(64.dp)
                    .testTag("party_correct_button")
            ) {
                Text(
                    text = "GOT IT! ✓",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun PartyResults(
    score: Int,
    passes: Int,
    highScore: Int,
    onPlayAgain: () -> Unit,
    onDone: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(28.dp), ambientColor = NeonGold),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = NeonGold,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "TIME'S UP!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$score",
                color = NeonCyan,
                fontSize = 56.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "MOVIES GUESSED",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            if (score >= highScore && score > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NeonGold.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGold.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "★ NEW HIGH RECORD ★",
                        color = NeonGold,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPlayAgain,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Play Again", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onDone,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.08f),
                    contentColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Back to Lobby", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
