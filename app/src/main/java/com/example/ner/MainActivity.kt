package com.example.ner

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ner.databinding.ActivityMainBinding

var numberOfFields: Int = 3
var precisionOfFragmentation: Int = 6
var shortestField: Float = 5f
var arrayOfNumbersFields = arrayListOf(1, 2, 3)
var firstConsoleIsUsed: Boolean = false
var lastConsoleIsUsed: Boolean = false
var currentField: Int = 1
var distanceToSection: Float = 2.5f
var currentGraph: String = "M"

var field: ArrayList<MyField> = ArrayList()

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
var paintField = Paint()
var paintBearing = Paint()
var paintGraph = Paint()
var paintTextField = Paint()
var paintTextBearing = Paint()
var paintTextMyMessage = Paint()
var paintOfTheSection = Paint()
var widthOfMyImage: Int = 0
var heightOfMyImage: Int = 0
var x0OfMyImage: Float = 0f
var y0OfMyImage: Float = 0f
var heightOfField: Float = 5f
var heightOfBearing: Float = 4f
var distanceToText: Float = 30f

var myBitmap = Bitmap.createBitmap(100, 100,  Bitmap.Config.ARGB_8888)!!
var widthOfImageView: Int = 0
var heightOfImageView: Int = 0
var textSize: Float = 12f
var scaleX: Float = 1f
var scaleY: Float = 1f


data class Reaction( // # tables of data for reactions
    var tableAreaPlus: ArrayList<Float> = ArrayList(),
    var tableAreaMinus: ArrayList<Float> = ArrayList(),
    var tableOrdinatePlus: ArrayList<Float> = ArrayList(),
    var tableOrdinateMinus:  ArrayList<Float> = ArrayList()
    )


class MyField(
    var lengthOfTheField: Float,
    var beginningOfTheField: Float,
    var EIofTheField: Float,
    var numberOfTheField: Int
) {

    fun drawOneField() { // it really draws one field

        //global nn, kmas, t0, Y0, c_beg, c_end, n_sec, d_sec
        val x0: Float = if (firstConsoleIsUsed) x0OfMyImage + this.beginningOfTheField / scaleX
                else  x0OfMyImage + (this.beginningOfTheField - field[0].lengthOfTheField)/ scaleX
        val xn: Float = x0 + this.lengthOfTheField / scaleX

        // draw a field
        when (this.numberOfTheField) {
            0 -> { // console at the beginning
                canvas.drawLine(x0, y0OfMyImage, xn, y0OfMyImage + heightOfField, paintField)
            }
            numberOfFields + 1 -> {// console at the end
                canvas.drawLine(x0, y0OfMyImage + heightOfField, xn, y0OfMyImage, paintField)
            }
            else -> { // an intermediate field
                canvas.drawLine(
                    x0,y0OfMyImage + heightOfField,
                    xn, y0OfMyImage + heightOfField,
                    paintField
                )
                // draw text for the field
                var str1: String = 'l' + this.numberOfTheField.toString()
                canvas.drawText(str1, (x0 + xn) / 2, y0OfMyImage - distanceToText, paintTextField)
                // draw bearings at the beginning of the field
                canvas.drawCircle(x0,
                    (y0OfMyImage + 1.5*heightOfField + heightOfBearing).toFloat(), heightOfBearing, paintBearing)
                str1 = 's' + (this.numberOfTheField - 1).toString()
                // draw text to bearing at the beginning of thr field
                canvas.drawText(str1, x0, y0OfMyImage + distanceToText + textSize, paintTextBearing)
                // draw text to bearing at the end of thr field
                str1 = "s" +this.numberOfTheField.toString()
                canvas.drawText(str1, xn,
                    y0OfMyImage + distanceToText + textSize, paintTextBearing)
                // draw bearings at the end of the field
                canvas.drawCircle(xn,
                    (y0OfMyImage + 1.5*heightOfField + heightOfBearing).toFloat(), heightOfBearing, paintBearing)
            }
        }
        // draw a line above
        canvas.drawLine(
            x0,y0OfMyImage,
            xn,y0OfMyImage,
            paintField
        )

        if (this.numberOfTheField == currentField) {
            // draw section
            val xs: Float = x0 + distanceToSection / scaleX
            canvas.drawLine(xs, y0OfMyImage - 20,
                xs, y0OfMyImage + heightOfField + 20, paintOfTheSection)
        }
    }


    fun fragmentationOfOneField() {  // make fragmentation of the field
        val dfi: Int = (this.lengthOfTheField / shortestField * precisionOfFragmentation).toInt()
        val df: Float = this.lengthOfTheField / dfi

        val x: ArrayList<Float> = arrayListOf(0f)

        for (i in 1..dfi) {
            x.add(x[i - 1] + df)
        }
            x.add((x[dfi] + df - 0.0001f).toFloat())

        var check1: Int = 0
        if (this.numberOfTheField == currentField) {  // the section is in the field
            for (i in 0 until x.size) {
                if (x[i] == distanceToSection) {
                    x.add(i + 1, (distanceToSection + 0.0001).toFloat())
                    check1 = 1
                    break
                }
            }
        }
        if (check1 == 0) {
            for (i in 1 until x.size) {
                if ((x[i - 1] < distanceToSection) and (distanceToSection < x[i])) {
                    x.add(i, distanceToSection)
                    x.add(i + 1, (distanceToSection + 0.0001).toFloat())
                    break
                }
            }
        }

        // draw it
        var xi: Float = 0f
        val l0: Float = if (firstConsoleIsUsed) 0f else field[0].lengthOfTheField
        for (i in x) {
            xi = x0OfMyImage + ((this.beginningOfTheField + i - l0) / scaleX)
            canvas.drawLine(xi, y0OfMyImage - heightOfField, xi, y0OfMyImage, paintOfTheSection)
        }

        for (xi in x) {
            solveIt(xi, this.numberOfTheField)
        }

        if (this.numberOfTheField == numberOfTheField + 1) { //# the last point
            solveIt(this.lengthOfTheField, this.numberOfTheField)
        }
    }
}


private fun solveIt(xi: Float, currentNumberOfTheField: Int) {

}


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
        initFirstModel()
        drawAll()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()
    }

    override fun onRestart() {
        super.onRestart()
        hideSystemUI()
    }

    private fun initFirstModel() {
        for (i in 0..3) {
            field.add(MyField(5f, i*5f, 1f, i))
        }
    }


    private fun drawAll() {
        canvasClean()
        drawAllFields()
        minField()

    }


    private fun minField() {
        val firstNumber: Int = if (firstConsoleIsUsed) 0 else 1
        val lastNumber: Int = if (lastConsoleIsUsed) 1 else 0

        shortestField = field[firstNumber + 1].lengthOfTheField
        for (i in firstNumber + 1..numberOfFields + lastNumber) {
            if (field[i].lengthOfTheField < shortestField) {
                shortestField = field[i].lengthOfTheField
            }
        }
    }


    private fun drawAllFields() {
        var sumLengthOfFields = 0f
        for (i in 1..numberOfFields) {
            sumLengthOfFields += field[i].lengthOfTheField
        }
        if (firstConsoleIsUsed) sumLengthOfFields += field[0].lengthOfTheField
        if (lastConsoleIsUsed) sumLengthOfFields += field[numberOfFields+1].lengthOfTheField
        scaleX =  sumLengthOfFields / widthOfMyImage

        if (firstConsoleIsUsed) {
            field[0].drawOneField()
            field[0].fragmentationOfOneField()
        }
        for (i in 1..numberOfFields) {
            field[i].drawOneField()
            field[i].fragmentationOfOneField()
        }
        if (lastConsoleIsUsed) {
            field[numberOfFields+1].drawOneField()
            field[numberOfFields+1].fragmentationOfOneField()
        }
    }


    private fun sendFunctionToElement() {
        sendFunctionToButtons()
        sendFunctionToCheckBoxes()
        sendFunctionToPrecision()
        sendFunctionToDistance()
        makeSpinner()
        initCanvas()
        initCheckConsole()
    }


    private fun firstConsole() {
        binding.chbConsoleBeginning?.setOnClickListener() {
            if (binding.chbConsoleBeginning!!.isChecked) {
                firstConsoleIsUsed = true
                try {
                    val p0: Float = LengthsInput[0].text.toString().toFloat()
                    if (p0 > 0) {
                        field[0].lengthOfTheField = p0
                        field[0].beginningOfTheField = 0f
                        LengthsInput[0].error = null
                        sendBeginningOfTheField()
                    } else {
                        LengthsInput[0].error = getString(R.string.wrong)
                    }
                } catch (exception: NumberFormatException) {
                    LengthsInput[0].error = getString(R.string.wrong)
                }

                try {
                    val p0: Float = EIInput[0].text.toString().toFloat()
                    if (p0 > 0) {
                        field[0].EIofTheField = p0
                        EIInput[0].error = null
                    } else {
                        EIInput[0].error = getString(R.string.wrong)
                    }
                } catch (exception: NumberFormatException) {
                    EIInput[0].error = getString(R.string.wrong)
                }
                NumbersOfTextView[0].isEnabled = true
                LengthsInput[0].isEnabled = true
                EIInput[0].isEnabled = true
                arrayOfNumbersFields.add(0, 0)
                drawAll()
            }
            else {
                NumbersOfTextView[0].isEnabled = false
                firstConsoleIsUsed = false
                LengthsInput[0].isEnabled = false
                EIInput[0].isEnabled = false
                arrayOfNumbersFields.removeAt(0)
                if (currentField == 0) {
                    currentField = 1
                    distanceToSection = (field[currentField].lengthOfTheField*0.5).toFloat()
                    binding.textDistance?.setText(distanceToSection.toString())
                }
                drawAll()
            }
        }
    }


    private fun lastConsole() {
        binding.chbConsoleEnd?.setOnClickListener() {
            var l: Float
            var l0: Float
            var EI: Float
            if (binding.chbConsoleEnd!!.isChecked) {
                lastConsoleIsUsed = true
                try {
                    l = LengthsInput[numberOfFields + 1].text.toString().toFloat()
                    if (l > 0) {
                        l0 = field[numberOfFields].beginningOfTheField +
                                field[numberOfFields].lengthOfTheField
                        LengthsInput[numberOfFields + 1].error = null
                    } else {
                        LengthsInput[numberOfFields + 1].error = getString(R.string.wrong)
                        l0 = 0f
                        l = 0f
                        EI = 0f
                    }
                } catch (exception: NumberFormatException) {
                    LengthsInput[numberOfFields + 1].error = getString(R.string.wrong)
                    l0 = 0f
                    l = 0f
                    EI = 0f
                }

                try {
                    EI = EIInput[numberOfFields + 1].text.toString().toFloat()
                    if (EI > 0) {
                        EIInput[numberOfFields + 1].error = null
                    } else {
                        EIInput[numberOfFields + 1].error = getString(R.string.wrong)
                        l0 = 0f
                        l = 0f
                        EI = 0f
                    }
                } catch (exception: NumberFormatException) {
                    EIInput[numberOfFields + 1].error = getString(R.string.wrong)
                    l0 = 0f
                    l = 0f
                    EI = 0f
                }
                if ((l > 0) and (l0 > 0) and (EI > 0)) {
                    field.add(MyField(l, l0, EI, numberOfFields + 1))
                }
                LengthsInput[numberOfFields + 1].isEnabled = true
                EIInput[numberOfFields + 1].isEnabled = true
                arrayOfNumbersFields.add(numberOfFields + 1)
                drawAll()
            }
            else {
                lastConsoleIsUsed = false
                LengthsInput[numberOfFields + 1].isEnabled = false
                EIInput[numberOfFields + 1].isEnabled = false
                arrayOfNumbersFields.removeAt(numberOfFields)
                if (currentField == numberOfFields + 1) {
                    currentField = numberOfFields
                    distanceToSection = (field[currentField].lengthOfTheField*0.5).toFloat()
                    binding.textDistance?.setText(distanceToSection.toString())
                }
                drawAll()
            }
        }
    }


    private fun initCheckConsole() {
        firstConsole()
        lastConsole()
    }


    private fun sendFunctionToDistance() {
        binding.textDistance?.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.textDistance?.imeOptions = 1
        binding.textDistance?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(p0: Editable?) {
                checkTheDistance()
            }
        })
    }


    private fun checkTheDistance() {
        try {
            val distance: Float = binding.textDistance?.text.toString().toFloat()
            if ((distance >= 0) and (distance <= field[currentField].lengthOfTheField)){
                distanceToSection = distance
                binding.textDistance!!.error = null
                drawAll()
            }
            else {
                distanceToSection = (field[currentField].lengthOfTheField*0.5).toFloat()
            }
        }
        catch (exception: NumberFormatException) {
            binding.textDistance!!.error = getString(R.string.wrong)
        }
    }

    private fun sendFunctionToPrecision() {
        binding.textPrecision?.inputType = InputType.TYPE_CLASS_NUMBER
        binding.textPrecision?.imeOptions = 1
        binding.textPrecision?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(p0: Editable?) {
                try {
                    if (p0.toString().toFloat()>2){
                        precisionOfFragmentation = p0.toString().toInt()
                        binding.textPrecision!!.error = null
                        drawAll()
                    }
                    else {
                        binding.textPrecision!!.error = getString(R.string.wrong)
                    }
                }
                catch (exception: NumberFormatException) {
                    binding.textPrecision!!.error = getString(R.string.wrong)
                }
            }
        })
    }

    private fun initCanvas(){
        myBitmap = Bitmap.createBitmap(
                widthOfImageView,
                heightOfImageView,
            Bitmap.Config.ARGB_8888
        )
        canvas = Canvas(myBitmap)

        paintField = Paint()
        paintField.color = ContextCompat.getColor(this, R.color.field)
        paintField.strokeWidth = 10F
        paintField.textSize = textSize
        paintField.textAlign = Paint.Align.CENTER
        paintField.style = Paint.Style.STROKE

        paintBearing.color = ContextCompat.getColor(this, R.color.bearing)
        paintBearing.strokeWidth = 4F
        paintBearing.textSize = textSize
        paintBearing.textAlign = Paint.Align.CENTER
        paintField.style = Paint.Style.STROKE

        paintGraph = Paint()
        paintGraph.color = ContextCompat.getColor(this, R.color.graph)
        paintGraph.strokeWidth = 4F
        paintGraph.textSize = textSize
        paintGraph.textAlign = Paint.Align.CENTER
        paintGraph.style = Paint.Style.STROKE

        paintTextField = Paint()
        paintTextField.color = ContextCompat.getColor(this, R.color.textField)
        paintTextField.strokeWidth = 4F
        paintTextField.textSize = textSize
        paintTextField.textAlign = Paint.Align.CENTER
        paintTextField.style = Paint.Style.FILL

        paintTextBearing = Paint()
        paintTextBearing.color = ContextCompat.getColor(this, R.color.textBearing)
        paintTextBearing.strokeWidth = 4F
        paintTextBearing.textSize = textSize
        paintTextBearing.textAlign = Paint.Align.CENTER
        paintTextBearing.style = Paint.Style.FILL

        paintTextMyMessage = Paint()
        paintTextMyMessage.color = ContextCompat.getColor(this, R.color.textMessage)
        paintTextMyMessage.strokeWidth = 4F
        paintTextMyMessage.textSize = textSize
        paintTextMyMessage.textAlign = Paint.Align.CENTER
        paintTextMyMessage.style = Paint.Style.FILL

        paintOfTheSection = Paint()
        paintOfTheSection.color = ContextCompat.getColor(this, R.color.section)
        paintOfTheSection.strokeWidth = 4F
        paintOfTheSection.textSize = textSize
        paintOfTheSection.textAlign = Paint.Align.CENTER
        paintOfTheSection.style = Paint.Style.STROKE

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
                drawAll()
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
        val dpScale: Float = Resources.getSystem().displayMetrics.density
        widthOfAnElement = ((widthOfApp-20*dpScale)/12).toInt()
        widthOfCheckBox = ((widthOfApp-20*dpScale)/11).toInt()

        widthOfImageView = widthOfApp
        heightOfImageView  = (150 * dpScale).toInt()
        widthOfMyImage = (0.8* widthOfImageView).toInt()
        heightOfMyImage = (0.8* heightOfImageView).toInt()
        x0OfMyImage = (0.1 * widthOfImageView).toFloat()
        y0OfMyImage = (0.5 * heightOfImageView).toFloat()
        heightOfField *= dpScale
        heightOfBearing *= dpScale
        distanceToText *= dpScale
        textSize *= dpScale
    }


    private fun hideSystemUI() {
        /*
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

         */
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
                else ->  {
                    CheckBoxDrawCurve[i - 1].text = "R"+ (i-4).toString()
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
            SpringInput[i-1].inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
            SpringInput[i-1].imeOptions = 1
            // check data of k
            checkInputData(SpringInput[i-1], i-1, "springs")
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
            EIInput[i-1].inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
            EIInput[i-1].imeOptions = 1
            // check the data of EI
            checkInputData(EIInput[i-1], i-1, "EI")
            // Add EditText to LinearLayout
            binding.LayoutEI?.addView(EIInput[i-1])
        }
    }

    private fun checkInputData(editText: EditText, index: Int, lengthIEorSprings: String) { // check if input > 0,
        // index - number of editText and field
        // lengthIEorSprings - what I have to check
        editText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(p0: Editable?) {
                try {
                    if (p0.toString().toFloat()>0){
                        when (lengthIEorSprings) {
                            "length" -> { field[index].lengthOfTheField = p0.toString().toFloat()
                                    sendBeginningOfTheField() }
                            "EI"    ->  {field[index].EIofTheField = p0.toString().toFloat()
                            }
                            "springs" -> {

                            }
                        }
                        LengthsInput[index].error = null
                        drawAll()
                    }
                    else {
                        LengthsInput[index].error = getString(R.string.wrong)
                    }
                }
                catch (exception: NumberFormatException) {
                    LengthsInput[index].error = getString(R.string.wrong)
                }
            }
        })
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
            LengthsInput[i-1].inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
            LengthsInput[i-1].imeOptions = 1
            // check the data of length
            checkInputData(LengthsInput[i-1], i-1, "length")
            // Add EditText to LinearLayout
            binding.LayoutLength.addView(LengthsInput[i-1])
        }
    }


    private fun sendBeginningOfTheField() {
        field[0].beginningOfTheField = 0f
        for (index in 1..numberOfFields) {
            field[index].beginningOfTheField = field[index-1].beginningOfTheField +
                    field[index-1].lengthOfTheField
        }
        if (lastConsoleIsUsed) {
            field[numberOfFields + 1].beginningOfTheField = field[numberOfFields].beginningOfTheField +
                    field[numberOfFields].lengthOfTheField
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
        // Add TextView to LinearLayout
        binding.LayoutNumbers?.addView(NumbersOfTextView[i])
    }

    private fun plusButton(){
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

            makeNeuDateOfField(enableNumberOfField)
            drawAll()
        }
    }


    private fun makeNeuDateOfField(numberOfNeuField: Int) {
        var l: Float
        var l0 = 0f
        try {
            l = LengthsInput[numberOfNeuField].text.toString().toFloat()
            l0 = field[numberOfNeuField-1].beginningOfTheField + field[numberOfNeuField-1].lengthOfTheField
        }
        catch (exception: NumberFormatException) {
            LengthsInput[numberOfNeuField].error = getString(R.string.wrong)
            l = 10f
        }

        var EI: Float
        try {
            EI = EIInput[numberOfNeuField].text.toString().toFloat()
        }
        catch (exception: NumberFormatException) {
            EIInput[numberOfNeuField].error = getString(R.string.wrong)
            EI = 1f
        }
        field.add(MyField(l, l0, EI, numberOfNeuField))
    }


    private fun minusButton() {
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
            field.removeAt(unEnableNumberOfField)
            drawAll()
        }
    }

    private fun sendFunctionToButtons() {
        plusButton()
        minusButton()
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