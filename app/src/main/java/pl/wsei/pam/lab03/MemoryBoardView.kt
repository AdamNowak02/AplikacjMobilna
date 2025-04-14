package pl.wsei.pam.lab03

import android.media.MediaPlayer
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import pl.wsei.pam.R
import java.util.*

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()

    // 🔥 Więcej ikon, żeby było ciekawiej
    private val icons: List<Int> = listOf(
        R.drawable.baseline_rocket_launch_24,
        R.drawable.baseline_star_24,
        R.drawable.baseline_favorite_24,
        R.drawable.baseline_android_24,
        R.drawable.baseline_air_24,
        R.drawable.baseline_airplanemode_active_24,
        R.drawable.baseline_bolt_24,
        R.drawable.baseline_bluetooth_24
    )

    private var isSound = true
    private val completionPlayer: MediaPlayer = MediaPlayer.create(gridLayout.context, R.raw.completion)

    init {
        val totalTiles = cols * rows
        if (totalTiles % 2 != 0) {
            throw IllegalArgumentException("Liczba kafelków musi być parzysta!")
        }

        // 🔥 Gwarantujemy, że każda ikona jest dokładnie 2 razy
        val neededPairs = totalTiles / 2
        val selectedIcons = icons.shuffled().take(neededPairs) // Pobieramy tyle ikon, ile potrzeba
        val shuffledIcons = (selectedIcons + selectedIcons).shuffled().toMutableList() // 🔥 Poprawione! Teraz to `MutableList`

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val button = ImageButton(gridLayout.context).also {
                    it.tag = "${row}x${col}"
                    val layoutParams = GridLayout.LayoutParams()
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)
                    it.layoutParams = layoutParams
                }
                val tileResource = shuffledIcons.removeAt(0) // 🔥 Teraz działa, bo shuffledIcons to `MutableList`
                addTile(button, tileResource)
                gridLayout.addView(button)
            }
        }
    }

    private val deckResource: Int = R.drawable.baseline_rocket_launch_24
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { _ -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag]
        if (tile == null || tile.revealed) return

        tile.revealed = true
        matchedPair.push(tile)

        val matchResult = logic.process({ tile.tileResource }, tile)

        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))

        if (isSound) {
            completionPlayer.start()
        }

        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }

        // 🔥 Sprawdzamy, czy wszystkie kafelki są odkryte
        if (tiles.values.all { it.revealed }) {
            Toast.makeText(gridLayout.context, "Gra zakończona!", Toast.LENGTH_SHORT).show()
        }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    fun setSoundEnabled(enabled: Boolean) {
        isSound = enabled
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, deckResource)
        tiles[button.tag.toString()] = tile
    }
}
