
package com.example.recettes.ui

import android.content.Context
import java.text.Normalizer
import java.util.Locale

fun sanitizeTitleToFilename(title: String): String {
    val noDiacritics = Normalizer.normalize(title, Normalizer.Form.NFD)
        .replace("\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    return noDiacritics
        .lowercase(Locale.FRANCE)
        .replace("&", "et")
        .replace("[^a-z0-9]+".toRegex(), "_")
        .trim('_')
}

fun assetImageExists(context: Context, base: String): String? {
    val mgr = context.assets
    val candidates = listOf("$base.jpg", "$base.jpeg", "$base.png", "$base.webp")
    for (c in candidates) {
        try {
            mgr.open("images/" + c).close()
            return "file:///android_asset/images/" + c
        } catch (_: Exception) {}
    }
    return null
}
