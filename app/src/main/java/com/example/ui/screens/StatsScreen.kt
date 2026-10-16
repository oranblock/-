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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CinemaBg
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.PhonoFlicksViewModel

data class CineBadge(
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val iconColor: Color
)

@Composable
fun StatsScreen(
    viewModel: PhonoFlicksViewModel,
    modifier: Modifier = Modifier
) {
    val allMovies by viewModel.allMovies.collectAsState()
    val solvedIds by viewModel.solvedIds.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val partyHighScore by viewModel.partyHighScore.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    val totalMovies = allMovies.size.coerceAtLeast(1)
    val solvedCount = solvedIds.size
    val progress = (solvedCount.toFloat() / totalMovies.toFloat()).coerceIn(0f, 1f)
    val percentInt = (progress * 100).toInt()

    val badges = listOf(
        CineBadge("Curtain Up", "Solve your very first movie puzzle", solvedCount >= 1, NeonEmerald),
        CineBadge("Matinee Explorer", "Crack 25 Mad-Gab film titles", solvedCount >= 25, NeonCyan),
        CineBadge("Film Buff", "Decode 50 iconic cinema illusions", solvedCount >= 50, NeonIndigo),
        CineBadge("Auditory Maestro", "Reach 100 solved movie riddles", solvedCount >= 100, NeonGold),
        CineBadge("Hollywood Legend", "Complete the full 200 movie pantheon", solvedCount >= 200, Color(0xFFEC4899)),
        CineBadge("Speed Demon", "Score 5+ in a single Party Rush round", partyHighScore >= 5, NeonCyan),
        CineBadge("Party Kingpin", "Score 10+ in a single Party Rush round", partyHighScore >= 10, NeonGold)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "CAREER & STATS",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Overview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CinemaSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "PUZZLES SOLVED",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$solvedCount / $totalMovies",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$percentInt% of library mastered",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progress },
                        strokeWidth = 8.dp,
                        color = NeonCyan,
                        trackColor = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.size(80.dp)
                    )
                    Text(
                        text = "$percentInt%",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Highlights row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Party High Score
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$partyHighScore",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Party High Score",
                        color = TextMuted,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Favorites Count
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${favoriteIds.size}",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Bookmarked Movies",
                        color = TextMuted,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cinephile Badges Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACHIEVEMENTS & BADGES",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "${badges.count { it.isUnlocked }} / ${badges.size} Unlocked",
                color = NeonGold,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            badges.forEach { badge ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (badge.isUnlocked) CinemaSurface else CinemaSurface.copy(alpha = 0.5f)
                    ),
                    border = if (badge.isUnlocked) {
                        androidx.compose.foundation.BorderStroke(1.dp, badge.iconColor.copy(alpha = 0.35f))
                    } else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (badge.isUnlocked) badge.iconColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (badge.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (badge.isUnlocked) badge.iconColor else TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = badge.title,
                                color = if (badge.isUnlocked) Color.White else TextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = badge.description,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        if (badge.isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Unlocked",
                                tint = NeonEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Reset Data Button
        Button(
            onClick = { showResetDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.06f),
                contentColor = TextMuted
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Career Progress", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Progress?", color = TextPrimary) },
            text = {
                Text(
                    "This will clear all solved movies, bookmarks, and your party high score. This cannot be undone.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Reset All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CinemaSurface
        )
    }
}
