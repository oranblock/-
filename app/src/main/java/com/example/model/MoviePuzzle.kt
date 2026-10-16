package com.example.model

data class MoviePuzzle(
    val id: Int,
    val misheardTitle: String,
    val actualTitle: String,
    val year: Int,
    val genre: String,
    val hint: String,
    val synopsis: String
) {
    /**
     * Checks if a user's guess matches the actual title using normalized comparison.
     * Strips punctuation, subtitles, and tolerates minor typos.
     */
    fun isCorrectGuess(guess: String): Boolean {
        val cleanGuess = normalize(guess)
        val cleanActual = normalize(actualTitle)
        if (cleanGuess.isEmpty()) return false
        if (cleanGuess == cleanActual) return true

        // Check without subtitle (e.g. "Kill Bill" for "Kill Bill: Vol. 1")
        if (actualTitle.contains(":")) {
            val mainTitle = normalize(actualTitle.substringBefore(":"))
            if (cleanGuess == mainTitle) return true
        }

        // Check without common leading articles
        val noArticleActual = cleanActual.removePrefix("the").removePrefix("a").removePrefix("an")
        val noArticleGuess = cleanGuess.removePrefix("the").removePrefix("a").removePrefix("an")
        if (noArticleGuess.isNotEmpty() && noArticleGuess == noArticleActual) return true

        // Levenshtein distance for minor typos
        val dist = levenshtein(cleanGuess, cleanActual)
        val maxTolerance = if (cleanActual.length > 8) 2 else 1
        return dist <= maxTolerance
    }

    private fun normalize(text: String): String {
        return text.lowercase()
            .replace(Regex("[^a-z0-9]"), "")
            .trim()
    }

    private fun levenshtein(s: String, t: String): Int {
        val m = s.length
        val n = t.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (s[i - 1] == t[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[m][n]
    }
}
