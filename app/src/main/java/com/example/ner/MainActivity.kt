package com.example.ner

import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ner.databinding.ActivityMainBinding

var widthOfApp: Int = 0
var heightOfApp: Int = 0
var widthOfAnElement: Int = 0

class MainActivity : AppCompatActivity() {

    var NumbersOfTextView: ArrayList<TextView> = ArrayList()
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI() // hide navigation bar

        getSize()
        for (i in 0..12){
            NumbersOfTextView.add(TextView(this))

            // setting height and width
            NumbersOfTextView[i].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
            // setting text
            NumbersOfTextView[i].setText("N"+i.toString())
            NumbersOfTextView[i].width = widthOfAnElement
            // onClick the text a message will be displayed "HELLO GEEK"
            NumbersOfTextView[i].setOnClickListener()
            {
                Toast.makeText(this@MainActivity, "HELLO GEEK",
                    Toast.LENGTH_LONG).show()
            }

            // Add TextView to LinearLayout
            binding.LayoutNumders?.addView(NumbersOfTextView[i])


        }


    }

    private fun getSize() {
        widthOfApp = Resources.getSystem().displayMetrics.widthPixels
        heightOfApp = Resources.getSystem().displayMetrics.heightPixels
        val dpScale = Resources.getSystem().displayMetrics.density
        widthOfAnElement = (widthOfApp/12).toInt()

    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
}


}