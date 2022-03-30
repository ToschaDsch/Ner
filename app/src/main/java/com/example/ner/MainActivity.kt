package com.example.ner

import android.content.ActivityNotFoundException
import android.content.Intent
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
import kotlin.math.abs
import kotlin.math.pow

var numberOfFields: Int = 3
var precisionOfFragmentation: Int = 6
var shortestField: Float = 5f
var arrayOfNumbersFields = arrayListOf(1, 2, 3)
var firstConsoleIsUsed: Boolean = false
var lastConsoleIsUsed: Boolean = false
var fieldOfTheSection: Int = 1
var distanceToSection: Float = 2.5f
var currentGraph: String = "M"
var firstNumber: Int = 1
var lastNumber: Int = 3
var k_ber: ArrayList<Float> = arrayListOf(0f, 0f, 0f, 0f)
var xx: FloatArray = FloatArray(0)
var areSpringsUsed: Boolean = false

var textToShare: String = ""

// coordinate for all cases
var xg: ArrayList<Float> = ArrayList()
var mg: ArrayList<Float> = ArrayList()
var qg: ArrayList<Float> = ArrayList()
var dg: ArrayList<Float> = ArrayList()
var rg: MutableList<ArrayList<Float>> = ArrayList()

var f: ArrayList<MyField> = ArrayList()
var rea: ArrayList<Reaction> = ArrayList()

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
    var l1: Float,  // length of the field
    var l0: Float,  // beginning of the field
    var ei1: Float,  //  stiffness of the field
    var numberOfTheField: Int,
    var tableOfMoment: ArrayList<Float> = arrayListOf(0f, 0f, 0f, 0f),
    var tableOfShear: ArrayList<Float> = arrayListOf(0f, 0f, 0f, 0f),
    var tableOfDeflection: ArrayList<Float> = arrayListOf(0f, 0f, 0f, 0f)
) {

    fun drawOneField() { // it really draws one field

        //global nn, kmas, t0, Y0, c_beg, c_end, n_sec, d_sec
        val x0: Float = if (firstConsoleIsUsed) x0OfMyImage + this.l0 / scaleX
                else  x0OfMyImage + (this.l0 - f[0].l1)/ scaleX
        val xn: Float = x0 + this.l1 / scaleX

        var str1: String = 'l' + this.numberOfTheField.toString()
        // draw text for the field
        canvas.drawText(str1, (x0 + xn) / 2, y0OfMyImage - distanceToText, paintTextField)


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

        if (this.numberOfTheField == fieldOfTheSection) {
            // draw section
            val xs: Float = x0 + distanceToSection / scaleX
            canvas.drawLine(xs, y0OfMyImage - 20,
                xs, y0OfMyImage + heightOfField + 20, paintOfTheSection)
        }
    }


    fun fragmentationOfOneField() {  // make fragmentation of the field
        val dfi: Int = (this.l1 / shortestField * precisionOfFragmentation).toInt()
        val df: Float = this.l1 / dfi

        val x: ArrayList<Float> = arrayListOf(0f)

        for (i in 1 until dfi) {
            x.add(x[i - 1] + df)
        }
            x.add(x[dfi-1] + df - 0.0001f)

        var check1 = 0
        if (this.numberOfTheField == fieldOfTheSection) {  // the section is in the field
            for (i in 0 until x.size) {
                if (x[i] == distanceToSection) {
                    x.add(i + 1, (distanceToSection + 0.0001).toFloat())
                    check1 = 1
                    break
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
        }

        // draw fragmentation
        var xi: Float
        val l0: Float = if (firstConsoleIsUsed) 0f else f[0].l1
        for (i in x) {
            xi = x0OfMyImage + ((this.l0 + i - l0) / scaleX)
            canvas.drawLine(xi, y0OfMyImage - heightOfField, xi, y0OfMyImage, paintOfTheSection)
        }

        for (i in x) {
            solveIt(i, this.numberOfTheField)
        }

        if (this.numberOfTheField == numberOfFields + 1) { //# the last point
            solveIt(this.l1, this.numberOfTheField)
        }
        if ((this.numberOfTheField == numberOfFields) and (!lastConsoleIsUsed)) { //# the last point
            solveIt(this.l1, this.numberOfTheField)
        }
    }
}


private fun drawGraph(theGraphIs: String) {
    val nameOfInfluenceLine = "influence line $theGraphIs"
    val str1: String = nameOfInfluenceLine + "/n" + numberOfFields.toString() + "/n"
    canvas.drawText(str1,20f,  20f, paintTextMyMessage)

    when (theGraphIs) {
        "M" ->  drawTheGraphic(mg)  //draw moment
        "Q" ->  drawTheGraphic(qg)  //draw shear
        "d" ->  drawTheGraphic(dg)  //draw deformation
        else -> {val k: Int = theGraphIs.substring(1).toInt()
            val matrix = rg[k]
            drawTheGraphic(matrix)   // draw a reaction
        }
    }
}


private fun drawTheGraphic(listOfOrdinate: ArrayList<Float>) {
    val ymax: Float? = listOfOrdinate.maxByOrNull { it }
    var ymin: Float? = listOfOrdinate.minByOrNull { it }
    if (ymin!! < 0f) {
        ymin = - ymin
    }

    scaleY = if (ymax!! > ymin) (0.5*heightOfMyImage/ ymax).toFloat() else (0.5*heightOfMyImage/ymin).toFloat()
    for (i in 1 until xg.size) {
        canvas.drawLine(
            x0OfMyImage + xg[i - 1] / scaleX, y0OfMyImage,
            x0OfMyImage+ xg[i - 1] / scaleX, y0OfMyImage - listOfOrdinate[i - 1] * scaleY,
            paintGraph)
        canvas.drawLine(
            x0OfMyImage+ xg[i - 1] / scaleX, y0OfMyImage - listOfOrdinate[i - 1] * scaleY,
            x0OfMyImage + xg[i] / scaleX, y0OfMyImage - listOfOrdinate[i] * scaleY,
            paintGraph)
        canvas.drawLine(
            x0OfMyImage + xg[i] / scaleX, y0OfMyImage - listOfOrdinate[i] * scaleY,
        x0OfMyImage + xg[i] / scaleX, y0OfMyImage,
            paintGraph)
    }
}

private fun solveIt(x1: Float, n1: Int) {

    //def solve_it(x1, n1):  # solve the system
    //global nn, d_sec, n_sec, n0, xg, mg, qg, dg, spr
    val nn: Int = numberOfFields
    val stiffnessTensor = Array(nn-1) {FloatArray(nn)} //stiffness tensor

    for (i in 0 until nn-1) {   //# make dii
        stiffnessTensor[i][i] = f[i + 1].l1 / (3 * f[i + 1].ei1) + f[i + 2].l1 / (3 * f[i + 2].ei1) + 1 / f[i + 1].l1.pow(2) * k_ber[i] + (1 / f[i + 1].l1 + 1 / f[i + 2].l1).pow(2) * k_ber[i + 1] + 1 / f[i + 2].l1.pow(2) * k_ber[i + 2]

        if ((i == 0) and (n1 == 1)) {// the first field
            stiffnessTensor[0][nn - 1] = -(f[1].l1 - x1) * x1 * (f[1].l1 + x1) / (6 * f[1].ei1 * f[1].l1) - (1 - x1 / f[1].l1) / f[1].l1 * k_ber[0] + (1 / f[1].l1 + 1 / f[2].l1) * x1 / f[1].l1 * k_ber[1]
            if (nn > 2) {
                stiffnessTensor[1][nn - 1] = -1 / f[2].l1 * x1 / f[1].l1 * k_ber[1]
            }
        }
        else if ((n1 == nn) and (i == nn - 2)) {
            //# the last field
            stiffnessTensor[nn - 2][nn - 1] =
                -(2 * f[nn].l1 - x1) * x1 * (f[nn].l1 - x1) / (6 * f[nn].ei1 * f[nn].l1)-(x1 / f[nn].l1) / f[nn].l1 * k_ber[nn]+(1 - x1 / f[nn].l1) * (1 / f[nn].l1 + 1 / f[nn - 1].l1) * k_ber[nn - 1]
            if (nn > 2) {
                stiffnessTensor[nn - 3][nn - 1] = -1 / f[nn - 1].l1 * k_ber[nn - 1] * (1 - x1 / f[nn].l1)
            }
        }
        else if ((n1 == 0) and (i == 0)) {
            //# there is a console at the beginning
            stiffnessTensor[0][nn - 1] = (f[0].l1 - x1) * f[1].l1 / (6 * f[1].ei1)-(f[0].l1 - x1 + f[1].l1) / f[1].l1 / f[1].l1 * k_ber[0]-(1 / f[1].l1 + 1 / f[2].l1) * (f[0].l1 - x1) / f[1].l1 * k_ber[1]
            if (nn > 2) {
                stiffnessTensor[1][nn - 1] = (f[0].l1 - x1) / f[1].l1 * k_ber[1] / f[2].l1
            }
        }
        else if (n1 == nn + 1) { //  # there is a console at the end
            stiffnessTensor[nn - 2][nn - 1] = x1 * f[nn].l1 / (6 * f[nn].ei1)-(x1 + f[nn].l1) / f[nn].l1 / f[nn].l1 * k_ber[nn]-(x1 / f[nn].l1) * (1 / f[nn].l1 + 1 / f[nn - 1].l1) * k_ber[nn - 1]
            if (nn > 2) {
                stiffnessTensor[nn - 3][nn - 1] = x1 / f[nn].l1 / f[nn - 1].l1 * k_ber[nn - 1]
            }
        }
        else { //  # GENERAL CASE
            if (i == n1 - 2) {//  # xi is left
                stiffnessTensor[i][nn - 1] = -(2 * f[n1].l1 - x1) * x1 * (f[n1].l1 - x1) / (6 * f[n1].ei1 * f[n1].l1)+(1 - x1 / f[n1].l1) * (1 / f[n1].l1 + 1 / f[n1 - 1].l1) * k_ber[n1 - 1]-x1 / f[n1].l1 / f[n1].l1 * k_ber[n1]
                if ((nn - 1 > n1) and (n1 > 1)) {
                    stiffnessTensor[i + 2][nn - 1] = -x1 / f[n1].l1 * k_ber[n1] / f[n1 + 1].l1
                }
            }
            else if (i == n1 - 1) { //  # xi is right
                stiffnessTensor[i][nn - 1] = -(f[n1].l1 + x1) * x1 * (f[n1].l1 - x1) / (6 * f[n1].ei1 * f[n1].l1)-(1 - x1 / f[n1].l1) / f[n1].l1 * k_ber[n1 - 1]+x1 / f[n1].l1 * (1 / f[n1].l1 + 1 / f[n1 + 1].l1) * k_ber[n1]

                if ((2 < n1) and (n1 < nn)) {
                     stiffnessTensor[i - 2][nn - 1] = -(1 - x1 / f[n1].l1) * k_ber[n1 - 1] / f[n1 - 1].l1
                }
            }
        }
    }
    if (nn != 1) {
        //# you need xi, 2 and more fields
        for (i in 0 until nn - 2) { //# make d21, d21 et cetera
            stiffnessTensor[i][i + 1] = f[i + 2].l1 / (6 * f[i + 2].ei1)-(1 / f[i + 1].l1 + 1 / f[i + 2].l1) / f[i + 2].l1 * k_ber[i + 1]-(1 / f[i + 2].l1 + 1 / f[i + 3].l1) / f[i + 3].l1 * k_ber[i + 2]
            stiffnessTensor[i + 1][i] = stiffnessTensor[i][i + 1]
        }
        for (i in 0 until nn - 3) {  //# d13, 31 et cetera
            stiffnessTensor[i][i + 2] = 1 / f[i + 2].l1 / f[i + 3].l1 * k_ber[i + 2]
            stiffnessTensor[i + 2][i] = stiffnessTensor[i][i + 2]
        }
        xx = gaussPivotFunc(stiffnessTensor)

        makeResultManyFields(x1, n1)
    }
    else {
        makeResultOneField(x1, n1)
    }
}


private fun gaussPivotFunc(matrix: Array<FloatArray>): FloatArray {
    /*                  |a11   a12 a13    b1|
            matrix =    |a21   a22 a23    b2|
                        |a31   a32 a33    b3|

    return      xx =    |x1    x2   x3|
     */
    val n: Int = matrix[0].size - 1
    val xx = FloatArray(n)
    var tmp: Float

    //null left at the bottom
    for (k in 0 until n) {
        tmp = matrix[k][k]  //check matrix[k][k]
        if (tmp == 0f) { // degenerate matrix (!!!)
            return xx
        }
        for (i in 0..n) {
            matrix[k][i] /= tmp
        }
        for (i in k+1 until n) {
            tmp = matrix[i][k]/matrix[k][k]
            for (j in 0 until n+1) {
                matrix[i][j] -= matrix[k][j]*tmp
            }
        }
    }

    //null right above
    for (k in n-1 downTo 0) {
        for (i in n downTo 0) {
            matrix[k][i] /= matrix[k][k]
        }
        for (i in k-1 downTo 0){
            tmp = matrix[i][k] / matrix[k][k]
            for (j in n downTo 0) {
                matrix[i][j] -= matrix[k][j] * tmp
            }
        }
    }
    // answer
    for (i in 0 until n) {
        xx[i] = matrix[i][n]
    }
    return xx
}


private fun makeResultManyFields(x1: Float, n1: Int) {
    val nn: Int = numberOfFields
    val nSec: Int = fieldOfTheSection
    val n0: Int = if (firstConsoleIsUsed) 0 else 1
    val n2: Int = if (lastConsoleIsUsed) 1 else 0
    val dSec: Float = distanceToSection
    val mx: Float
    val qx: Float
    val dx: Float
    val rx = FloatArray(numberOfFields + 1)

    when (nSec) {
        1 -> {  //# the first field
            if ((n0 == 0) and (n1 == 0)) { //# there is a console at the beginning
                mx = -dSec / f[1].l1 * xx[0] + (f[0].l1 - x1) * (f[1].l1 - dSec) / f[1].l1
                qx = -(xx[0] / f[1].l1 + (f[0].l1 - x1) / f[1].l1)
                dx = -(f[1].l1 - dSec) * dSec * (f[1].l1 + dSec) / (6 * f[1].ei1 * f[1].l1) * xx[0] + (2 * f[1].l1 - dSec) * dSec * (f[1].l1 - dSec) / (6 * f[1].ei1 * f[1].l1) * (f[0].l1 - x1)
            } else {
                //# there is no console
                mx = -dSec / f[1].l1 * xx[0]  //#+M
                qx = -(xx[0] / f[1].l1)
                dx = -(f[1].l1 - dSec) * dSec * (f[1].l1 + dSec) / (6 * f[1].ei1 * f[1].l1) * xx[0]
            }
        }
        nn -> {  //# the last field
            if ((n2 == 1) and (n1 == nn + 1)) { //# there is a console at the end
                mx = -((f[nn].l1 - dSec) / f[nn].l1 * xx[nn - 2] - x1 * dSec / f[nn].l1)
                qx = (xx[nn - 2] / f[nn].l1 + x1 / f[nn].l1)
                dx = -(2 * f[nn].l1 - dSec) * dSec * (f[nn].l1 - dSec) / (6 * f[nn].ei1 * f[nn].l1) * xx[nn - 2]+(f[nn].l1 - dSec) * dSec * (f[nn].l1 + dSec) / (6 * f[nn].ei1 * f[nn].l1) * x1
            } else {
                //# there is no console at the end
                mx = -((f[nn].l1 - dSec) / f[nn].l1 * xx[nn - 2]) // #+M
                qx = (xx[nn - 2] / f[nn].l1)
                dx = -(2 * f[nn].l1 - dSec) * dSec * (f[nn].l1 - dSec) / (6 * f[nn].ei1 * f[nn].l1) * xx[nn - 2]
            }
        }
        0 -> { //  # there is a console at the beginning, GENERAL CASE
            mx = 0f
            qx = 0f
            dx = (f[0].l1 - dSec) * f[1].l1 * xx[0] / (6 * f[1].ei1)
        }
        nn + 1 -> { //  # there is a console at the end, GENERAL CASE
            mx = 0f
            qx = 0f
            dx = dSec * f[nn].l1 * xx[nn - 2] / (6 * f[nn].ei1)
        }
        else -> { // # an intermediate field, GENERAL CASE
            mx = -(dSec / f[nSec].l1 * xx[nSec - 1] + (f[nSec].l1 - dSec) / f[nSec].l1 * xx[nSec - 2]) // #+M
            qx = (-1 / f[nSec].l1 * xx[nSec - 1] + 1 / f[nSec].l1 * xx[nSec - 2])
            dx = -(f[nSec].l1 - dSec) * dSec * (f[nSec].l1 + dSec) / (6 * f[nSec].ei1 * f[nSec].l1) * xx[nSec - 1] - (2 * f[nSec].l1 - dSec) * dSec * (f[nSec].l1 - dSec) / (6 * f[nSec].ei1 * f[nSec].l1) * xx[nSec - 2]
        }
    }
    makeM0(x1, n1,
        nn, nSec, n0, n2, dSec, mx, qx, dx, rx)
}


private fun makeResultOneField(x1: Float, n1: Int) {
    val nn: Int = numberOfFields
    val nSec: Int = fieldOfTheSection
    val n0: Int = if (firstConsoleIsUsed) 0 else 1
    val n2: Int = if (lastConsoleIsUsed) 1 else 0
    val dSec: Float = distanceToSection
    var mx = 0f
    var qx = 0f
    var dx= 0f
    val rx = FloatArray(nn+1)       // coefficients for bearings

    if ((n1 == 0) and (nSec == 1)) {
        //# the last is on the console at the beginning
        mx = (f[0].l1 - x1) * (f[1].l1 - dSec) / f[1].l1
        qx = -(f[0].l1 - x1) / f[1].l1
        dx = (2 * f[1].l1 - dSec) * dSec * (f[1].l1 - dSec) / (6 * f[1].ei1 * f[1].l1) * (f[0].l1 - x1)
        rx[0] = -(f[0].l1 - x1 + f[1].l1) / f[1].l1
        rx[1] = (f[0].l1 - x1) / f[1].l1
    }
    else if ((n1 == 2) and (nSec == 1)) { //  # the last is on the console on the end
        mx = x1 * dSec / f[1].l1
        qx = x1 / f[1].l1
        dx = (f[nn].l1 - dSec) * dSec * (f[nn].l1 + dSec) / (6 * f[nn].ei1 * f[nn].l1) * x1
        rx[1] = -(x1 + f[1].l1) / f[1].l1
        rx[0] = x1 / f[1].l1
    }
    else { //  # there is an intermediate field, the last is on the console
        if (nSec == 0) { //  # the section is on the first field
            dx = if (n1 == 1) { //  # last is on an intermediate  field
                (2 * f[1].l1 - x1) * x1 * (f[1].l1 - x1) * (f[0].l1 - dSec) / (6 * f[1].l1 * f[1].ei1)
            } else { //# last is on the last field
                -x1 * (f[0].l1 - dSec) * f[1].l1 / (6 * f[1].ei1)
            }
        }
        else if (nSec == nn +1) { //  # the section is on the last field
            if (n1 == 1) { // # last is on an intermediate field
                dx = (f[1].l1 + x1) * x1 * (f[1].l1 - x1) * dSec / (6 * f[1].l1 * f[1].ei1)

            }
            else { // # last is on the first field
                dx = -dSec * (f[0].l1 - x1) * f[1].l1 / (6 * f[1].ei1)
                mx = 0f
                qx = 0f
                rx[1] = -(x1 / f[1].l1)
                rx[0] = -(1 - x1 / f[1].l1)
            }
        }
        else { // intermediate field
            rx[1] = -(x1 / f[1].l1)
            rx[0] = -(1 - x1 / f[1].l1)
        }
    }
    makeM0(x1, n1, nn, nSec, n0, n2, dSec, mx, qx, dx, rx)
}

private fun makeM0(
    x1: Float,
    n1: Int,
    nn: Int,
    nSec: Int,
    n0: Int,
    n2: Int,
    dSec: Float,
    mx1: Float,
    qx1: Float,
    dx1: Float,
    rx: FloatArray
) {
    var mx: Float = mx1
    var qx: Float = qx1
    var dx: Float = dx1

    val b1: Float
    if (nSec == n1) {  //# + M0
        if (n1 == 0) { //# there is a console at the beginning
            if (dSec >= x1) {
                mx = -x1 + dSec
                qx = -1f
                dx = (-((f[0].l1 - x1) * (f[0].l1 - dSec) * f[1].l1 / (3 * f[1].ei1) + ((f[0].l1 - x1) * 0.5 - (f[0].l1 - dSec) / 6) * (f[0].l1 - dSec).pow(2) / f[0].ei1)).toFloat()
            } else {
                mx = 0f
                qx = 0f
                dx = (-((f[0].l1 - x1) * (f[0].l1 - dSec) * f[1].l1 / (3 * f[1].ei1) + ((f[0].l1 - x1) * (f[0].l1 - dSec) * 0.5 - (f[0].l1 - x1).pow(2) / 6) * (f[0].l1 - x1) / f[0].ei1)).toFloat()
            }
        }
        else if (n1 == nn + 1) {  //   # there is a console at the end
            if (x1 > dSec) {
                mx = x1 - dSec
                qx = 1f
                dx = (-x1 * dSec * f[nn].l1 / (3 * f[nn].ei1)-(x1 * 0.5 - dSec / 6) * dSec.pow(2) / f[nn + 1].ei1).toFloat()
            } else {  //# x1 < d_sec
                mx = 0f
                qx = 0f
                dx = (-x1 * dSec * f[nn].l1 / (3 * f[nn].ei1) - (x1 * dSec * 0.5 - x1.pow(2) / 6) * x1 / f[nn + 1].ei1).toFloat()
            }
        }
        else {  //# there is an intermediate field, GENERAL CASE
            if (x1 <= dSec) {
                mx -= (x1 * (f[nSec].l1 - x1) / f[nSec].l1) * (f[nSec].l1 - dSec) / (f[nSec].l1 - x1)
                qx += x1 / f[nSec].l1
                b1 = f[nSec].l1 - x1
                dx = dx - b1 * dSec / (6 * f[nSec].ei1 * f[nSec].l1) * (f[nSec].l1.pow(2) - b1.pow(2) - dSec.pow(2)) - (dSec - x1).pow(3) / (6 * f[nSec].ei1)
            } else {//  # x1 > d_sec
                if ((x1 == 0f) and (n1 != 0)) {
                    mx = 0f
                    qx = 0f
                    dx = 0f
                } else {
                    mx -= (x1 * (f[nSec].l1 - x1) / f[nSec].l1) * dSec / x1
                    qx -= (f[nSec].l1 - x1) / f[nSec].l1
                    b1 = f[nSec].l1 - x1
                    dx -= b1 * dSec / (6 * f[nSec].ei1 * f[nSec].l1) * (f[nSec].l1.pow(2) - b1.pow(2) - dSec.pow(2))
                }
            }
        }
    }

        //# + M0 2
        // # the first field
    if ((nSec == 0) and (n1 == 1) and (nn != 1)){
        dx += (2 * f[1].l1 - x1) * x1 * (f[1].l1 - x1) * (f[0].l1 - dSec) / (6 * f[1].l1 * f[1].ei1)
        }
    if ((nSec == nn + 1) and (n1 == nn) and (nn != 1)) {
        dx += (f[nn].l1 + x1) * x1 * (f[nn].l1 - x1) * dSec / (6 * f[nn].l1 * f[nn].ei1)
    } // # the last field

    makeReaction(x1, n1,
        nn, nSec, n0, n2, dSec, mx, qx, dx, rx)
}


private fun makeReaction(x1: Float, n1: Int,
                         nn: Int,
                         nSec: Int,
                         n0: Int,
                         n2: Int,
                         dSec: Float,
                         mx: Float,
                         qx: Float,
                         dx: Float,
                         rx: FloatArray) {

    //# reactions
    if (nn > 1) {
        when (n1) {
            1 -> { //  # first field
                rx[0] = -1 + x1 / f[1].l1
                rx[1] = -x1 / f[1].l1
            }
            nn -> {  //# last field
                rx[nn] = -x1 / f[nn].l1
                rx[nn - 1] = -1 + x1 / f[nn].l1
            }
            0 -> { //  # we are on the first console
                rx[0] = -(f[0].l1 - x1 + f[1].l1) / f[1].l1
                rx[1] = (f[0].l1 - x1) / f[1].l1
            }
            nn + n2 -> {  //# we are on the last console
                rx[nn] = -(f[nn].l1 + x1) / f[nn].l1
                rx[nn - 1] = x1 / f[nn].l1
            }
            else -> {
                rx[n1 - 1] = rx[n1 - 1] - 1 + x1 / f[n1].l1  //# an intermediate field+M0
                rx[n1] = rx[n1] - x1 / f[n1].l1
            }
        }

        rx[0] = rx[0] - xx[0] / f[1].l1  //# first field
        rx[1] = rx[1] + xx[0] / f[1].l1
        for (i in 2 until nn) { //  # all intermediate fields, GENERAL CASE
            rx[i - 1] = rx[i - 1] - xx[i - 1] / f[i].l1 + xx[i - 2] / f[i].l1
            rx[i] = rx[i] + xx[i - 1] / f[i].l1 - xx[i - 2] / f[i].l1
        }
        rx[nn] = rx[nn] - xx[nn - 2] / f[nn].l1  //# last field
        rx[nn - 1] = rx[nn - 1] + xx[nn - 2] / f[nn].l1

    }

appendData(x1, n1,
    nn, nSec, n0, n2, dSec, mx, qx, dx, rx)
}


private fun appendData(x1: Float, n1: Int,
                       nn: Int,
                       nSec: Int,
                       n0: Int,
                       n2: Int,
                       dSec: Float,
                       mx: Float,
                       qx: Float,
                       dx1: Float,
                       rx: FloatArray ) {
    var dx: Float = dx1
    if (firstConsoleIsUsed) {
        xg.add(x1 + f[n1].l0)
    } else {
        xg.add(x1 + f[n1].l0 - f[0].l1)
    }

    mg.add(mx)
    qg.add(qx)

    if (areSpringsUsed) { //  # there are springs
        when (nSec) {
            0 -> {
                // # console at the beginning
                dx += inter1(
                    k_ber[0] * rx[0],
                    k_ber[1] * rx[1],
                    f[1].l1,
                    -(f[1].l1 + f[0].l1 - dSec)
                )
            }
            nn + 1 -> {//   # console at the end
                dx += inter1(
                    k_ber[nn - 1] * rx[nn - 1],
                    k_ber[nn] * rx[nn],
                    f[nn].l1,
                    f[nn].l1 + dSec
                )
            }
            else -> { //# general case
                dx += inter1(
                    k_ber[nSec - 1] * rx[nSec - 1],
                    k_ber[nSec] * rx[nSec],
                    f[nSec].l1,
                    dSec
                )
            }
        }
    }
    else {
        if ((dSec == 0f) or (dSec == f[nSec].l1)) {
            dx = 0f
        }
    }

    dg.add(dx)
    for (i in 0..nn) {
        rg[i].add(rx[i])
    }
}


private fun inter1(a: Float, b: Float, l1: Float, c: Float): Float { // # the function interpolates between two point
   /* """ a - value at beginning
    b - at the end
    l1 - length between the two points
    c - distance between a and x
""" */

    if (l1 < 0) { //  # it is not crazy
        return a
    }
    return if (abs(c) <= l1) {
        if (a * b >= 0) { //  # the same sign
            (b - a) / l1 * c + a
        } else {  // # the values have different sings
            c / l1 * (b - a) + a
        }
    }
    else if (c > 0) {// # we are right
        (b - a) / l1 * c + a
    }
    else { //  # we are left
        -(a - b) / l1 * c + b
    }
}


class MainActivity : AppCompatActivity() {
    lateinit var resultSumPlus: TextView
    lateinit var resultSumMinus: TextView
    lateinit var resultSum: TextView
    lateinit var checkSprings: CheckBox

    lateinit var binding: ActivityMainBinding


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
            f.add(MyField(5f, i * 5f, 1f, i))
        }
    }


    private fun drawAll() {
        makeNullAllData()
        canvasClean()
        minField()
        drawAllFields()
        makeAllTables()
        makeTestOfAllDate()
    }


    private fun makeTestOfAllDate() {
        textToShare = "x; M; Q; d; "

        for (i in 0..numberOfFields) {
            textToShare += "R$i; "
        }
        textToShare += '\u000A'
        for (line in 0 until xg.size) {
            textToShare += xg[line].toString() + "; " + mg[line].toString() + "; " + qg[line].toString() + "; " + dg[line].toString() + "; "
            for (i in 0..numberOfFields) {
                textToShare += rg[i][line].toString() + "; "
            }
            textToShare += '\u000A'
        }
    }

    private fun makeAllTables() {
        clearTable()
        var ii1 = 0  //# make the tables
        val n0: Int = if (firstConsoleIsUsed) 0 else 1
        val n2: Int = if (lastConsoleIsUsed) 1 else 0
        ii1 = table(n0, ii1)
        for (i in n0 + 1..numberOfFields + n2) {
            ii1 = table(i, ii1)
        }
        setTable(currentGraph)
    }

    private fun makeNullAllData() {
        xg = ArrayList(0)
        mg = ArrayList(0)
        qg = ArrayList(0)
        dg = ArrayList(0)
        rg = MutableList(numberOfFields + 1) { ArrayList<Float>(0) }
        rea = ArrayList(0)
        for (i in 0..numberOfFields) {
            rea.add(Reaction())
        }
    }

    private fun minField() {
        firstNumber = if (firstConsoleIsUsed) 0 else 1
        lastNumber = if (lastConsoleIsUsed) numberOfFields + 1 else numberOfFields + 0

        shortestField = f[firstNumber].l1
        for (i in firstNumber..lastNumber) {
            if (f[i].l1 < shortestField) {
                shortestField = f[i].l1
            }
        }
    }


    fun drawAllFields() {
        var sumLengthOfFields = 0f
        for (i in 1..numberOfFields) {
            sumLengthOfFields += f[i].l1
        }
        if (firstConsoleIsUsed) sumLengthOfFields += f[0].l1
        if (lastConsoleIsUsed) sumLengthOfFields += f[numberOfFields + 1].l1
        scaleX = sumLengthOfFields / widthOfMyImage

        if (firstConsoleIsUsed) {
            f[0].drawOneField()
            f[0].fragmentationOfOneField()
        }
        for (i in 1..numberOfFields) {
            f[i].drawOneField()
            f[i].fragmentationOfOneField()
        }
        if (lastConsoleIsUsed) {
            f[numberOfFields + 1].drawOneField()
            f[numberOfFields + 1].fragmentationOfOneField()
        }

        drawGraph(currentGraph)
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
                        f[0].l1 = p0
                        f[0].l0 = 0f
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
                        f[0].ei1 = p0
                        EIInput[0].error = null
                    } else {
                        EIInput[0].error = getString(R.string.wrong)
                    }
                } catch (exception: NumberFormatException) {
                    EIInput[0].error = getString(R.string.wrong)
                }
                NumbersOfTextView[1].isEnabled = true
                LengthsInput[0].isEnabled = true
                EIInput[0].isEnabled = true
                ResultAreaPlus[0].isEnabled = true
                ResultAreaMinus[0].isEnabled = true
                ResultOrdinatePlus[0].isEnabled = true
                ResultOrdinateMinus[0].isEnabled = true
                arrayOfNumbersFields.add(0, 0)
                drawAll()
            } else {
                NumbersOfTextView[1].isEnabled = false
                firstConsoleIsUsed = false
                LengthsInput[0].isEnabled = false
                EIInput[0].isEnabled = false
                ResultAreaPlus[0].isEnabled = false
                ResultAreaMinus[0].isEnabled = false
                ResultOrdinatePlus[0].isEnabled = false
                ResultOrdinateMinus[0].isEnabled = false
                arrayOfNumbersFields.removeAt(0)
                if (fieldOfTheSection == 0) {
                    fieldOfTheSection = 1
                    distanceToSection = (f[fieldOfTheSection].l1 * 0.5).toFloat()
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
                        l0 = f[numberOfFields].l0 +
                                f[numberOfFields].l1
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
                    f.add(MyField(l, l0, EI, numberOfFields + 1))
                }
                NumbersOfTextView[numberOfFields + 2].isEnabled = true
                LengthsInput[numberOfFields + 1].isEnabled = true
                EIInput[numberOfFields + 1].isEnabled = true
                ResultAreaPlus[numberOfFields + 1].isEnabled = true
                ResultAreaMinus[numberOfFields + 1].isEnabled = true
                ResultOrdinatePlus[numberOfFields + 1].isEnabled = true
                ResultOrdinateMinus[numberOfFields + 1].isEnabled = true
                arrayOfNumbersFields.add(numberOfFields + 1)
                drawAll()
            } else {
                lastConsoleIsUsed = false
                NumbersOfTextView[numberOfFields + 2].isEnabled = false
                LengthsInput[numberOfFields + 1].isEnabled = false
                EIInput[numberOfFields + 1].isEnabled = false
                ResultAreaPlus[numberOfFields + 1].isEnabled = false
                ResultAreaMinus[numberOfFields + 1].isEnabled = false
                ResultOrdinatePlus[numberOfFields + 1].isEnabled = false
                ResultOrdinateMinus[numberOfFields + 1].isEnabled = false
                arrayOfNumbersFields.removeAt(numberOfFields)
                if (fieldOfTheSection == numberOfFields + 1) {
                    fieldOfTheSection = numberOfFields
                    distanceToSection = (f[fieldOfTheSection].l1 * 0.5).toFloat()
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
        binding.textDistance?.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.textDistance?.imeOptions = 1
        binding.textDistance?.addTextChangedListener(object : TextWatcher {
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
            if ((distance >= 0) and (distance <= f[fieldOfTheSection].l1)) {
                distanceToSection = distance
                binding.textDistance!!.error = null
                drawAll()
            } else {
                distanceToSection = (f[fieldOfTheSection].l1 * 0.5).toFloat()
            }
        } catch (exception: NumberFormatException) {
            binding.textDistance!!.error = getString(R.string.wrong)
        }
    }

    private fun sendFunctionToPrecision() {
        binding.textPrecision?.inputType = InputType.TYPE_CLASS_NUMBER
        binding.textPrecision?.imeOptions = 1
        binding.textPrecision?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                try {
                    if (p0.toString().toFloat() > 2) {
                        precisionOfFragmentation = p0.toString().toInt()
                        binding.textPrecision!!.error = null
                        drawAll()
                    } else {
                        binding.textPrecision!!.error = getString(R.string.wrong)
                    }
                } catch (exception: NumberFormatException) {
                    binding.textPrecision!!.error = getString(R.string.wrong)
                }
            }
        })
    }

    private fun initCanvas() {
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
        paintTextMyMessage.textAlign = Paint.Align.LEFT
        paintTextMyMessage.style = Paint.Style.FILL

        paintOfTheSection = Paint()
        paintOfTheSection.color = ContextCompat.getColor(this, R.color.section)
        paintOfTheSection.strokeWidth = 4F
        paintOfTheSection.textSize = textSize
        paintOfTheSection.textAlign = Paint.Align.CENTER
        paintOfTheSection.style = Paint.Style.STROKE

        binding.imageView.background = BitmapDrawable(resources, myBitmap)
    }


    fun canvasClean() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }


    private fun makeSpinner() {
        val arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, arrayOfNumbersFields
        )
        binding.spinnerNumbersOfFields?.adapter = arrayAdapter
        binding.spinnerNumbersOfFields?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,      // it is index in the array
                id: Long
            ) {

                fieldOfTheSection = arrayOfNumbersFields[position]
                drawAll()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(
                    this@MainActivity, fieldOfTheSection.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun getSize() {
        widthOfApp = Resources.getSystem().displayMetrics.widthPixels
        heightOfApp = Resources.getSystem().displayMetrics.heightPixels
        val dpScale: Float = Resources.getSystem().displayMetrics.density
        widthOfAnElement = ((widthOfApp - 20 * dpScale) / 12).toInt()
        widthOfCheckBox = ((widthOfApp - 20 * dpScale) / 11).toInt()

        widthOfImageView = widthOfApp
        heightOfImageView = (150 * dpScale).toInt()
        widthOfMyImage = (0.8 * widthOfImageView).toInt()
        heightOfMyImage = (0.8 * heightOfImageView).toInt()
        x0OfMyImage = (0.1 * widthOfImageView).toFloat()
        y0OfMyImage = (0.5 * heightOfImageView).toFloat()
        heightOfField *= dpScale
        heightOfBearing *= dpScale
        distanceToText *= dpScale
        textSize *= dpScale
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
            isItEnabled = !((i == 1) || (i > 4))
            // make date
            makeNumbersOfText(i, isItEnabled)
            makeLengthsInput(i, isItEnabled)
            makeEIInput(i, isItEnabled)
            if (i < 12) makeSpringInput(i, isItEnabled)
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
        // Add EditText to LinearLayout
        binding.LayoutResultsSum?.addView(resultSumPlus)
    }


    private fun makeResultsOrdinateMin(i: Int, isItEnabled: Boolean) {
        if (i == 0) {
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
            ResultOrdinateMinus[i - 1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            ResultOrdinateMinus[i - 1].text = "0"
            ResultOrdinateMinus[i - 1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            ResultOrdinateMinus[i - 1].width = widthOfAnElement
            ResultOrdinateMinus[i - 1].isEnabled = isItEnabled
            // Add EditText to LinearLayout
            binding.LayoutResultsOrdinateMin?.addView(ResultOrdinateMinus[i - 1])
        }
    }


    private fun makeResultsOrdinateMax(i: Int, isItEnabled: Boolean) {
        if (i == 0) {
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
            ResultOrdinatePlus[i - 1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            ResultOrdinatePlus[i - 1].text = "0"
            ResultOrdinatePlus[i - 1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            ResultOrdinatePlus[i - 1].width = widthOfAnElement
            ResultOrdinatePlus[i - 1].isEnabled = isItEnabled
            // Add EditText to LinearLayout
            binding.LayoutResultsOrdinateMax?.addView(ResultOrdinatePlus[i - 1])
        }
    }


    private fun makeResultsAreasMin(i: Int, isItEnabled: Boolean) {
        if (i == 0) {
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
            ResultAreaMinus[i - 1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            ResultAreaMinus[i - 1].text = "0"
            ResultAreaMinus[i - 1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            ResultAreaMinus[i - 1].width = widthOfAnElement
            ResultAreaMinus[i - 1].isEnabled = isItEnabled
            // Add EditText to LinearLayout
            binding.LayoutResultsAreaMin?.addView(ResultAreaMinus[i - 1])
        }
    }


    private fun makeResultsAreasMax(i: Int, isItEnabled: Boolean) {
        if (i == 0) {
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
            ResultAreaPlus[i - 1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            ResultAreaPlus[i - 1].text = "0"
            ResultAreaPlus[i - 1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            ResultAreaPlus[i - 1].width = widthOfAnElement
            ResultAreaPlus[i - 1].isEnabled = isItEnabled
            // onClick the text a message will be displayed "HELLO GEEK"
            ResultAreaPlus[i - 1].setOnClickListener()
            {
                Toast.makeText(
                    this@MainActivity, "HELLO GEEK LENGTH",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Add EditText to LinearLayout
            binding.LayoutResultsAreaMax?.addView(ResultAreaPlus[i - 1])
        }
    }

    private fun makeResults() {
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


    private fun otherData() {
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

    private fun makeCheckBaxDrawCurve(i: Int) {
        if (i == 0) {
            val nameDrawCurve = TextView(this)
            // setting height and width
            nameDrawCurve.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            nameDrawCurve.setText(R.string.drawACurve)
            nameDrawCurve.textAlignment = View.TEXT_ALIGNMENT_CENTER
            nameDrawCurve.width = 2 * widthOfCheckBox
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
            when (i - 4) {
                -3 -> {
                    CheckBoxDrawCurve[i - 1].text = "M"
                    CheckBoxDrawCurve[i - 1].isChecked = true
                    // Add CheckBox to LinearLayout
                    binding.LayoutDrawACurveMQd?.addView(CheckBoxDrawCurve[i - 1])
                }
                -2 -> {
                    CheckBoxDrawCurve[i - 1].text = "Q"
                    // Add CheckBox to LinearLayout
                    binding.LayoutDrawACurveMQd?.addView(CheckBoxDrawCurve[i - 1])
                }
                -1 -> {
                    CheckBoxDrawCurve[i - 1].text = "δ"
                    // Add CheckBox to LinearLayout
                    binding.LayoutDrawACurveMQd?.addView(CheckBoxDrawCurve[i - 1])
                }
                else -> {
                    CheckBoxDrawCurve[i - 1].text = "R" + (i - 4).toString()
                    // Add CheckBox to LinearLayout
                    binding.LayoutDrawACurveReaction?.addView(CheckBoxDrawCurve[i - 1])
                    if (i - 4 > 3) {
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


    private fun makeSpringInput(i: Int, isItEnabled: Boolean) {
        if (i == 0) {
            checkSprings = CheckBox(this)
            // setting height and width
            checkSprings.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            checkSprings.setText(R.string.springs)
            checkSprings.textAlignment = View.TEXT_ALIGNMENT_CENTER
            checkSprings.width = (widthOfAnElement * 1.5).toInt()
            // Add EditText to LinearLayout
            binding.LayoutSprings?.addView(checkSprings)
            checkSprings.setOnClickListener()
            {
                openAllSprings()
            }
        } else {
            SpringInput.add(EditText(this))

            // setting height and width
            SpringInput[i - 1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            SpringInput[i - 1].setText("1")
            SpringInput[i - 1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            SpringInput[i - 1].width = widthOfAnElement
            SpringInput[i - 1].isEnabled = false
            SpringInput[i - 1].inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
            SpringInput[i - 1].imeOptions = 1
            // check data of k
            checkInputData(SpringInput[i - 1], i - 1, "springs")
            // Add EditText to LinearLayout
            binding.LayoutSprings?.addView(SpringInput[i - 1])
        }
    }


    private fun makeEIInput(i: Int, isItEnabled: Boolean) {
        if (i == 0) {
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
            EIInput[i - 1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            EIInput[i - 1].setText("1")
            EIInput[i - 1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            EIInput[i - 1].width = widthOfAnElement
            EIInput[i - 1].isEnabled = isItEnabled
            EIInput[i - 1].inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
            EIInput[i - 1].imeOptions = 1
            // check the data of EI
            checkInputData(EIInput[i - 1], i - 1, "EI")
            // Add EditText to LinearLayout
            binding.LayoutEI?.addView(EIInput[i - 1])
        }
    }

    private fun checkInputData(
        editText: EditText,
        index: Int,
        lengthIEorSprings: String
    ) { // check if input > 0,
        // index - number of editText and field
        // lengthIEorSprings - what I have to check
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                try {
                    if (p0.toString().toFloat() > 0) {
                        when (lengthIEorSprings) {
                            "length" -> {
                                f[index].l1 = p0.toString().toFloat()
                                sendBeginningOfTheField()
                            }
                            "EI" -> {
                                f[index].ei1 = p0.toString().toFloat()
                            }
                            "springs" -> {
                                k_ber[index] = 1 / p0.toString().toFloat()
                            }
                        }
                        LengthsInput[index].error = null
                        drawAll()
                    } else {
                        LengthsInput[index].error = getString(R.string.wrong)
                    }
                } catch (exception: NumberFormatException) {
                    LengthsInput[index].error = getString(R.string.wrong)
                }
            }
        })
    }


    private fun makeLengthsInput(i: Int, isItEnabled: Boolean) {
        if (i == 0) {
            val nameLength = TextView(this)
            // setting height and width
            nameLength.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            nameLength.setText(R.string.length)
            nameLength.textAlignment = View.TEXT_ALIGNMENT_CENTER
            nameLength.width = widthOfAnElement
            // Add EditText to LinearLayout
            binding.LayoutLength.addView(nameLength)
        } else {
            LengthsInput.add(EditText(this))
            // setting height and width
            LengthsInput[i - 1].layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // setting text
            LengthsInput[i - 1].setText("5")
            LengthsInput[i - 1].textAlignment = View.TEXT_ALIGNMENT_CENTER
            LengthsInput[i - 1].width = widthOfAnElement
            LengthsInput[i - 1].isEnabled = isItEnabled
            LengthsInput[i - 1].inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
            LengthsInput[i - 1].imeOptions = 1
            // check the data of length
            checkInputData(LengthsInput[i - 1], i - 1, "length")
            // Add EditText to LinearLayout
            binding.LayoutLength.addView(LengthsInput[i - 1])
        }
    }


    private fun sendBeginningOfTheField() {
        f[0].l0 = 0f
        for (index in 1..numberOfFields) {
            f[index].l0 = f[index - 1].l0 +
                    f[index - 1].l1
        }
        if (lastConsoleIsUsed) {
            f[numberOfFields + 1].l0 = f[numberOfFields].l0 +
                    f[numberOfFields].l1
        }
    }


    private fun makeNumbersOfText(i: Int, isItEnabled: Boolean) {
        NumbersOfTextView.add(TextView(this))
        // setting height and width
        NumbersOfTextView[i].layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // setting text
        if (i == 0) {
            NumbersOfTextView[i].text = ("<b>N</b>").toSpanned()
        } else {
            NumbersOfTextView[i].text = (i - 1).toString()
        }
        NumbersOfTextView[i].textAlignment = View.TEXT_ALIGNMENT_CENTER
        NumbersOfTextView[i].width = widthOfAnElement
        NumbersOfTextView[i].isEnabled = isItEnabled
        // Add TextView to LinearLayout
        binding.LayoutNumbers?.addView(NumbersOfTextView[i])
    }

    private fun plusButton() {
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
            NumbersOfTextView[enableNumberOfField + 1].isEnabled = true
            LengthsInput[enableNumberOfField].isEnabled = true
            EIInput[enableNumberOfField].isEnabled = true
            if (checkSprings.isChecked) {
                SpringInput[numberOfFields].isEnabled = true
                k_ber.add(SpringInput[numberOfFields].text.toString().toFloat())
            } else {
                k_ber.add(1f)
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
            l0 = f[numberOfNeuField - 1].l0 + f[numberOfNeuField - 1].l1
        } catch (exception: NumberFormatException) {
            LengthsInput[numberOfNeuField].error = getString(R.string.wrong)
            l = 10f
        }

        var EI: Float
        try {
            EI = EIInput[numberOfNeuField].text.toString().toFloat()
        } catch (exception: NumberFormatException) {
            EIInput[numberOfNeuField].error = getString(R.string.wrong)
            EI = 1f
        }
        f.add(MyField(l, l0, EI, numberOfNeuField))
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
            NumbersOfTextView[unEnableNumberOfField + 1].isEnabled = false
            LengthsInput[unEnableNumberOfField].isEnabled = false
            EIInput[unEnableNumberOfField].isEnabled = false
            if (checkSprings.isChecked) {
                SpringInput[numberOfFields + 1].isEnabled = false
            }
            CheckBoxDrawCurve[numberOfFields + 4].isEnabled = false
            ResultAreaPlus[unEnableNumberOfField].isEnabled = false
            ResultAreaMinus[unEnableNumberOfField].isEnabled = false
            ResultOrdinatePlus[unEnableNumberOfField].isEnabled = false
            ResultOrdinateMinus[unEnableNumberOfField].isEnabled = false

            arrayOfNumbersFields.remove(arrayOfNumbersFields.size)
            if (unEnableNumberOfField == fieldOfTheSection) {
                fieldOfTheSection--
            }
            if (currentGraph[0] == 'R') {
                var numberOfGraph: Int = (currentGraph.substringAfter('R')).toInt()
                if (unEnableNumberOfField == numberOfGraph) {
                    CheckBoxDrawCurve[numberOfGraph + 3].isChecked = false
                    numberOfGraph--
                    CheckBoxDrawCurve[numberOfGraph + 3].isChecked = true
                    currentGraph = "R$numberOfGraph"
                }
            }
            f.removeAt(unEnableNumberOfField)
            k_ber.removeAt(numberOfFields)
            drawAll()
        }
    }

    private fun sendFunctionToButtons() {
        plusButton()
        minusButton()
        shareButton()
    }


    private fun shareButton() {
        binding.shareTheData?.setOnClickListener {
            // intent to share
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToShare) // my text to share
                type = "text/plain" // specify type of intent
            }
            try {
                startActivity(sendIntent)
            } catch (e: ActivityNotFoundException) {

            }
            //create a choose, from which app is to be shared
            //val chosser = Intent.createChooser(intent, "Share using...")
            //startActivity(chosser)
        }
    }


    private fun drawAGraphListener(index: Int) {
        for (i in CheckBoxDrawCurve) {
            i.isChecked = false
        }
        CheckBoxDrawCurve[index].isChecked = true

        when (index) {
            0 -> {
                currentGraph = "M"
                drawAll()
                drawGraph(currentGraph)
            }
            1 -> {
                currentGraph = "Q"
                drawAll()
                drawGraph(currentGraph)
            }
            2 -> {
                currentGraph = "d"
                drawAll()
                drawGraph(currentGraph)
            }
            else -> {
                currentGraph = 'R' + (index - 3).toString()
                drawAll()
                drawGraph(currentGraph)
            }
        }
    }

    private fun sendFunctionToCheckBoxes() {
        binding.chbConsoleBeginning?.setOnClickListener {
            if (binding.chbConsoleBeginning!!.isChecked) {
                firstConsoleIsUsed = true
                NumbersOfTextView[0].isEnabled = true
                LengthsInput[0].isEnabled = true
                EIInput[0].isEnabled = true
                ResultAreaPlus[0].isEnabled = true
                ResultAreaMinus[0].isEnabled = true
                ResultOrdinatePlus[0].isEnabled = true
                ResultOrdinateMinus[0].isEnabled = true
            } else {
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
            if (binding.chbConsoleEnd!!.isChecked) {
                lastConsoleIsUsed = true
                val enableNumberOfField = numberOfFields + 1
                NumbersOfTextView[enableNumberOfField + 1].isEnabled = true
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
            } else {
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

    private fun openAllSprings() {
        if (checkSprings.isChecked) {
            areSpringsUsed = true
            for (i in 0..numberOfFields) {
                SpringInput[i].isEnabled = true
                try {
                    k_ber[i] = 1 / SpringInput[i].text.toString().toFloat()
                } catch (exception: NumberFormatException) {
                    SpringInput[i].error = getString(R.string.wrong)
                    k_ber[i] = 0f
                    areSpringsUsed = false
                }
            }
        } else {
            areSpringsUsed = false
            for (i in 0..numberOfFields) {
                SpringInput[i].isEnabled = false
                k_ber[i] = 0f
            }
        }
    }


    fun String.toSpanned(): Spanned {      // make style from HTML
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            return Html.fromHtml(this)
        }
    }


    private fun clearTable() {
        for (i in 0..11) {
            ResultAreaPlus[i].text = "0"
            ResultAreaMinus[i].text = "0"
            ResultOrdinatePlus[i].text = "0"
            ResultOrdinateMinus[i].text = "0"
        }
    }


    private fun setTable(name: String) { //  # show dates on tables
        val nn: Int = numberOfFields
        val n0: Int = if (firstConsoleIsUsed) 0 else 1
        val n2: Int = if (lastConsoleIsUsed) 1 else 0
        when (name) {
            "M" -> { //  # set moments
                for (i in n0..nn + n2) {
                    ResultAreaPlus[i].text = String.format("%.3f", f[i].tableOfMoment[2])
                    ResultAreaMinus[i].text = String.format("%.3f", f[i].tableOfMoment[3])
                    ResultOrdinatePlus[i].text = String.format("%.3f", f[i].tableOfMoment[0])
                    ResultOrdinateMinus[i].text = String.format("%.3f", f[i].tableOfMoment[1])
                }
            }
            "Q" -> { //  # set shears
                for (i in n0..nn + n2) {
                    ResultAreaPlus[i].text = String.format("%.3f", f[i].tableOfShear[2])
                    ResultAreaMinus[i].text = String.format("%.3f", f[i].tableOfShear[3])
                    ResultOrdinatePlus[i].text = String.format("%.3f", f[i].tableOfShear[0])
                    ResultOrdinateMinus[i].text = String.format("%.3f", f[i].tableOfShear[1])
                }
            }
            "d" -> { // # set deformations
                for (i in n0..nn + n2) {
                    ResultAreaPlus[i].text = String.format("%.3f", f[i].tableOfDeflection[2])
                    ResultAreaMinus[i].text = String.format("%.3f", f[i].tableOfDeflection[3])
                    ResultOrdinatePlus[i].text = String.format("%.3f", f[i].tableOfDeflection[0])
                    ResultOrdinateMinus[i].text = String.format("%.3f", f[i].tableOfDeflection[1])
                }
            }
            else -> { //  # R
                val k: Int = name.substring(1).toInt()
                for (i in n0..nn + n2) {
                    ResultAreaPlus[i].text = String.format("%.3f", rea[k].tableAreaPlus[i])
                    ResultAreaMinus[i].text = String.format("%.3f", rea[k].tableAreaMinus[i])
                    ResultOrdinatePlus[i].text = String.format("%.3f", rea[k].tableOrdinatePlus[i])
                    ResultOrdinateMinus[i].text = String.format("%.3f", rea[k].tableOrdinateMinus[i])
                }
            }
        }
    }

    private fun table(initialNumber: Int, newNumber: Int) : Int { //  # Make tables for all the cases (M, Q, d, Ri)
        // the function solves the data for the tables
        //global xg, mg, qg, rg, rea, n0
        val i: Int = initialNumber
        var ii: Int = newNumber
        var maxM: Float = mg[ii]
        var minM: Float  = mg[ii]
        var amP  = 0f
        var amM  = 0f

        var maxQ: Float = qg[ii]
        var minQ: Float = qg[ii]
        var aqP = 0f
        var aqM = 0f

        var maxD: Float = dg[ii]
        var minD: Float = dg[ii]
        var adP = 0f
        var adM = 0f

        val maxR: ArrayList<Float> = ArrayList()
        val minR: ArrayList<Float> = ArrayList()
        val arP: ArrayList<Float> = ArrayList()
        val arM: ArrayList<Float> = ArrayList()

        var ai: Float

        for (i2 in 0..numberOfFields) {
            maxR.add(rg[i2][ii])
            minR.add(rg[i2][ii])
            arP.add(0f)
            arM.add(0f)
        }

        while ((xg[ii] <= f[i].l0 + f[i].l1) and (ii < xg.size-1)){
            ii += 1

            //# moments
            if (mg[ii] > maxM) {
                maxM = mg[ii]
            }
            if (mg[ii] < minM) {
                minM = mg[ii]
            }
            ai = (.5 * (xg[ii] - xg[ii - 1]) * (mg[ii] + mg[ii - 1])).toFloat()
            if (ai > 0f) {
                amP += ai
            }
            else {
                amM += ai
            }
            //# shear
            if (qg[ii] > maxQ) {
                maxQ = qg[ii]
            }
            if (qg[ii] < minQ) {
                minQ = qg[ii]
            }
            ai = (.5 * (xg[ii] - xg[ii - 1]) * (qg[ii] + qg[ii - 1])).toFloat()
            if (ai > 0f) {
                aqP += ai
            }
            else {
                aqM += ai
            }
            //# deformations
            if (dg[ii] > maxD) {
                maxD = dg[ii]
            }
            if (dg[ii] < minD) {
                minD = dg[ii]
            }
            ai = (.5 * (xg[ii] - xg[ii - 1]) * (dg[ii] + dg[ii - 1])).toFloat()
            if (ai > 0f) {
                adP += ai
            }
            else {
                adM += ai
            }
            for (i2 in 0..numberOfFields) { //   # all reactions
                if (rg[i2][ii] > maxR[i2]) {
                    maxR[i2] = rg[i2][ii]
                }
                if (rg[i2][ii] < minR[i2]) {
                    minR[i2] = rg[i2][ii]
                }
                ai = (.5 * (xg[ii] - xg[ii - 1]) * (rg[i2][ii] + rg[i2][ii - 1])).toFloat()
                if (ai > 0) {
                    arP[i2] += ai
                }
                else {
                    arM[i2] += ai
                }
            }
        }
        //# table                       ymax, ymin, a+,   a-
        f[i].tableOfMoment = arrayListOf(maxM, minM, amP, amM)
        f[i].tableOfShear = arrayListOf(maxQ, minQ, aqP, aqM)
        f[i].tableOfDeflection = arrayListOf(maxD, minD, adP, adM)
        if ((!firstConsoleIsUsed) and (i == 1)) {
            for (i2 in 0..numberOfFields) {
                rea[i2].tableOrdinatePlus.add(0f)
                rea[i2].tableOrdinateMinus.add(0f)
                rea[i2].tableAreaPlus.add(0f)
                rea[i2].tableAreaMinus.add(0f)
            }
        }
        for (i2 in 0..numberOfFields) { //# all reactions
            rea[i2].tableOrdinatePlus.add(maxR[i2])
            rea[i2].tableOrdinateMinus.add(minR[i2])
            rea[i2].tableAreaPlus.add(arP[i2])
            rea[i2].tableAreaMinus.add(arM[i2])
        }
        return ii
    }
}
