package pl.wsei.pam.lab01

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class Lab01Activity : AppCompatActivity() {
    lateinit var mLayout: LinearLayout
    lateinit var mTitle: TextView
    lateinit var mProgress: ProgressBar
    var mBoxes: MutableList<CheckBox> = mutableListOf()
    var mButtons: MutableList<Button> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lab01activity)
        mLayout = findViewById(R.id.main)

        // Nagłówek
        mTitle = TextView(this)
        mTitle.text = "Laboratorium 1"
        mTitle.textSize = 24f
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(20, 20, 20, 20)
        mTitle.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        mTitle.layoutParams = params
        mLayout.addView(mTitle)

        // Pętla dodająca CheckBoxy i przyciski "Testuj"
        for (i in 1..6) {
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val checkBox = CheckBox(this)
            checkBox.text = "Zadanie $i"
            checkBox.isEnabled = false
            mBoxes.add(checkBox)

            val button = Button(this).also {
                it.text = "Testuj"
                it.setOnClickListener { testTask(i) }
            }
            mButtons.add(button)

            row.addView(checkBox)
            row.addView(button)
            mLayout.addView(row)
        }

        // Pasek postępu
        mProgress = ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        ).also {
            it.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            it.max = 6
        }
        mLayout.addView(mProgress)
    }

    // Funkcja do testowania konkretnego zadania
    private fun testTask(index: Int) {
        when (index) {
            1 -> if (task11(4, 6) in 0.666665..0.666667 &&
                task11(7, -6) in -1.1666667..-1.1666665) markAsChecked(0)
            2 -> if (task12(7U, 6U) == "7 + 6 = 13" &&
                task12(12U, 15U) == "12 + 15 = 27") markAsChecked(1)
            3 -> if (task13(0.0, 5.4f) && !task13(7.0, 5.4f) &&
                !task13(-6.0, -1.0f) && task13(6.0, 9.1f) &&
                !task13(6.0, -1.0f) && task13(1.0, 1.1f)) markAsChecked(2)
            4 -> if (task14(-2, 5) == "-2 + 5 = 3" &&
                task14(-2, -5) == "-2 - 5 = -7") markAsChecked(3)
            5 -> if (task15("DOBRY") == 4 &&
                task15("barDzo dobry") == 5 &&
                task15("doStateczny") == 3 &&
                task15("Dopuszczający") == 2 &&
                task15("NIEDOSTATECZNY") == 1 &&
                task15("XYZ") == -1) markAsChecked(4)
            6 -> if (task16(
                    mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                    mapOf("A" to 1U, "B" to 2U)
                ) == 2U &&
                task16(
                    mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                    mapOf("F" to 1U, "G" to 2U)
                ) == 0U &&
                task16(
                    mapOf("A" to 23U, "B" to 47U, "C" to 30U),
                    mapOf("A" to 1U, "B" to 2U, "C" to 4U)
                ) == 7U) markAsChecked(5)
        }
    }

    // Oznacz zadanie jako zaliczone i aktualizuj pasek postępu
    private fun markAsChecked(index: Int) {
        if (!mBoxes[index].isChecked) {
            mBoxes[index].isChecked = true
            updateProgress()
        }
    }

    // Aktualizacja ProgressBar
    private fun updateProgress() {
        val completedTasks = mBoxes.count { it.isChecked }
        mProgress.progress = completedTasks
    }

    // Zadanie 11: Dzielenie niecałkowite
    private fun task11(a: Int, b: Int): Double {
        return a.toDouble() / b
    }

    // Zadanie 12: Łańcuch "<a> + <b> = <a + b>"
    private fun task12(a: UInt, b: UInt): String {
        return "$a + $b = ${a + b}"
    }

    // Zadanie 13: Sprawdzenie zakresu wartości
    fun task13(a: Double, b: Float): Boolean {
        return a >= 0 && a < b
    }

    // Zadanie 14: Operacje matematyczne w Stringu
    fun task14(a: Int, b: Int): String {
        return if (b >= 0) "$a + $b = ${a + b}" else "$a - ${-b} = ${a + b}"
    }

    // Zadanie 15: Ocena na podstawie nazwy
    fun task15(degree: String): Int {
        return when (degree.lowercase()) {
            "bardzo dobry" -> 5
            "dobry" -> 4
            "dostateczny" -> 3
            "dopuszczający" -> 2
            "niedostateczny" -> 1
            else -> -1
        }
    }

    // Zadanie 16: Liczenie możliwych egzemplarzy
    fun task16(store: Map<String, UInt>, asset: Map<String, UInt>): UInt {
        return asset.keys.minOfOrNull { key ->
            store[key]?.div(asset[key] ?: 1U) ?: 0U
        } ?: 0U
    }
}
