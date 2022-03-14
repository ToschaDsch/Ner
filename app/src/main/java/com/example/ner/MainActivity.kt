package com.example.ner

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ner.databinding.ActivityMainBinding

var numberOfFields: Int = 3
var arrayOfNumbersFields = arrayListOf<Int>(1, 2, 3)
var firstConsoleIsUsed: Boolean = false
var lastConsoleIsUsed: Boolean = false
var currentField: Int = 1
var currentGraph: String = "M"

var widthOfApp: Int = 0
var heightOfApp: Int = 0
var widthOfAnElement: Int = 0
var widthOfCheckBox: Int = 0
var NumbersOfTextView: ArrayList<TextView> = ArrayList()
var LengthsInput: ArrayList<EditText> = ArrayList()
var EIInput: ArrayList<EditText> = ArrayList()
var SpringInput: ArrayList<EditText> = ArrayList()
var CheckBoxDrawCurve: ArrayList<RadioButton> = ArrayList()
var ResultAreaPlus: ArrayList<TextView> = ArrayList()
var ResultAreaMinus: ArrayList<TextView> = ArrayList()
var ResultOrdinatePlus: ArrayList<TextView> = ArrayList()
var ResultOrdinateMinus: ArrayList<TextView> = ArrayList()

lateinit var canvas: Canvas
var myPaint = Paint()
var myBitmap = Bitmap.createBitmap(100, 100,  Bitmap.Config.ARGB_8888)

class MainActivity : AppCompatActivity() {
    lateinit var resultSumPlus: TextView
    lateinit var resultSumMinus: TextView
    lateinit var resultSum: TextView
    lateinit var checkSprings: CheckBox

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI() // hide navigation bar

        getSize()
        makeArrayOfDate()
        sendFunctionToElement()
    }

    private fun sendFunctionToElement() {
        sendFunctionToButtons()
        sendFunctionToCheckBoxes()
        makeSpinner()
        initCanvas()

    }


    private fun initCanvas(){
        myBitmap = Bitmap.createBitmap(
            myWidthImage,
            myHeightImage,
            Bitmap.Config.ARGB_8888
        )
        canvas = Canvas(myBitmap)
        myPaint = Paint()
        myPaint.color = Color.BLACK
        myPaint.strokeWidth = 4F
        myPaint.textSize = textSize
        myPaint.textAlign = Paint.Align.CENTER
        myPaint.style = Paint.Style.STROKE

        myBluePaint.color = ContextCompat.getColor(this, R.color.my_blue_color)
        myBluePaint.strokeWidth = 4F
        myBluePaint.textSize = textSize
        myBluePaint.textAlign = Paint.Align.CENTER

        binding.imageView.background = BitmapDrawable(resources, myBitmap)
    }

    private fun canvasClean(){
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }


    private fun makeSpinner() {
        val arrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, arrayOfNumbersFields)
        binding.spinnerNumbersOfFields?.adapter = arrayAdapter
        binding.spinnerNumbersOfFields?.onItemSelectedListener = object:
                                            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,      // it is index in the array
                id: Long) {

                currentField = arrayOfNumbersFields[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(
                    this@MainActivity, currentField.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun getSize() {
        widthOfApp = Resources.getSystem().displayMetrics.widthPixels
        heightOfApp = Resources.getSystem().displayMetrics.heightPixels
        val dpScale = Resources.getSystem().displayMetrics.density
        widthOfAnElement = ((widthOfApp-20*dpScale)/12).toInt()
        widthOfCheckBox = ((widthOfApp-20*dpScale)/11).toInt()
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
        var isItEnabled: Boolean
        for (i in 0..12) {
            isItEnabled = !((i==1)||(i>4))
            // make date
            makeNumbersOfText(i, isItEnabled)
            makeLengthsInput(i, isItEnabled)
            makeEIInput(i, isItEnabled)
            if (i<12) makeSpringInput(i, isItEnabled)
            // make results
            makeResultsAreasMax(i, isItEnabled)
            makeResultsAreasMin(i, isItEnabled)
            makeResultsOrdinateMax(i, isItEnabled)
            makeResultsOrdinateMin(i, isItEnabled)
        }
        for (i in 0..14) {
            makeCheckBaxDrawCurve(i)
        }
        otherData()
        makeResults()
        makeResultsSumPlus()
        makeResultsSumMinus()
        makeResultsSum()
    }

    private fun makeResultsSum() {
        val textResultSum = TextView(this)

        // setting height and width
        textResultSum.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // setting text
        textResultSum.text = ("<b>ΣA =</b>").toSpanned()
        textResultSum.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textResultSum.width = widthOfAnElement
        // onClick the text a message will be displayed "HELLO GEEK"
        textResultSum.setOnClickListener()
        {
            Toast.makeText(
                this@MainActivity, "HELLO GEEK LENGTH",
                Toast.LENGTH_LONG
            ).show()
        }
        // Add EditText to LinearLayout
        binding.LayoutResultsSum?.addView(textResultSum)

        resultSum = TextView(this)

        // setting height and width
        resultSum.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // setting text
        resultSum.text = ("<b>0</b>").toSpanned()
        resultSum.textAlignment = View.TEXT_ALIGNMENT_CENTER
        resultSum.width = widthOfAnElement
        // onClick the text a message will be displayed "HELLO GEEK"
        resultSum.setOnClickListener()
        {
            Toast.makeText(
                this@MainActivity, "HELLO GEEK LENGTH",
                Toast.LENGTH_LONG
            ).show()
        }
        // Add EditText to LinearLayout
        binding.LayoutResultsSum?.addView(resultSum)
    }


    private fun makeResultsSumMinus() {
        val textResultSumMinus = TextView(this)

        // setting height and width
        textResultSumMinus.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // setting text
        textResultSumMinus.text = ("<b>ΣA- =</b>").toSpanned()
        textResultSumMinus.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textResultSumMinus.width = widthOfAnElement
        // onClick the text a message will be displayed "HELLO GEEK"
        textResultSumMinus.setOnClickListener()
        {
            Toast.makeText(
                this@MainActivity, "HELLO GEEK LENGTH",
                Toast.LENGTH_LONG
            ).show()
        }
        // Add EditText to LinearLayout
        binding.LayoutResultsSum?.addView(textResultSumMinus)

        resultSumMinus = TextView(this)

        // setting height and width
        resultSumMinus.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // setting text
        resultSumMinus.text = ("<b>0</b>").toSpanned()
        resultSumMinus.textAlignment = View.TEXT_ALIGNMENT_CENTER
        resultSumMinus.width = widthOfAnElement
        // onClick the text a message will be displayed "HELLO GEEK"
        resultSumMinus.setOnClickListener()
        {
            Toast.makeText(
                this@MainActivity, "HELLO GEEK LENGTH",
                Toast.LENGTH_LONG
            ).show()
        }
        // Add EditText to LinearLayout
        binding.LayoutResultsSum?.addView(resultSumMinus)
    }


    private fun makeResultsSumPlus() {
        val textResultSumPlus = TextView(this)

        // setting height and width
        textResultSumPlus.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // setting text
        textResultSumPlus.text = ("<b>ΣA+ =</b>").toSpanned()
        textResultSumPlus.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textResultSumPlus.width = widthOfAnElement
        // onClick the text a message will be displayed "HELLO GEEK"
        textResultSumPlus.setOnClickListener()
        {
            Toast.makeText(
                this@MainActivity, "HELLO GEEK LENGTH",
                Toast.LENGTH_LONG
            ).show()
        }
        // Add EditText to LinearLayout
        binding.LayoutResultsSum?.addView(textResultSumPlus)

        resultSumPlus = TextView(this)

        // setting height and width
        resultSumPlus.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // setting text
        resultSumPlus.text = ("<b>0</b>").toSpanned()
        resultSumPlus.textAlignment = View.TEXT_ALIGNMENT_CENTER
        resultSumPlus.width = widthOfAnElement
        // onClick the text a message will be displayed "HELLO GEEK"
        resultSumPlus.setOnClickListener()
        {
            Toast.makeText(
                this@MainActivity, "HELLO GEEK LENGTH",
                Toast.LENGTH_LONG
            ).show()
        }
        // Add EditText to LinearLayout
        binding.LayoutResultsSum?.addView(resultSumPlus)
    }


    private fun makeResultsOrdinateMin(i: Int, isItEnabled: Boolean) {
        if (i==0) {
            val nameResultsOrdinateMin = TextView(this)
            // setting height and width
            nameResultsOrdinateMin.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            nameResultsOrdinateMin.setText(R.string.ymin)
            nameResultsOrdinateMin.textAlignment = View.TEXT_ALIGNMENT_CENTER
            nameResultsOrdinateMin.width = widthOfAnElement
            // Add EditText to LinearLayout
            binding.LayoutResultsOrdinateMin?.addView(nameResultsOrdinateMin)
        } else {
            ResultOrdinateMinus.add(TextView(this))

            // setting height and width
            ResultOrdinateMinus[i-1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            ResultOrdinateMinus[i-1].text = "0"
            ResultOrdinateMinus[i-1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            ResultOrdinateMinus[i-1].width = widthOfAnElement
            ResultOrdinateMinus[i-1].isEnabled = isItEnabled
            // onClick the text a message will be displayed "HELLO GEEK"
            ResultOrdinateMinus[i-1].setOnClickListener()
            {
                Toast.makeText(
                    this@MainActivity, "HELLO GEEK LENGTH",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Add EditText to LinearLayout
            binding.LayoutResultsOrdinateMin?.addView(ResultOrdinateMinus[i-1])
        }
    }


    private fun makeResultsOrdinateMax(i: Int, isItEnabled: Boolean) {
        if (i==0) {
            val nameResultsOrdinateMin = TextView(this)
            // setting height and width
            nameResultsOrdinateMin.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            nameResultsOrdinateMin.setText(R.string.ymax)
            nameResultsOrdinateMin.textAlignment = View.TEXT_ALIGNMENT_CENTER
            nameResultsOrdinateMin.width = widthOfAnElement
            // Add EditText to LinearLayout
            binding.LayoutResultsOrdinateMax?.addView(nameResultsOrdinateMin)
        } else {
            ResultOrdinatePlus.add(TextView(this))

            // setting height and width
            ResultOrdinatePlus[i-1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            ResultOrdinatePlus[i-1].text = "0"
            ResultOrdinatePlus[i-1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            ResultOrdinatePlus[i-1].width = widthOfAnElement
            ResultOrdinatePlus[i-1].isEnabled = isItEnabled
            // onClick the text a message will be displayed "HELLO GEEK"
            ResultAreaMinus[i-1].setOnClickListener()
            {
                Toast.makeText(
                    this@MainActivity, "HELLO GEEK LENGTH",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Add EditText to LinearLayout
            binding.LayoutResultsOrdinateMax?.addView(ResultOrdinatePlus[i-1])
        }
    }


    private fun makeResultsAreasMin(i: Int, isItEnabled: Boolean) {
        if (i==0) {
            val nameResultsAreaMin = TextView(this)
            // setting height and width
            nameResultsAreaMin.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            nameResultsAreaMin.setText(R.string.areaMinus)
            nameResultsAreaMin.textAlignment = View.TEXT_ALIGNMENT_CENTER
            nameResultsAreaMin.width = widthOfAnElement
            // Add EditText to LinearLayout
            binding.LayoutResultsAreaMin?.addView(nameResultsAreaMin)
        } else {
            ResultAreaMinus.add(TextView(this))

            // setting height and width
            ResultAreaMinus[i-1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            ResultAreaMinus[i-1].text = "0"
            ResultAreaMinus[i-1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            ResultAreaMinus[i-1].width = widthOfAnElement
            ResultAreaMinus[i-1].isEnabled = isItEnabled
            // onClick the text a message will be displayed "HELLO GEEK"
            ResultAreaMinus[i-1].setOnClickListener()
            {
                Toast.makeText(
                    this@MainActivity, "HELLO GEEK LENGTH",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Add EditText to LinearLayout
            binding.LayoutResultsAreaMin?.addView(ResultAreaMinus[i-1])
        }
    }


    private fun makeResultsAreasMax(i: Int, isItEnabled: Boolean) {
        if (i==0) {
            val nameResultsAreaMax = TextView(this)
            // setting height and width
            nameResultsAreaMax.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            nameResultsAreaMax.setText(R.string.areaPlus)
            nameResultsAreaMax.textAlignment = View.TEXT_ALIGNMENT_CENTER
            nameResultsAreaMax.width = widthOfAnElement
            // Add EditText to LinearLayout
            binding.LayoutResultsAreaMax?.addView(nameResultsAreaMax)
        } else {
            ResultAreaPlus.add(TextView(this))

            // setting height and width
            ResultAreaPlus[i-1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            ResultAreaPlus[i-1].text = "0"
            ResultAreaPlus[i-1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            ResultAreaPlus[i-1].width = widthOfAnElement
            ResultAreaPlus[i-1].isEnabled = isItEnabled
            // onClick the text a message will be displayed "HELLO GEEK"
            ResultAreaPlus[i-1].setOnClickListener()
            {
                Toast.makeText(
                    this@MainActivity, "HELLO GEEK LENGTH",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Add EditText to LinearLayout
            binding.LayoutResultsAreaMax?.addView(ResultAreaPlus[i-1])
        }
    }

    private fun makeResults(){
        val nameResults = TextView(this)
        // setting height and width
        nameResults.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // setting text
        nameResults.setText(R.string.results)
        nameResults.textAlignment = View.TEXT_ALIGNMENT_CENTER
        // Add EditText to LinearLayout
        binding.LayoutResults?.addView(nameResults)
    }


    private fun otherData(){
        val nameOtherDate = TextView(this)
        // setting height and width
        nameOtherDate.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // setting text
        nameOtherDate.setText(R.string.otherData)
        nameOtherDate.textAlignment = View.TEXT_ALIGNMENT_CENTER
        // Add EditText to LinearLayout
        binding.LayoutDateBelow?.addView(nameOtherDate)
    }

    private fun makeCheckBaxDrawCurve(i: Int){
        if (i==0) {
            val nameDrawCurve = TextView(this)
            // setting height and width
            nameDrawCurve.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            nameDrawCurve.setText(R.string.drawACurve)
            nameDrawCurve.textAlignment = View.TEXT_ALIGNMENT_CENTER
            nameDrawCurve.width = 2*widthOfCheckBox
            // Add EditText to LinearLayout
            binding.LayoutDrawACurveMQd?.addView(nameDrawCurve)
        } else {
            CheckBoxDrawCurve.add(RadioButton(this))

            // setting height and width
            CheckBoxDrawCurve[i - 1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            when (i-4){
                -3 ->    {CheckBoxDrawCurve[i - 1].text = "M"
                    CheckBoxDrawCurve[i - 1].isChecked = true
                    // Add CheckBox to LinearLayout
                    binding.LayoutDrawACurveMQd?.addView(CheckBoxDrawCurve[i - 1])
                }
                -2 ->    {CheckBoxDrawCurve[i - 1].text = "Q"
                    // Add CheckBox to LinearLayout
                    binding.LayoutDrawACurveMQd?.addView(CheckBoxDrawCurve[i - 1])
                }
                -1 ->    {CheckBoxDrawCurve[i - 1].text = "δ"
                    // Add CheckBox to LinearLayout
                    binding.LayoutDrawACurveMQd?.addView(CheckBoxDrawCurve[i - 1])
                }
                else ->  {CheckBoxDrawCurve[i - 1].text = "R"+ (i-4).toString()
                    // Add CheckBox to LinearLayout
                    binding.LayoutDrawACurveReaction?.addView(CheckBoxDrawCurve[i - 1])
                    if (i-4>3) {
                        CheckBoxDrawCurve[i - 1].isEnabled = false
                    }
                }
            }

            CheckBoxDrawCurve[i - 1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            CheckBoxDrawCurve[i - 1].width = widthOfCheckBox
            // onClick the text a message will be displayed "HELLO GEEK"
            CheckBoxDrawCurve[i - 1].setOnClickListener()
            {
                drawAGraphListener(i - 1)
            }

        }
    }


    private fun makeSpringInput(i: Int, isItEnabled: Boolean){
        if (i==0) {
            checkSprings = CheckBox(this)
            // setting height and width
            checkSprings.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            checkSprings.setText(R.string.springs)
            checkSprings.textAlignment = View.TEXT_ALIGNMENT_CENTER
            checkSprings.width = (widthOfAnElement*1.5).toInt()
            // Add EditText to LinearLayout
            binding.LayoutSprings?.addView(checkSprings)
            checkSprings.setOnClickListener()
            { openAllSprings()
            }
        } else {
            SpringInput.add(EditText(this))

            // setting height and width
            SpringInput[i-1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            SpringInput[i-1].setText("1")
            SpringInput[i-1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            SpringInput[i-1].width = widthOfAnElement
            SpringInput[i-1].isEnabled = false
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


    private fun makeEIInput(i: Int, isItEnabled: Boolean){
        if (i==0) {
            val nameEI = TextView(this)
            // setting height and width
            nameEI.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            nameEI.text = "EI"
            nameEI.textAlignment = View.TEXT_ALIGNMENT_CENTER
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
            EIInput[i-1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            EIInput[i-1].width = widthOfAnElement
            EIInput[i-1].isEnabled = isItEnabled
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


    private fun makeLengthsInput(i: Int, isItEnabled: Boolean){
        if (i==0) {
            val nameLength = TextView(this)
            // setting height and width
            nameLength.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            nameLength.setText(R.string.length)
            nameLength.textAlignment = View.TEXT_ALIGNMENT_CENTER
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
            LengthsInput[i-1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            LengthsInput[i-1].width = widthOfAnElement
            LengthsInput[i-1].isEnabled = isItEnabled
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


    private fun makeNumbersOfText(i: Int, isItEnabled: Boolean){
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
        NumbersOfTextView[i].textAlignment = View.TEXT_ALIGNMENT_CENTER
        NumbersOfTextView[i].width = widthOfAnElement
        NumbersOfTextView[i].isEnabled = isItEnabled
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


    private fun sendFunctionToButtons() {
        // plus button
        binding.btnPlusAField?.setOnClickListener {
            numberOfFields++
            if (numberOfFields == 10) {
                binding.btnPlusAField?.isEnabled = false
            }
            binding.btnMinusAField?.isEnabled = true    //  make enable minusButton
            // make enable data for the field
            val enableNumberOfField: Int = if (lastConsoleIsUsed) (numberOfFields + 1)
            else numberOfFields
            NumbersOfTextView[enableNumberOfField+1].isEnabled = true
            LengthsInput[enableNumberOfField].isEnabled = true
            EIInput[enableNumberOfField].isEnabled = true
            if (checkSprings.isChecked) {
                SpringInput[numberOfFields].isEnabled = true
            }
            CheckBoxDrawCurve[numberOfFields + 3].isEnabled = true
            ResultAreaPlus[enableNumberOfField].isEnabled = true
            ResultAreaMinus[enableNumberOfField].isEnabled = true
            ResultOrdinatePlus[enableNumberOfField].isEnabled = true
            ResultOrdinateMinus[enableNumberOfField].isEnabled = true

            arrayOfNumbersFields.add(enableNumberOfField)
        }

        // minus button
        binding.btnMinusAField?.setOnClickListener {
            numberOfFields--
            if (numberOfFields == 1) {
                binding.btnMinusAField?.isEnabled = false
            }
            binding.btnPlusAField?.isEnabled = true    //  make enable plusButton
            // make unenable data for the field
            val unEnableNumberOfField: Int = if (lastConsoleIsUsed) (numberOfFields + 2)
                                                                else (numberOfFields + 1)
            NumbersOfTextView[unEnableNumberOfField+1].isEnabled = false
            LengthsInput[unEnableNumberOfField].isEnabled = false
            EIInput[unEnableNumberOfField ].isEnabled = false
            if (checkSprings.isChecked) {
                SpringInput[numberOfFields].isEnabled = false
            }
            CheckBoxDrawCurve[numberOfFields+4].isEnabled = false
            ResultAreaPlus[unEnableNumberOfField].isEnabled = false
            ResultAreaMinus[unEnableNumberOfField].isEnabled = false
            ResultOrdinatePlus[unEnableNumberOfField].isEnabled = false
            ResultOrdinateMinus[unEnableNumberOfField].isEnabled = false

            arrayOfNumbersFields.remove(arrayOfNumbersFields.size)
            if (unEnableNumberOfField == currentField) {
                currentField --
            }
            if (currentGraph[0] == 'R') {
                var numberOfGraph: Int = (currentGraph.substringAfter('R')).toInt()
                if (unEnableNumberOfField == numberOfGraph){
                    CheckBoxDrawCurve[numberOfGraph+3].isChecked= false
                    numberOfGraph --
                    CheckBoxDrawCurve[numberOfGraph+3].isChecked = true
                    currentGraph = "R$numberOfGraph"
                }
            }
        }
    }

    private fun drawAGraphListener(index: Int){
        for (i in CheckBoxDrawCurve) {
            i.isChecked = false
        }
        CheckBoxDrawCurve[index].isChecked = true

        currentGraph = when (index) {
            0 -> "M"
            1 -> "Q"
            2 -> "d"
            else -> {
                'R' + (index - 3).toString()
            }
        }
    }

    private fun sendFunctionToCheckBoxes(){
        binding.chbConsoleBeginning?.setOnClickListener {
            if (binding.chbConsoleBeginning!!.isChecked){
                firstConsoleIsUsed = true
                NumbersOfTextView[0].isEnabled = true
                LengthsInput[0].isEnabled = true
                EIInput[0].isEnabled = true
                ResultAreaPlus[0].isEnabled = true
                ResultAreaMinus[0].isEnabled = true
                ResultOrdinatePlus[0].isEnabled = true
                ResultOrdinateMinus[0].isEnabled = true
            }
            else {
                firstConsoleIsUsed = true
                NumbersOfTextView[0].isEnabled = false
                LengthsInput[0].isEnabled = false
                EIInput[0].isEnabled = false
                ResultAreaPlus[0].isEnabled = false
                ResultAreaMinus[0].isEnabled = false
                ResultOrdinatePlus[0].isEnabled = false
                ResultOrdinateMinus[0].isEnabled = false
            }
        }

        binding.chbConsoleEnd?.setOnClickListener {
            if (binding.chbConsoleEnd!!.isChecked){
                lastConsoleIsUsed = true
                val enableNumberOfField = numberOfFields + 1
                NumbersOfTextView[enableNumberOfField+1].isEnabled = true
                LengthsInput[enableNumberOfField].isEnabled = true
                EIInput[enableNumberOfField].isEnabled = true
                if (checkSprings.isChecked) {
                    SpringInput[enableNumberOfField].isEnabled = true
                }
                CheckBoxDrawCurve[numberOfFields + 3].isEnabled = true
                ResultAreaPlus[enableNumberOfField].isEnabled = true
                ResultAreaMinus[enableNumberOfField].isEnabled = true
                ResultOrdinatePlus[enableNumberOfField].isEnabled = true
                ResultOrdinateMinus[enableNumberOfField].isEnabled = true
            }
            else {
                lastConsoleIsUsed = false
                val enableNumberOfField = numberOfFields + 1
                NumbersOfTextView[enableNumberOfField + 1].isEnabled = false
                LengthsInput[enableNumberOfField].isEnabled = false
                EIInput[enableNumberOfField].isEnabled = false
                if (checkSprings.isChecked) {
                    SpringInput[enableNumberOfField].isEnabled = false
                }
                CheckBoxDrawCurve[numberOfFields + 3].isEnabled = false
                ResultAreaPlus[enableNumberOfField].isEnabled = false
                ResultAreaMinus[enableNumberOfField].isEnabled = false
                ResultOrdinatePlus[enableNumberOfField].isEnabled = false
                ResultOrdinateMinus[enableNumberOfField].isEnabled = false
            }
        }
    }
    private fun openAllSprings(){
        if (checkSprings.isChecked) {
            for (i in 0..numberOfFields) {
                SpringInput[i].isEnabled = true
            }
        }
        else {
            for (i in 0..numberOfFields) {
                SpringInput[i].isEnabled = false
            }
        }
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