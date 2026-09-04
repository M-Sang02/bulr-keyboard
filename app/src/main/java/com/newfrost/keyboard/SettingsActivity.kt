package com.newfrost.keyboard

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
            setBackgroundColor(Color.rgb(20, 28, 36))
        }

        root.addView(TextView(this).apply {
            text = "New Frost Keyboard"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        })

        root.addView(TextView(this).apply {
            text = "\nFrost Glass UI\n\n• QWERTY tiếng Việt\n• Khung và phím bo góc\n• Nền kính trong suốt\n• Blur có thể nâng cấp ở bản tiếp theo\n• Telex/VNI và gợi ý từ sẽ được tích hợp tiếp"
            textSize = 17f
            setTextColor(Color.LTGRAY)
        })
        setContentView(root)
    }
}
