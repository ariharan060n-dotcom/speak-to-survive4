package com.speaktosurvive.app

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class Contact(val name: String, val number: String)

/** Simple on-device settings storage. Nothing here ever leaves the phone. */
object Prefs {
    const val MAX_CONTACTS = 5
    const val DEFAULT_CODE = "help me now"
    const val DEFAULT_PRESSES = 5

    private fun sp(c: Context): SharedPreferences =
        c.applicationContext.getSharedPreferences("speak_to_survive", Context.MODE_PRIVATE)

    fun userName(c: Context): String = sp(c).getString("name", "") ?: ""
    fun setUserName(c: Context, v: String) {
        sp(c).edit().putString("name", v).apply()
    }

    fun codeWord(c: Context): String = sp(c).getString("code", DEFAULT_CODE) ?: DEFAULT_CODE
    fun setCodeWord(c: Context, v: String) {
        sp(c).edit().putString("code", v).apply()
    }

    fun voiceEnabled(c: Context): Boolean = sp(c).getBoolean("voice_on", true)
    fun setVoiceEnabled(c: Context, v: Boolean) {
        sp(c).edit().putBoolean("voice_on", v).apply()
    }

    fun powerEnabled(c: Context): Boolean = sp(c).getBoolean("power_on", true)
    fun setPowerEnabled(c: Context, v: Boolean) {
        sp(c).edit().putBoolean("power_on", v).apply()
    }

    fun powerPresses(c: Context): Int = sp(c).getInt("presses", DEFAULT_PRESSES)
    fun setPowerPresses(c: Context, v: Int) {
        sp(c).edit().putInt("presses", v).apply()
    }

    fun autoCall(c: Context): Boolean = sp(c).getBoolean("auto_call", true)
    fun setAutoCall(c: Context, v: Boolean) {
        sp(c).edit().putBoolean("auto_call", v).apply()
    }

    fun contacts(c: Context): List<Contact> {
        val raw = sp(c).getString("contacts", "[]") ?: "[]"
        return try {
            val arr = JSONArray(raw)
            val out = ArrayList<Contact>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(Contact(o.getString("n"), o.getString("p")))
            }
            out
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun setContacts(c: Context, list: List<Contact>) {
        val arr = JSONArray()
        for (ct in list) {
            arr.put(JSONObject().put("n", ct.name).put("p", ct.number))
        }
        sp(c).edit().putString("contacts", arr.toString()).apply()
    }

    /** Returns an error message, or null when the code phrase is acceptable. */
    fun codeError(s: String): String? {
        val t = s.trim().lowercase()
        if (!Regex("^[a-z]+( [a-z]+)*$").matches(t)) {
            return "Use only English letters and single spaces"
        }
        val words = t.split(" ")
        if (words.size < 2) return "Use at least 2 words so it is not said by accident"
        if (words.size > 4) return "Use at most 4 words"
        return null
    }

    fun numberOk(s: String): Boolean = Regex("^\\+?[0-9]{6,15}$").matches(s)
}
