package pl.wsei.pam.lab02

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab03.Lab03Activity

class Lab02Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab02)
    }

    fun onBoardSizeSelected(view: View) {
        val intent = Intent(this, Lab03Activity::class.java)

        // Pobranie rozmiaru planszy z tagu przycisku
        val size = (view.tag as String).split(" ").map { it.toInt() }.toIntArray()

        // Przekazanie wymiarów
        intent.putExtra("size", size)
        startActivity(intent)
    }

}
