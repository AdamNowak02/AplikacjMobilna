package pl.wsei.pam.lab03

class MemoryGameLogic(private val maxMatches: Int) {
    private var selectedTiles: MutableList<Tile> = mutableListOf()
    private var matches: Int = 0
    private var gameChangeListener: ((MemoryGameEvent) -> Unit)? = null

    fun setOnGameChangeListener(listener: (MemoryGameEvent) -> Unit) {
        gameChangeListener = listener
    }

    fun process(value: () -> Int, tile: Tile): GameStates {
        if (selectedTiles.size < 1) {
            selectedTiles.add(tile)
            gameChangeListener?.invoke(MemoryGameEvent(selectedTiles.toList(), GameStates.Matching))
            return GameStates.Matching
        }

        selectedTiles.add(tile)
        val result = selectedTiles[0].tileResource == selectedTiles[1].tileResource
        matches += if (result) 1 else 0

        val gameState = when {
            result && matches == maxMatches -> GameStates.Finished
            result -> GameStates.Match
            else -> GameStates.NoMatch
        }

        gameChangeListener?.invoke(MemoryGameEvent(selectedTiles.toList(), gameState))
        selectedTiles.clear()
        return gameState
    }
}
