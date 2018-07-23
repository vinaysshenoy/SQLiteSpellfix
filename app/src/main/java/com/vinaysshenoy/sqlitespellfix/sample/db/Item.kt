package com.vinaysshenoy.sqlitespellfix.sample.db

import android.content.ContentValues
import java.util.UUID

val searchReplaceRegex = Regex("[\\s;_\\-:,]")

data class Item(
    val id: UUID,
    val text: String
) {

  fun toContentValues() = ContentValues()
      .apply {
        put("id", id.toString())
        put("text1", text)
        put("searchText", text.replace(searchReplaceRegex, ""))
      }
}

data class ReadItem(
    val id: UUID,
    val text: String,
    val editDistance: Int
)