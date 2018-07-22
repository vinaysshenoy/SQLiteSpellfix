package com.vinaysshenoy.sqlitespellfix.sample.db

import java.util.UUID

data class Item(
	val id: UUID,
	val text: String
)

data class ReadItem(
	val id: UUID,
	val text: String,
	val editDistance: Int
)