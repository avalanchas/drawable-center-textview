package com.elliecoding.drawablecentertextview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.max

/**
 * Centers the content of a TextView including the compound drawable, by setting start and end
 * padding. Thus, the view can be match_parent, while still aligning its content in the center.
 * Note: currently only supports drawableStart! Neither drawableLeft nor drawableEnd nor
 * drawableRight are supported. Not compatible with TextView's "autoSizeText", which will trigger an
 * infinite loop of onDraw.
 */
class DrawableCenterTextView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas) {
        // We want the icon and/or text grouped together and centered as a group, so we need to accommodate any existing padding
        val totalWidth = (width - paddingStart - paddingEnd)

        // Compute textWidth by finding the widest line
        var textWidth = 0
        val layout = layout
        if (layout != null) {
            for (i in 0 until layout.lineCount) {
                // Careful, getLineEnd is something completely different, we need getLineRight here
                textWidth = max(textWidth, layout.getLineRight(i).toInt())
            }
        }

        // Compute start drawable width, if any
        val drawableStart = compoundDrawables[0]
        val drawableWidth = drawableStart?.intrinsicWidth ?: 0

        // We only count the drawable padding if there is both an icon and text
        val drawablePadding =
            if (textWidth > 0 && drawableStart != null) compoundDrawablePadding else 0

        // Adjust contents to center
        val contentWidth = textWidth + drawableWidth + drawablePadding
        val translate = (totalWidth - contentWidth)
        // Apply a break-condition to not trigger an infinite loop in onDraw
        if (translate > 0 && translate != paddingStart) {
            setPaddingRelative(translate, 0, translate, 0)
        }
        super.onDraw(canvas)
    }
}
