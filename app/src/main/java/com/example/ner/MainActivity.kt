package com.example.ner

import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ner.databinding.ActivityMainBinding

var widthOfApp: Int = 0
var heightOfApp: Int = 0
var widthOfAnElement: Int = 0
var NumbersOfTextView: ArrayList<TextView> = ArrayList()
var LengthsInput: ArrayList<EditText> = ArrayList()
var EIInput: ArrayList<EditText> = ArrayList()
var SpringInput: ArrayList<EditText> = ArrayList()

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI() // hide navigation bar

        getSize()
        makeArrayOfDate()
    }


    private fun getSize() {
        widthOfApp = Resources.getSystem().displayMetrics.widthPixels
        heightOfApp = Resources.getSystem().displayMetrics.heightPixels
        val dpScale = Resources.getSystem().displayMetrics.density
        widthOfAnElement = ((widthOfApp-20*dpScale)/12).toInt()
    }


    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }


    private fun makeArrayOfDate() {
        for (i in 0..12) {
            makeNumbersOfText(i)
            makeLengthsInput(i)
            makeEIInput(i)
            if (i<12) makeSpringInput(i)
        }
    }


    private fun makeSpringInput(i: Int){
        if (i==0) {
            val checkSprings = CheckBox(this)
            // setting height and width
            checkSprings.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            checkSprings.setText(R.string.springs)
            checkSprings.textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
            checkSprings.width = (widthOfAnElement*1.5).toInt()
            // Add EditText to LinearLayout
            binding.LayoutSprings?.addView(checkSprings)
        } else {
            SpringInput.add(EditText(this))

            // setting height and width
            SpringInput[i-1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            SpringInput[i-1].setText("1")
            SpringInput[i-1].textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
            SpringInput[i-1].width = widthOfAnElement
            // onClick the text a message will be displayed "HELLO GEEK"
            SpringInput[i-1].setOnClickListener()
            {
                Toast.makeText(
                    this@MainActivity, "HELLO GEEK LENGTH",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Add EditText to LinearLayout
            binding.LayoutSprings?.addView(SpringInput[i-1])
        }
    }


    private fun makeEIInput(i: Int){
        if (i==0) {
            val nameEI = TextView(this)
            // setting height and width
            nameEI.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            nameEI.text = "EI"
            nameEI.textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
            nameEI.width = widthOfAnElement
            // Add EditText to LinearLayout
            binding.LayoutEI?.addView(nameEI)
        } else {
            EIInput.add(EditText(this))

            // setting height and width
            EIInput[i-1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            EIInput[i-1].setText("1")
            EIInput[i-1].textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
            EIInput[i-1].width = widthOfAnElement
            // onClick the text a message will be displayed "HELLO GEEK"
            EIInput[i-1].setOnClickListener()
            {
                Toast.makeText(
                    this@MainActivity, "HELLO GEEK LENGTH",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Add EditText to LinearLayout
            binding.LayoutEI?.addView(EIInput[i-1])
        }
    }


    private fun makeLengthsInput(i: Int){
        if (i==0) {
            val nameLength = TextView(this)
            // setting height and width
            nameLength.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            nameLength.setText(R.string.length)
            nameLength.textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
            nameLength .width = widthOfAnElement
            // Add EditText to LinearLayout
            binding.LayoutLength.addView(nameLength)
        } else {
            LengthsInput.add(EditText(this))

            // setting height and width
            LengthsInput[i-1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            LengthsInput[i-1].setText("5")
            LengthsInput[i-1].textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
            LengthsInput[i-1].width = widthOfAnElement
            // onClick the text a message will be displayed "HELLO GEEK"
            LengthsInput[i-1].setOnClickListener()
            {
                Toast.makeText(
                    this@MainActivity, "HELLO GEEK LENGTH",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Add EditText to LinearLayout
            binding.LayoutLength.addView(LengthsInput[i-1])
        }
    }


    private fun makeNumbersOfText(i: Int){
        NumbersOfTextView.add(TextView(this))
        // setting height and width
        NumbersOfTextView[i].layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // setting text
        if (i == 0) {
            NumbersOfTextView[i].text = ("<b>N</b>").toSpanned()
        }
        else {
            NumbersOfTextView[i].text = (i-1).toString()
        }
        NumbersOfTextView[i].textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
        NumbersOfTextView[i].width = widthOfAnElement
        // onClick the text a message will be displayed "HELLO GEEK"
        NumbersOfTextView[i].setOnClickListener()
        {
            Toast.makeText(
                this@MainActivity, "HELLO GEEK",
                Toast.LENGTH_LONG
            ).show()
        }
        // Add TextView to LinearLayout
        binding.LayoutNumbers?.addView(NumbersOfTextView[i])
    }
}


fun String.toSpanned() : Spanned {      // make style from HTML
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        return Html.fromHtml(this)
    }
}