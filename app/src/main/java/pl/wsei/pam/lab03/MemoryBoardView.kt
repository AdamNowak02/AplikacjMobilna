package pl.wsei.pam.lab03

import android.media.MediaPlayer
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import pl.wsei.pam.lab01.R
import java.util.*

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val icons: List<Int> = listOf(
        R.drawable.baseline_rocket_launch_24,
        R.drawable.baseline_star_24,
        R.drawable.baseline_favorite_24,
        R.drawable.baseline_android_24
        // Dodaj więcej ikon, jeśli potrzeba!
    )

    private var isSound = true  // Stan dźwięku (włączony/wyłączony)
    private val completionPlayer: MediaPlayer = MediaPlayer.create(gridLayout.context, R.raw.completion)  // Załaduj odpowiedni dźwięk

    init {
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(icons.subList(0, cols * rows / 2))
            it.addAll(icons.subList(0, cols * rows / 2))
            it.shuffle()
        }



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
                val tileResource = shuffledIcons.removeAt(0)
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
        if (tile == null || tile.revealed) return  // Zapobiega ponownemu kliknięciu

        tile.revealed = true
        matchedPair.push(tile)

        // Poprawione wywołanie process() - przekazujemy tiles jako argument
        val matchResult = logic.process({ tile?.tileResource ?: -1 }, tile)

        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))

        // Odgrywaj dźwięk, jeśli jest włączony
        if (isSound) {
            completionPlayer.start()  // Uruchamiamy dźwięk
        }

        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    // Dodana metoda do zmiany stanu dźwięku
    fun setSoundEnabled(enabled: Boolean) {
        isSound = enabled
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, deckResource)
        tiles[button.tag.toString()] = tile
    }

}
