package nl.amsterdam.openmonumentendag.utils

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import nl.amsterdam.openmonumentendag.R


class HeaderView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val greenPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val yellowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val greyDarkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val greyLightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val greyLighterPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var greenPath: Path
    private lateinit var yellowLeftPath: Path
    private lateinit var yellowRightPath: Path
    private lateinit var whitePath: Path
    private lateinit var greyDarkPath: Path
    private lateinit var greyLightPath: Path
    private lateinit var greyLighterPath: Path

    private var str1X: Float = 0f
    private var str1Y: Float = 0f
    private var str2X: Float = 0f
    private var str2Y: Float = 0f
    private var str3X: Float = 0f
    private var str3Y: Float = 0f

    private lateinit var bitmapLogo: Bitmap
    private lateinit var destBitmapLogoRect: Rect

    private var destBitmapLogoX: Float = 0f
    private var destBitmapLogoY: Float = 0f

    private fun init() {
        greenPaint.style = Paint.Style.FILL
        greenPaint.color = ContextCompat.getColor(context, R.color.headerGreen)

        yellowPaint.style = Paint.Style.FILL
        yellowPaint.color = ContextCompat.getColor(context, R.color.headerYellow)

        whitePaint.style = Paint.Style.FILL
        whitePaint.color = ContextCompat.getColor(context, R.color.colorWhite)

        greyDarkPaint.style = Paint.Style.FILL
        greyDarkPaint.color = ContextCompat.getColor(context, R.color.headerGreyDark)

        greyLightPaint.style = Paint.Style.FILL
        greyLightPaint.color = ContextCompat.getColor(context, R.color.headerGreyLight)

        greyLighterPaint.style = Paint.Style.FILL
        greyLighterPaint.color = ContextCompat.getColor(context, R.color.headerGreyLighter)

        textPaint.color = ContextCompat.getColor(context, R.color.headerText)

        val widthF = measuredWidth.toFloat()
        val heightF = measuredHeight.toFloat()

        val leftMark1 = heightF * 0.3f
        val leftMark2 = leftMark1 + heightF * 0.08f
        val leftMark3 = leftMark2 + heightF * 0.58f

        val rightMark1 = heightF * 0.18f
        val rightMark2 = rightMark1 + heightF * 0.06f
        val rightMark3 = rightMark2 + heightF * 0.49f
        val rightMark4 = rightMark3 + heightF * 0.06f
        val rightMark5 = rightMark4 + heightF * 0.08f

        val centerMark1X = widthF * 0.67f
        val centerMark1Y = heightF * 0.67f

        val centerMark2X = widthF * 0.68f
        val centerMark2Y = heightF * 0.69f

        val centerMark3X = widthF * 0.73f
        val centerMark3Y = heightF * 0.85f

        val yellowBreakAtPercent = 0.4f
        val yellowBreakX = (1 - yellowBreakAtPercent) * centerMark2X + yellowBreakAtPercent * widthF
        val yellowBreakY = (1 - yellowBreakAtPercent) * centerMark2Y + yellowBreakAtPercent * rightMark2

        greenPath = Path()
        greenPath.moveTo(0f, 0f)
        greenPath.lineTo(widthF, 0f)
        greenPath.lineTo(widthF, rightMark1)
        greenPath.lineTo(centerMark1X, centerMark1Y)
        greenPath.lineTo(0f, leftMark1)
        greenPath.lineTo(0f, 0f)
        greenPath.close()

        yellowLeftPath = Path()
        yellowLeftPath.moveTo(0f, leftMark2)
        yellowLeftPath.lineTo(0f, leftMark3)
        yellowLeftPath.lineTo(centerMark3X, centerMark3Y)
        yellowLeftPath.lineTo(centerMark2X, centerMark2Y)
        yellowLeftPath.lineTo(0f, leftMark2)
        yellowLeftPath.close()

        greyDarkPath = Path()
        greyDarkPath.moveTo(widthF, rightMark1)
        greyDarkPath.lineTo(widthF, rightMark2)
        greyDarkPath.lineTo(centerMark2X, centerMark2Y)
        greyDarkPath.lineTo(centerMark1X, centerMark1Y)
        greyDarkPath.lineTo(widthF, rightMark1)
        greyDarkPath.close()

        greyLightPath = Path()
        greyLightPath.moveTo(widthF, rightMark2)
        greyLightPath.lineTo(widthF, rightMark3)
        greyLightPath.lineTo(centerMark2X, centerMark2Y)
        greyLightPath.lineTo(widthF, rightMark2)
        greyLightPath.close()

        greyLighterPath = Path()
        greyLighterPath.moveTo(widthF, rightMark3)
        greyLighterPath.lineTo(widthF, rightMark4)
        greyLighterPath.lineTo(centerMark3X, centerMark3Y)
        greyLighterPath.lineTo(centerMark2X, centerMark2Y)
        greyLighterPath.lineTo(widthF, rightMark3)
        greyLighterPath.close()

        yellowRightPath = Path()
        yellowRightPath.moveTo(widthF, rightMark2)
        yellowRightPath.lineTo(widthF, rightMark5)
        yellowRightPath.lineTo(yellowBreakX, yellowBreakY)
        yellowRightPath.lineTo(widthF, rightMark2)
        yellowRightPath.close()

        whitePath = Path()
        whitePath.moveTo(widthF, rightMark4)
        whitePath.lineTo(widthF, heightF)
        whitePath.lineTo(0f, heightF)
        whitePath.lineTo(centerMark3X, centerMark3Y)
        whitePath.lineTo(widthF, rightMark4)
        whitePath.close()

        textPaint.textSize = widthF * 0.07f
        textPaint.isFakeBoldText = true

        str1X = widthF * 0.36f
        str1Y = heightF * 0.22f

        str2X = widthF * 0.3f
        str2Y = str1Y + textPaint.textSize

        str3X = widthF * 0.56f
        str3Y = str2Y + textPaint.textSize

        bitmapLogo = BitmapFactory.decodeResource(resources, R.drawable.omd)

        val destBitmapLogoWidth = (bitmapLogo.width * .4).toInt()
        val destBitmapLogoHeight = (bitmapLogo.height * .4).toInt()

        destBitmapLogoRect = Rect(0, 0, destBitmapLogoWidth, destBitmapLogoHeight)
        destBitmapLogoX = (widthF / 2) - (destBitmapLogoWidth / 2).toFloat()
        destBitmapLogoY = (heightF / 2) - (destBitmapLogoHeight * 1.2).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(ContextCompat.getColor(context, R.color.headerBackground))
        canvas.drawPath(greenPath, greenPaint)
        canvas.drawPath(yellowLeftPath, yellowPaint)
        canvas.drawPath(greyDarkPath, greyDarkPaint)
        canvas.drawPath(greyLightPath, greyLightPaint)
        canvas.drawPath(greyLighterPath, greyLighterPaint)
        canvas.drawPath(yellowRightPath, yellowPaint)
        canvas.drawPath(whitePath, whitePaint)

        canvas.save()
        canvas.translate(destBitmapLogoX, destBitmapLogoY)
        canvas.drawBitmap(bitmapLogo, null, destBitmapLogoRect, null)
        canvas.restore()
    }

}