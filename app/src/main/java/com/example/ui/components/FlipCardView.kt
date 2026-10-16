package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MoviePuzzle
import com.example.ui.theme.CinemaBg
import com.example.ui.theme.CinemaCardBack
import com.example.ui.theme.CinemaCardFront
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GuessResult

@Composable
fun FlipCardView(
    movie: MoviePuzzle,
    isFlipped: Boolean,
    isHintRevealed: Boolean,
    isFavorite: Boolean,
    isSolved: Boolean,
    isSpeaking: Boolean,
    speechRate: Float,
    userGuess: String,
    guessResult: GuessResult,
    onFlip: () -> Unit,
    onToggleHint: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleSolved: () -> Unit,
    onSpeak: () -> Unit,
    onCycleSpeechRate: () -> Unit,
    onGuessChange: (String) -> Unit,
    onCheckGuess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "CardRotation"
    )

    val density = LocalDensity.current.density
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 16f * density
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onFlip
            )
            .testTag("game_flip_card")
    ) {
        if (rotation <= 90f) {
            // Front Face
            CardFront(
                movie = movie,
                isHintRevealed = isHintRevealed,
                isFavorite = isFavorite,
                isSolved = isSolved,
                isSpeaking = isSpeaking,
                speechRate = speechRate,
                userGuess = userGuess,
                guessResult = guessResult,
                onToggleHint = onToggleHint,
                onToggleFavorite = onToggleFavorite,
                onSpeak = onSpeak,
                onCycleSpeechRate = onCycleSpeechRate,
                onGuessChange = onGuessChange,
                onCheckGuess = onCheckGuess
            )
        } else {
            // Back Face (Mirrored so text reads correctly)
            CardBack(
                movie = movie,
                isFavorite = isFavorite,
                isSolved = isSolved,
                onToggleFavorite = onToggleFavorite,
                onToggleSolved = onToggleSolved,
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            )
        }
    }
}

@Composable
private fun CardFront(
    movie: MoviePuzzle,
    isHintRevealed: Boolean,
    isFavorite: Boolean,
    isSolved: Boolean,
    isSpeaking: Boolean,
    speechRate: Float,
    userGuess: String,
    guessResult: GuessResult,
    onToggleHint: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSpeak: () -> Unit,
    onCycleSpeechRate: () -> Unit,
    onGuessChange: (String) -> Unit,
    onCheckGuess: () -> Unit
) {
    var showGuessInput by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp, RoundedCornerShape(28.dp), ambientColor = NeonIndigo, spotColor = NeonCyan)
            .border(
                1.5.dp,
                Brush.linearGradient(
                    colors = listOf(
                        NeonIndigo.copy(alpha = 0.5f),
                        NeonCyan.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaCardFront)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Controls Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Genre Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonIndigo.copy(alpha = 0.18f),
                    border = borderBrush(NeonIndigo.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = movie.genre.uppercase(),
                        color = Color(0xFFC7D2FE),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Speech Speed Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .clickable(onClick = onCycleSpeechRate)
                            .testTag("speech_rate_toggle")
                    ) {
                        Text(
                            text = "${speechRate}x",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Audio TTS Button
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSpeaking) NeonIndigo else Color.White.copy(alpha = 0.08f)
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                            .testTag("audio_tts_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak title aloud",
                            tint = if (isSpeaking) Color.White else NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Favorite Button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .testTag("favorite_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Bookmark",
                            tint = if (isFavorite) NeonGold else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Auditory Illusion Main Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = movie.misheardTitle,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "⚡ SAY IT FAST OUT LOUD ⚡",
                        color = NeonCyan,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                if (isSolved) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NeonEmerald.copy(alpha = 0.2f),
                        border = borderBrush(NeonEmerald.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "SOLVED",
                                color = NeonEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hint Box Accordion
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onToggleHint)
                    .border(
                        1.dp,
                        if (isHintRevealed) NeonGold.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                color = CinemaBg.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!isHintRevealed) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Hint",
                                tint = NeonGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Need a movie clue?",
                                color = NeonGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Text(
                            text = movie.hint,
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Optional Interactive Guessing Feature
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!showGuessInput) {
                    FilledTonalButton(
                        onClick = { showGuessInput = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.White.copy(alpha = 0.06f),
                            contentColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("toggle_guess_input_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = NeonCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Test your guess without flipping",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userGuess,
                            onValueChange = onGuessChange,
                            placeholder = { Text("Enter movie title...", fontSize = 12.sp, color = TextMuted) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("guess_text_field"),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { onCheckGuess() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = CinemaBg,
                                unfocusedContainerColor = CinemaBg
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalButton(
                            onClick = onCheckGuess,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = NeonIndigo,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("submit_guess_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Submit Guess", modifier = Modifier.size(18.dp))
                        }
                    }

                    AnimatedVisibility(visible = guessResult is GuessResult.Incorrect) {
                        Text(
                            text = "Not quite! Listen closely and try saying it faster.",
                            color = Color(0xFFFCA5A5),
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer prompt
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TAP CARD TO REVEAL TITLE",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun CardBack(
    movie: MoviePuzzle,
    isFavorite: Boolean,
    isSolved: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleSolved: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(20.dp, RoundedCornerShape(28.dp), ambientColor = NeonCyan, spotColor = NeonIndigo)
            .border(
                1.5.dp,
                Brush.linearGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.6f),
                        NeonIndigo.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaCardBack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonCyan.copy(alpha = 0.18f),
                    border = borderBrush(NeonCyan.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = movie.genre.uppercase(),
                        color = Color(0xFFBAE6FD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Release Year Badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NeonGold.copy(alpha = 0.18f),
                        border = borderBrush(NeonGold.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = movie.year.toString(),
                            color = NeonGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Bookmark",
                            tint = if (isFavorite) NeonGold else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actual Movie Title Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ORIGINAL MOVIE",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = movie.actualTitle,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Synopsis Card Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = CinemaBg.copy(alpha = 0.45f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "SYNOPSIS",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = movie.synopsis,
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Solved Toggle Action
            FilledTonalButton(
                onClick = onToggleSolved,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (isSolved) NeonEmerald.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
                    contentColor = if (isSolved) NeonEmerald else TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("toggle_solved_button")
            ) {
                Icon(
                    imageVector = if (isSolved) Icons.Default.CheckCircle else Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSolved) "Marked as Solved ✓" else "Mark as Solved",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer prompt
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TAP CARD TO FLIP BACK",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

private fun borderBrush(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)
