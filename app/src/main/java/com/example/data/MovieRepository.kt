package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.MoviePuzzle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import java.io.InputStreamReader
import java.util.Calendar

class MovieRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("phonoflicks_prefs", Context.MODE_PRIVATE)

    private val _movies = MutableStateFlow<List<MoviePuzzle>>(emptyList())
    val movies: StateFlow<List<MoviePuzzle>> = _movies.asStateFlow()

    private val _solvedIds = MutableStateFlow<Set<Int>>(emptySet())
    val solvedIds: StateFlow<Set<Int>> = _solvedIds.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    private val _partyHighScore = MutableStateFlow(0)
    val partyHighScore: StateFlow<Int> = _partyHighScore.asStateFlow()

    init {
        loadPreferences()
        loadMovies()
    }

    private fun loadPreferences() {
        val savedSolved = prefs.getStringSet(KEY_SOLVED, emptySet()) ?: emptySet()
        _solvedIds.value = savedSolved.mapNotNull { it.toIntOrNull() }.toSet()

        val savedFavs = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        _favoriteIds.value = savedFavs.mapNotNull { it.toIntOrNull() }.toSet()

        _partyHighScore.value = prefs.getInt(KEY_PARTY_HIGH_SCORE, 0)
    }

    private fun loadMovies() {
        try {
            val inputStream = context.assets.open("movies_master.json")
            val jsonString = InputStreamReader(inputStream).use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<MoviePuzzle>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    MoviePuzzle(
                        id = obj.optInt("id", i + 1),
                        misheardTitle = obj.optString("misheardTitle", ""),
                        actualTitle = obj.optString("actualTitle", ""),
                        year = obj.optInt("year", 2000),
                        genre = obj.optString("genre", "Cinema"),
                        hint = obj.optString("hint", ""),
                        synopsis = obj.optString("synopsis", "")
                    )
                )
            }
            _movies.value = list
        } catch (e: Exception) {
            e.printStackTrace()
            // In case of any asset reading error, provide baseline fallback entries
            _movies.value = listOf(
                MoviePuzzle(
                    id = 1,
                    misheardTitle = "Jew Rack Sick Park",
                    actualTitle = "Jurassic Park",
                    year = 1993,
                    genre = "Sci-Fi / Adventure",
                    hint = "Cloned prehistoric creatures run amok on an island.",
                    synopsis = "A pragmatic paleontologist touring an almost-complete theme park on an island in Central America is tasked with protecting two children when cloned dinosaurs break loose."
                ),
                MoviePuzzle(
                    id = 2,
                    misheardTitle = "Theme Eight Ricks",
                    actualTitle = "The Matrix",
                    year = 1999,
                    genre = "Sci-Fi / Action",
                    hint = "Red pill or blue pill?",
                    synopsis = "When a computer hacker discovers reality is a simulated illusion created by AI, he joins a rebellion to free mankind."
                )
            )
        }
    }

    fun toggleSolved(id: Int) {
        val current = _solvedIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _solvedIds.value = current
        prefs.edit().putStringSet(KEY_SOLVED, current.map { it.toString() }.toSet()).apply()
    }

    fun markSolved(id: Int) {
        val current = _solvedIds.value.toMutableSet()
        if (current.add(id)) {
            _solvedIds.value = current
            prefs.edit().putStringSet(KEY_SOLVED, current.map { it.toString() }.toSet()).apply()
        }
    }

    fun toggleFavorite(id: Int) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _favoriteIds.value = current
        prefs.edit().putStringSet(KEY_FAVORITES, current.map { it.toString() }.toSet()).apply()
    }

    fun recordPartyScore(score: Int) {
        if (score > _partyHighScore.value) {
            _partyHighScore.value = score
            prefs.edit().putInt(KEY_PARTY_HIGH_SCORE, score).apply()
        }
    }

    fun resetProgress() {
        _solvedIds.value = emptySet()
        _favoriteIds.value = emptySet()
        _partyHighScore.value = 0
        prefs.edit()
            .remove(KEY_SOLVED)
            .remove(KEY_FAVORITES)
            .remove(KEY_PARTY_HIGH_SCORE)
            .apply()
    }

    fun getDailyPuzzle(): MoviePuzzle? {
        val list = _movies.value
        if (list.isEmpty()) return null
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % list.size
        return list[index]
    }

    companion object {
        private const val KEY_SOLVED = "key_solved_ids"
        private const val KEY_FAVORITES = "key_favorite_ids"
        private const val KEY_PARTY_HIGH_SCORE = "key_party_high_score"
    }
}
