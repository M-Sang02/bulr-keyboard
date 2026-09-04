package com.newfrost.keyboard

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class FrostKeyboardService : InputMethodService() {

    private var input: InputConnection? = null

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        input = currentInputConnection
        setInputView(buildKeyboard())
    }

    private fun buildKeyboard(): View {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(8), dp(8), dp(8))
            background = rounded(Color.argb(225, 28, 40, 52), 28)
        }

        val rows = listOf(
            "QWERTYUIOP",
            "ASDFGHJKL",
            "ZXCVBNM",
            "123  ⌫  SPACE  ↵"
        )

        rows.forEachIndexed { index, text ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }

            if (index < 3) {
                text.forEach { ch ->
                    row.addView(key(ch.toString()))
                }
            } else {
                listOf("123", "⌫", "SPACE", "↵").forEach { label ->
                    row.addView(key(label, if (label == "SPACE") 4f else 1f))
                }
            }

            panel.addView(row, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(52)
            ).apply { bottomMargin = dp(5) })
        }
        return panel
    }

    private fun key(label: String, weight: Float = 1f): View {
        val b = Button(this).apply {
            text = label
            textSize = if (label.length > 1) 12f else 18f
            setTextColor(Color.WHITE)
            isAllCaps = false
            background = rounded(Color.argb(105, 255, 255, 255), 16)
            setOnClickListener { commit(label) }
            stateListAnimator = null
        }
        return b.also {
            it.layoutParams = LinearLayout.LayoutParams(0, dp(48), weight).apply {
                setMargins(dp(2), 0, dp(2), 0)
            }
        }
    }

    private fun commit(label: String) {
        val ic = currentInputConnection ?: return
        when (label) {
            "⌫" -> ic.deleteSurroundingText(1, 0)
            "SPACE" -> ic.commitText(" ", 1)
            "↵" -> ic.sendKeyEvent(android.view.KeyEvent(
                android.view.KeyEvent.ACTION_DOWN,
                android.view.KeyEvent.KEYCODE_ENTER
            ))
            "123" -> ic.commitText("", 1) // numeric layer placeholder
            else -> ic.commitText(label, 1)
        }
    }

    private fun rounded(color: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()
}
