package com.example

import com.example.model.MoviePuzzle
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testMoviePuzzleGuessVerification() {
        val puzzle = MoviePuzzle(
            id = 1,
            misheardTitle = "Jew Rack Sick Park",
            actualTitle = "Jurassic Park",
            year = 1993,
            genre = "Sci-Fi / Adventure",
            hint = "Cloned prehistoric creatures run amok on an island.",
            synopsis = "A paleontologist protects children from escaped dinosaurs."
        )

        // Exact match
        assertTrue(puzzle.isCorrectGuess("Jurassic Park"))
        // Case insensitivity
        assertTrue(puzzle.isCorrectGuess("jurassic park"))
        // Punctuation and spaces
        assertTrue(puzzle.isCorrectGuess("   juras-sic park!  "))
        // Minor typo tolerance
        assertTrue(puzzle.isCorrectGuess("Jurrasic Park"))
        // Subtitle handling (e.g. Kill Bill vs Kill Bill: Vol. 1)
        val subtitlePuzzle = MoviePuzzle(
            id = 16,
            misheardTitle = "Quill Pill",
            actualTitle = "Kill Bill: Vol. 1",
            year = 2003,
            genre = "Action",
            hint = "Yellow jumpsuit",
            synopsis = "Revenge saga."
        )
        assertTrue(subtitlePuzzle.isCorrectGuess("Kill Bill"))
        assertTrue(subtitlePuzzle.isCorrectGuess("Kill Bill: Vol. 1"))

        // False check
        assertFalse(puzzle.isCorrectGuess("Star Wars"))
        assertFalse(puzzle.isCorrectGuess(""))
    }
}
