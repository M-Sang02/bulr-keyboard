package com.newfrost.keyboard

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView

class FrostKeyboardService : InputMethodService() {
    companion object { var activeService: FrostKeyboardService? = null }

    private var suggestions: LinearLayout? = null
    private var composingWord = StringBuilder()
    private var lastCommittedWord = ""
    private var uppercase = false

    override fun onCreate() { super.onCreate(); activeService = this }
    override fun onDestroy() { if (activeService === this) activeService = null; super.onDestroy() }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        composingWord.clear(); lastCommittedWord = ""
        setInputView(buildKeyboard())
    }

    private fun buildKeyboard(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(7), dp(6), dp(7), dp(4))
            background = rounded(Color.argb(242, 22, 30, 42), 24)
        }
        root.addView(buildToolbar(), LinearLayout.LayoutParams(-1, dp(42)))
        root.addView(buildSuggestions(), LinearLayout.LayoutParams(-1, dp(43)))
        listOf("qwertyuiop", "asdfghjkl", "zxcvbnm").forEachIndexed { i, chars ->
            val row = LinearLayout(this).apply { gravity = Gravity.CENTER; orientation = LinearLayout.HORIZONTAL }
            if (i == 1) row.setPadding(dp(11),0,dp(11),0)
            if (i == 2) row.setPadding(dp(34),0,dp(34),0)
            chars.forEach { row.addView(key(if (uppercase) it.uppercase() else it.toString())) }
            root.addView(row, LinearLayout.LayoutParams(-1, dp(47)).apply { bottomMargin = dp(4) })
        }
        val bottom = LinearLayout(this).apply { gravity = Gravity.CENTER; orientation = LinearLayout.HORIZONTAL }
        bottom.addView(actionKey("⇧",1f) { uppercase = !uppercase; setInputView(buildKeyboard()) })
        bottom.addView(actionKey("?123",1.25f) { commit("123") })
        bottom.addView(actionKey("☺",1f) { commit("😊") })
        bottom.addView(actionKey("space",4f) { commit(" ") })
        bottom.addView(actionKey(".",1f) { commit(".") })
        bottom.addView(actionKey("⌫",1.25f) { backspace() })
        root.addView(bottom, LinearLayout.LayoutParams(-1, dp(50)))
        root.addView(buildNavigationBar(), LinearLayout.LayoutParams(-1, dp(31)))
        return root
    }

    private fun buildToolbar(): View {
        val bar = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL; orientation = LinearLayout.HORIZONTAL }
        val buttons = listOf("☷" to { }, "📋" to { }, "⌁" to { }, "⚙" to { openSettings() }, "🎙" to { openVoice() })
        buttons.forEach { (icon, action) ->
            val b = TextView(this).apply { text=icon; textSize=18f; gravity=Gravity.CENTER; setTextColor(Color.WHITE); setOnClickListener{action()} }
            bar.addView(b, LinearLayout.LayoutParams(0,-1,1f))
        }
        return bar
    }

    private fun buildSuggestions(): View {
        val scroll = HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled=false }
        suggestions = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
        scroll.addView(suggestions)
        updateSuggestions("")
        return scroll
    }

    private fun updateSuggestions(prefix: String) {
        val list = if (prefix.isBlank()) listOf("Việt Nam","Xin chào","Bạn có khỏe không?") else SuggestionEngine.suggest(prefix)
        suggestions?.removeAllViews()
        list.forEach { word ->
            val tv=TextView(this).apply { text=word; textSize=15f; setTextColor(Color.WHITE); gravity=Gravity.CENTER; setPadding(dp(13),0,dp(13),0); setOnClickListener{ replaceCurrentWord(word) } }
            suggestions?.addView(tv, LinearLayout.LayoutParams(-2,-1))
        }
    }

    private fun buildNavigationBar(): View {
        val bar=LinearLayout(this).apply { gravity=Gravity.CENTER; orientation=LinearLayout.HORIZONTAL }
        listOf("‹","›","↑","↓","⌘").forEach { icon ->
            val tv=TextView(this).apply { text=icon; textSize=16f; setTextColor(Color.LTGRAY); gravity=Gravity.CENTER; setOnClickListener{ navigate(icon) } }
            bar.addView(tv, LinearLayout.LayoutParams(0,-1,1f))
        }
        return bar
    }

    private fun key(label:String):View = actionKey(label,1f){ commit(label) }
    private fun actionKey(label:String, weight:Float, action:()->Unit):View {
        return Button(this).apply {
            text=label; textSize=if(label.length>3) 12f else 17f; isAllCaps=false; setTextColor(Color.WHITE)
            background=rounded(Color.argb(120,235,245,255),15); stateListAnimator=null; setPadding(0,0,0,0); setOnClickListener{action()}
            layoutParams=LinearLayout.LayoutParams(0,dp(44),weight).apply{setMargins(dp(2),0,dp(2),0)}
        }
    }

    private fun commit(label:String) {
        val ic=currentInputConnection ?: return
        if(label=="123") { ic.commitText("",1); return }
        if(label==" ") { commitComposed(); ic.commitText(" ",1); composingWord.clear(); updateSuggestions(""); return }
        if(label==".") { commitComposed(); ic.commitText(".",1); composingWord.clear(); updateSuggestions(""); return }
        val ch=label.lowercase()
        if(ch.length==1 && ch[0].isLetter()) {
            composingWord.append(ch)
            val converted=VietnameseTelex.convert(composingWord.toString())
            ic.deleteSurroundingText(lastCommittedWord.length,0)
            val out=if(uppercase) converted.uppercase() else converted
            ic.commitText(out,1); lastCommittedWord=out; updateSuggestions(out)
        } else ic.commitText(label,1)
    }

    private fun commitComposed(){ lastCommittedWord="" }
    private fun backspace(){
        val ic=currentInputConnection ?: return
        if(composingWord.isNotEmpty()) { composingWord.deleteCharAt(composingWord.lastIndex); ic.deleteSurroundingText(lastCommittedWord.length,0); lastCommittedWord=""; val out=VietnameseTelex.convert(composingWord.toString()); ic.commitText(out,1); lastCommittedWord=out; updateSuggestions(out) }
        else ic.deleteSurroundingText(1,0)
    }
    private fun replaceCurrentWord(word:String){ val ic=currentInputConnection ?: return; ic.deleteSurroundingText(lastCommittedWord.length,0); ic.commitText(word,1); composingWord.clear(); lastCommittedWord=word; updateSuggestions(word) }
    private fun navigate(icon:String){ val ic=currentInputConnection ?: return; val key=when(icon){"‹"->21;"›"->22;"↑"->19;"↓"->20;else->0}; if(key!=0) ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,key)); if(key!=0) ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_UP,key)) }
    private fun openSettings(){ startActivity(Intent(this,SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    private fun openVoice(){ startActivity(Intent(this,VoiceInputActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    private fun rounded(color:Int,radius:Int)=GradientDrawable().apply{setColor(color);cornerRadius=dp(radius).toFloat()}
    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
}

object SuggestionEngine {
    private val words=listOf("xin","chào","việt","nam","bạn","mình","mình là","không","được","cảm","ơn","hôm","nay","trời","đẹp","bầu trời","điện thoại","bàn phím","gõ tiếng Việt","thời tiết","tôi","có","thể","giúp")
    fun suggest(prefix:String)=words.filter{it.startsWith(prefix.lowercase())}.take(3).ifEmpty{listOf(prefix)}
}

object VietnameseTelex {
    private val toneMarks=mapOf('s' to '\u0301','f' to '\u0300','r' to '\u0309','x' to '\u0303','j' to '\u0323')
    fun convert(input:String):String {
        var s=input.lowercase()
        if(s.isEmpty()) return s
        val tone=s.lastOrNull()?.let{toneMarks[it]}
        if(tone!=null) s=s.dropLast(1)
        s=s.replace("aa","â").replace("aw","ă").replace("dd","đ").replace("ee","ê").replace("oo","ô").replace("ow","ơ").replace("uw","ư")
        if(tone!=null) {
            val idx=findToneIndex(s)
            if(idx>=0) s=s.substring(0,idx)+s[idx]+tone+s.substring(idx+1)
        }
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFC)
    }
    private fun findToneIndex(s:String):Int {
        val candidates=s.mapIndexedNotNull{idx,c->if("aeiouyâăêôơư".contains(c)) idx else null}
        return if(candidates.isEmpty()) -1 else candidates[candidates.size/2]
    }
}
