package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.TtsManager
import com.example.data.MovieRepository
import com.example.model.MoviePuzzle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    DECK,
    PARTY_RUSH,
    DIRECTORY,
    DAILY,
    STATS
}

sealed class GuessResult {
    data object None : GuessResult()
    data class Correct(val title: String) : GuessResult()
    data class Incorrect(val attempted: String) : GuessResult()
}

class PhonoFlicksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository(application)
    val ttsManager = TtsManager(application)

    val allMovies = repository.movies
    val solvedIds = repository.solvedIds
    val favoriteIds = repository.favoriteIds
    val partyHighScore = repository.partyHighScore

    private val _currentTab = MutableStateFlow(ScreenTab.DECK)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _selectedGenreFilter = MutableStateFlow("All")
    val selectedGenreFilter: StateFlow<String> = _selectedGenreFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _deckIndex = MutableStateFlow(0)
    val deckIndex: StateFlow<Int> = _deckIndex.asStateFlow()

    private val _isFlipped = MutableStateFlow(false)
    val isFlipped: StateFlow<Boolean> = _isFlipped.asStateFlow()

    private val _isHintRevealed = MutableStateFlow(false)
    val isHintRevealed: StateFlow<Boolean> = _isHintRevealed.asStateFlow()

    private val _userGuess = MutableStateFlow("")
    val userGuess: StateFlow<String> = _userGuess.asStateFlow()

    private val _guessFeedback = MutableStateFlow<GuessResult>(GuessResult.None)
    val guessFeedback: StateFlow<GuessResult> = _guessFeedback.asStateFlow()

    // Filtered movies for the Deck
    val filteredMovies: StateFlow<List<MoviePuzzle>> = combine(
        allMovies,
        _selectedGenreFilter,
        favoriteIds,
        solvedIds
    ) { movies, filter, favs, solved ->
        when (filter) {
            "All" -> movies
            "Favorites" -> movies.filter { favs.contains(it.id) }
            "Unsolved" -> movies.filter { !solved.contains(it.id) }
            "Solved" -> movies.filter { solved.contains(it.id) }
            else -> movies.filter { it.genre.contains(filter, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Active card
    val currentCard: StateFlow<MoviePuzzle?> = combine(filteredMovies, _deckIndex) { list, idx ->
        if (list.isNotEmpty() && idx in list.indices) list[idx] else list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Party Rush state
    private val _partyActive = MutableStateFlow(false)
    val partyActive: StateFlow<Boolean> = _partyActive.asStateFlow()

    private val _partyTimeLeft = MutableStateFlow(60)
    val partyTimeLeft: StateFlow<Int> = _partyTimeLeft.asStateFlow()

    private val _partyDuration = MutableStateFlow(60)
    val partyDuration: StateFlow<Int> = _partyDuration.asStateFlow()

    private val _partyScore = MutableStateFlow(0)
    val partyScore: StateFlow<Int> = _partyScore.asStateFlow()

    private val _partyPassed = MutableStateFlow(0)
    val partyPassed: StateFlow<Int> = _partyPassed.asStateFlow()

    private val _partyList = MutableStateFlow<List<MoviePuzzle>>(emptyList())
    val partyList: StateFlow<List<MoviePuzzle>> = _partyList.asStateFlow()

    private val _partyIndex = MutableStateFlow(0)
    val partyIndex: StateFlow<Int> = _partyIndex.asStateFlow()

    private val _partyFinished = MutableStateFlow(false)
    val partyFinished: StateFlow<Boolean> = _partyFinished.asStateFlow()

    private var partyTimerJob: Job? = null

    fun selectTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun setGenreFilter(filter: String) {
        _selectedGenreFilter.value = filter
        _deckIndex.value = 0
        resetCardTransientState()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun flipCard() {
        _isFlipped.value = !_isFlipped.value
    }

    fun toggleHint() {
        _isHintRevealed.value = !_isHintRevealed.value
    }

    fun nextCard() {
        val list = filteredMovies.value
        if (list.isEmpty()) return
        _deckIndex.value = if (_deckIndex.value < list.size - 1) _deckIndex.value + 1 else 0
        resetCardTransientState()
    }

    fun prevCard() {
        val list = filteredMovies.value
        if (list.isEmpty()) return
        _deckIndex.value = if (_deckIndex.value > 0) _deckIndex.value - 1 else list.size - 1
        resetCardTransientState()
    }

    fun shuffleDeck() {
        val list = filteredMovies.value
        if (list.size > 1) {
            val randomIdx = (0 until list.size).random()
            _deckIndex.value = randomIdx
        }
        resetCardTransientState()
    }

    fun jumpToCardById(targetId: Int) {
        val list = filteredMovies.value
        val idx = list.indexOfFirst { it.id == targetId }
        if (idx != -1) {
            _deckIndex.value = idx
        } else {
            // Switch filter to "All" if not in current filter
            _selectedGenreFilter.value = "All"
            val allList = allMovies.value
            val allIdx = allList.indexOfFirst { it.id == targetId }
            if (allIdx != -1) {
                _deckIndex.value = allIdx
            }
        }
        resetCardTransientState()
        _currentTab.value = ScreenTab.DECK
    }

    fun updateGuessText(text: String) {
        _userGuess.value = text
        _guessFeedback.value = GuessResult.None
    }

    fun checkGuess() {
        val card = currentCard.value ?: return
        val guess = _userGuess.value.trim()
        if (guess.isEmpty()) return

        if (card.isCorrectGuess(guess)) {
            _guessFeedback.value = GuessResult.Correct(card.actualTitle)
            repository.markSolved(card.id)
            _isFlipped.value = true
        } else {
            _guessFeedback.value = GuessResult.Incorrect(guess)
        }
    }

    fun toggleSolvedCurrent() {
        currentCard.value?.let { card ->
            repository.toggleSolved(card.id)
        }
    }

    fun toggleFavoriteCurrent() {
        currentCard.value?.let { card ->
            repository.toggleFavorite(card.id)
        }
    }

    fun toggleFavorite(id: Int) {
        repository.toggleFavorite(id)
    }

    fun toggleSolved(id: Int) {
        repository.toggleSolved(id)
    }

    fun speakCurrentMisheard() {
        currentCard.value?.let { card ->
            ttsManager.speak(card.misheardTitle)
        }
    }

    fun speakPartyMisheard() {
        val list = _partyList.value
        val idx = _partyIndex.value
        if (idx in list.indices) {
            ttsManager.speak(list[idx].misheardTitle)
        }
    }

    private fun resetCardTransientState() {
        _isFlipped.value = false
        _isHintRevealed.value = false
        _userGuess.value = ""
        _guessFeedback.value = GuessResult.None
        ttsManager.stop()
    }

    // Party Rush Mode Logic
    fun startPartyRush(seconds: Int = 60) {
        val movies = allMovies.value.shuffled()
        _partyDuration.value = seconds
        _partyTimeLeft.value = seconds
        _partyScore.value = 0
        _partyPassed.value = 0
        _partyList.value = movies
        _partyIndex.value = 0
        _partyFinished.value = false
        _partyActive.value = true

        partyTimerJob?.cancel()
        partyTimerJob = viewModelScope.launch {
            while (_partyTimeLeft.value > 0 && _partyActive.value) {
                delay(1000L)
                _partyTimeLeft.value -= 1
            }
            finishPartyRush()
        }
    }

    fun partyAnswerCorrect() {
        if (!_partyActive.value) return
        val list = _partyList.value
        val idx = _partyIndex.value
        if (idx in list.indices) {
            repository.markSolved(list[idx].id)
        }
        _partyScore.value += 1
        advancePartyCard()
    }

    fun partyAnswerPass() {
        if (!_partyActive.value) return
        _partyPassed.value += 1
        advancePartyCard()
    }

    private fun advancePartyCard() {
        val list = _partyList.value
        if (_partyIndex.value < list.size - 1) {
            _partyIndex.value += 1
        } else {
            finishPartyRush()
        }
    }

    fun finishPartyRush() {
        _partyActive.value = false
        _partyFinished.value = true
        partyTimerJob?.cancel()
        repository.recordPartyScore(_partyScore.value)
    }

    fun cancelPartyRush() {
        _partyActive.value = false
        _partyFinished.value = false
        partyTimerJob?.cancel()
    }

    fun getDailyPuzzle(): MoviePuzzle? {
        return repository.getDailyPuzzle()
    }

    fun resetAllProgress() {
        repository.resetProgress()
        resetCardTransientState()
    }

    override fun onCleared() {
        super.onCleared()
        partyTimerJob?.cancel()
        ttsManager.shutdown()
    }
}
