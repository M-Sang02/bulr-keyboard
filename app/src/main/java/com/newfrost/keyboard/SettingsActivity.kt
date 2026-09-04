package com.newfrost.keyboard

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(28,40,28,24);setBackgroundColor(Color.rgb(25,29,42))}
        root.addView(TextView(this).apply{text="‹   Cài đặt New Frost Keyboard";textSize=26f;setTextColor(Color.WHITE);setPadding(0,0,0,28)})
        root.addView(section("Ngôn ngữ","Tiếng Việt (Việt Nam) • QWERTY", "Telex / VNI"))
        root.addView(section("Lựa chọn ưu tiên","Hiển thị thanh gợi ý", "Phím số và ký hiệu"))
        root.addView(section("Giao diện","Frost Glass • phím bo góc", "Độ trong suốt và độ cao bàn phím"))
        root.addView(section("Chỉnh sửa và đề xuất","Tự động sửa • gợi ý từ", "Từ điển cá nhân"))
        root.addView(section("Nhập bằng cách lướt","Vuốt để nhập", "Bật / tắt trong bản tiếp theo"))
        root.addView(section("Nhập liệu bằng giọng nói","Tiếng Việt (Việt Nam)", "Dùng dịch vụ nhận dạng giọng nói của Android"))
        root.addView(section("Bàn phím","Mở cài đặt bàn phím Android", "Chọn New Frost Keyboard làm bàn phím mặc định"), LinearLayout.LayoutParams(-1,-2))
        root.getChildAt(root.childCount-1).setOnClickListener{startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))}
        setContentView(root)
    }
    private fun section(title:String,a:String,b:String):LinearLayout{
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(0,16,0,16)}
        box.addView(TextView(this).apply{text=title;textSize=20f;setTextColor(Color.WHITE)})
        box.addView(TextView(this).apply{text="$a\n$b";textSize=15f;setTextColor(Color.LTGRAY);setPadding(34,8,0,0)})
        return box
    }
}
