package pl.wsei.pam.lab03


import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import pl.wsei.pam.R
import java.util.Timer
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {

    private lateinit var main: GridLayout
    private lateinit var gameLogic: MemoryGameLogic
    private val tiles = mutableListOf<Tile>()
    private lateinit var completionPlayer: MediaPlayer
    private lateinit var negativePlayer: MediaPlayer
    private var isSoundOn = true  // Dodana zmienna do kontrolowania dźwięku

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        main = findViewById(R.id.main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3)
        if (size.size != 2) {
            Log.e("Lab03Activity", "Błąd: 'size' ma błędny format")
            return
        }

        val rows = size[0]
        val columns = size[1]
        Log.d("Lab03Activity", "Rozmiar planszy: ${rows}x${columns}")

        main.columnCount = columns
        main.rowCount = rows

        gameLogic = MemoryGameLogic((rows * columns) / 2)

        generateBoard(rows, columns)

        savedInstanceState?.getIntegerArrayList("boardState")?.let {
            restoreBoardState(it.toTypedArray())
        }

        gameLogic.setOnGameChangeListener { e ->
            runOnUiThread {
                when (e.state) {
                    GameStates.Matching -> e.tiles.forEach { it.revealed = true }
                    GameStates.Match -> {
                        if (isSoundOn) completionPlayer.start()
                        e.tiles.forEach {
                            animatePairedButton(it.button) { it.revealed = true }
                        }
                    }

                    GameStates.NoMatch -> {
                        if (isSoundOn) negativePlayer.start()
                        animateMismatchedPair(e.tiles[0].button, e.tiles[1].button) {
                            e.tiles.forEach { it.revealed = false }
                        }
                    }

                    GameStates.Finished -> {
                        Toast.makeText(this, "Gra zakończona!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePlayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)
    }

    override fun onPause() {
        super.onPause()
        completionPlayer.release()
        negativePlayer.release()
    }

    private fun generateBoard(rows: Int, columns: Int) {
        val totalTiles = rows * columns
        if (totalTiles % 2 != 0) {
            throw IllegalArgumentException("Liczba kafelków musi być parzysta!")
        }

        val icons = listOf(
            R.drawable.baseline_square_24,
            R.drawable.baseline_star_24,
            R.drawable.baseline_favorite_24,
            R.drawable.baseline_face_24,
            R.drawable.baseline_android_24,
            R.drawable.baseline_air_24,
            R.drawable.baseline_airplanemode_active_24,
            R.drawable.baseline_bolt_24,
            R.drawable.baseline_bluetooth_24
        )

        val neededPairs = totalTiles / 2
        val selectedIcons = icons.shuffled().take(neededPairs) // Wybieramy dokładnie tyle ikon, ile potrzeba
        val shuffledIcons = (selectedIcons + selectedIcons).shuffled() // Duplikujemy i mieszamy

        val backResource = R.drawable.baseline_rocket_launch_24
        main.removeAllViews()
        tiles.clear()

        for (i in 0 until totalTiles) {
            val btn = ImageButton(this).also {
                val layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    setGravity(Gravity.CENTER)
                    columnSpec = GridLayout.spec(i % columns, 1, 1f)
                    rowSpec = GridLayout.spec(i / columns, 1, 1f)
                }
                it.layoutParams = layoutParams
                main.addView(it)
            }

            val tile = Tile(btn, shuffledIcons[i], backResource) // Pobieramy kolejny kafelek z przygotowanej listy
            tiles.add(tile)
            btn.setOnClickListener { onTileClicked(tile) }
        }
    }

    private fun onTileClicked(tile: Tile) {
        if (tile.revealed) return
        tile.revealed = true
        gameLogic.process({ tile.tileResource }, tile)

        // 🔥 Sprawdzamy, czy to ostatnie dwie karty
        if (tiles.count { !it.revealed } == 0) {
            Toast.makeText(this, "🎉 Gra zakończona!", Toast.LENGTH_SHORT).show()

            // 🚀 Ukryjemy wszystkie karty animacją
            tiles.forEach { animatePairedButton(it.button) { it.button.visibility = View.GONE } }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val revealedStates = tiles.map { it.revealed }.toBooleanArray() // Czy kafelek odkryty?
        val tileIcons = tiles.map { it.tileResource }.toIntArray() // Ikony kafelków
        val visibilityStates = tiles.map { it.button.visibility == View.VISIBLE }.toBooleanArray() // Czy kafelek widoczny?

        outState.putBooleanArray("revealedStates", revealedStates)
        outState.putIntArray("tileIcons", tileIcons)
        outState.putBooleanArray("visibilityStates", visibilityStates) // Zapisujemy stan widoczności
    }



    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val revealedStates = savedInstanceState.getBooleanArray("revealedStates") ?: return
        val tileIcons = savedInstanceState.getIntArray("tileIcons") ?: return
        val visibilityStates = savedInstanceState.getBooleanArray("visibilityStates") ?: return

        if (revealedStates.size == tiles.size && tileIcons.size == tiles.size && visibilityStates.size == tiles.size) {
            for (i in tiles.indices) {
                tiles[i].revealed = revealedStates[i]
                tiles[i].tileResource = tileIcons[i]

                // Przywracamy stan widoczności kafelka
                if (visibilityStates[i]) {
                    tiles[i].button.visibility = View.VISIBLE
                } else {
                    tiles[i].button.visibility = View.INVISIBLE
                }

                // Jeśli kafelek był odkryty, pokazujemy ikonę
                if (tiles[i].revealed) {
                    tiles[i].button.setImageResource(tiles[i].tileResource)
                } else {
                    tiles[i].button.setImageResource(R.drawable.baseline_rocket_launch_24) // Tył kafelka
                }
            }
        }
    }



    private fun restoreBoardState(state: Array<Int>) {
        for (i in tiles.indices) {
            tiles[i].revealed = state[i] != -1
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.board_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.board_activity_sound -> {
                toggleSound(item)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleSound(item: MenuItem) {
        isSoundOn = !isSoundOn

        // Aktualizacja ikony
        item.setIcon(
            if (isSoundOn) R.drawable.baseline_audiotrack_24
            else R.drawable.baseline_volume_off_24 // Poprawiona ikona
        )
    }

    private fun animatePairedButton(button: ImageButton, action: Runnable) {
        val set = AnimatorSet()
        val random = java.util.Random()
        button.pivotX = random.nextFloat() * 200f
        button.pivotY = random.nextFloat() * 200f

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 1080f)
        val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 4f)
        val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 4f)
        val fade = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)

        set.startDelay = 500
        set.duration = 2000
        set.interpolator = DecelerateInterpolator()
        set.playTogether(rotation, scaleX, scaleY, fade)

        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                button.scaleX = 1f
                button.scaleY = 1f
                button.alpha = 0.0f
                button.visibility = ImageButton.GONE
                action.run()
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })

        set.start()
    }

    private fun animateMismatchedPair(
        button1: ImageButton,
        button2: ImageButton,
        action: Runnable
    ) {
        val set = AnimatorSet()
        val shake1 =
            ObjectAnimator.ofFloat(button1, "translationX", -20f, 20f, -15f, 15f, -5f, 5f, 0f)
        val shake2 =
            ObjectAnimator.ofFloat(button2, "translationX", -20f, 20f, -15f, 15f, -5f, 5f, 0f)

        set.duration = 500
        set.interpolator = LinearInterpolator()
        set.playTogether(shake1, shake2)

        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}

            override fun onAnimationEnd(animator: Animator) {
                action.run()
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })

        set.start()
    }
}
