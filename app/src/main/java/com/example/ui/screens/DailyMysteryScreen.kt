package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.PhonoFlicksViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyMysteryScreen(
    viewModel: PhonoFlicksViewModel,
    modifier: Modifier = Modifier
) {
    val dailyMovie = remember { viewModel.getDailyPuzzle() }
    val solvedIds by viewModel.solvedIds.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()

    var userGuess by remember { mutableStateOf("") }
    var isRevealed by remember { mutableStateOf(false) }
    var hasAttempted by remember { mutableStateOf(false) }
    var isCorrectGuess by remember { mutableStateOf(false) }

    val isSolved = dailyMovie != null && solvedIds.contains(dailyMovie.id)
    val todayFormatted = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = NeonGold.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGold.copy(alpha = 0.35f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = todayFormatted.uppercase(),
                        color = NeonGold,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "MYSTERY REEL OF THE DAY",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "One curated auditory puzzle every 24 hours. Can you solve it?",
                color = TextSecondary,
                fontSize = 12.5.sp,
                textAlign = TextAlign.Center
            )
        }

        if (dailyMovie != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(24.dp, RoundedCornerShape(26.dp), ambientColor = NeonGold)
                    .border(1.5.dp, NeonGold.copy(alpha = 0.4f), RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaCardFront)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NeonGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "DAILY CHALLENGE",
                                color = NeonGold,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.ttsManager.speak(dailyMovie.misheardTitle) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSpeaking) NeonIndigo else Color.White.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen to clue",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = dailyMovie.misheardTitle,
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 38.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "⚡ SAY IT ALOUD AS FAST AS YOU CAN ⚡",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Clue box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CinemaBg.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Clue: ${dailyMovie.hint}",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isRevealed || isSolved) {
                        // Reveal section
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NeonEmerald.copy(alpha = 0.18f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = NeonEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DAILY MOVIE SOLVED!",
                                        color = NeonEmerald,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = dailyMovie.actualTitle,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "(${dailyMovie.year}) • ${dailyMovie.genre}",
                                    color = NeonGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // Guess Input
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = userGuess,
                                onValueChange = {
                                    userGuess = it
                                    hasAttempted = false
                                },
                                placeholder = { Text("What is this movie?", color = TextMuted, fontSize = 13.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("daily_guess_input"),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGold,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = CinemaBg,
                                    unfocusedContainerColor = CinemaBg
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    hasAttempted = true
                                    if (dailyMovie.isCorrectGuess(userGuess)) {
                                        isCorrectGuess = true
                                        isRevealed = true
                                        viewModel.toggleSolved(dailyMovie.id)
                                    } else {
                                        isCorrectGuess = false
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                                modifier = Modifier
                                    .height(56.dp)
                                    .testTag("daily_submit_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Check", tint = CinemaBg)
                            }
                        }

                        if (hasAttempted && !isCorrectGuess) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Not quite! Listen to the pronunciation or check the clue.",
                                color = Color(0xFFFCA5A5),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                isRevealed = true
                                viewModel.toggleSolved(dailyMovie.id)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.08f),
                                contentColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Give Up & Reveal Answer", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Streak info
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CinemaSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Daily Mystery Streak",
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isSolved) "Today completed! Come back tomorrow." else "Solve today's puzzle to maintain your streak!",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }
    }
}
