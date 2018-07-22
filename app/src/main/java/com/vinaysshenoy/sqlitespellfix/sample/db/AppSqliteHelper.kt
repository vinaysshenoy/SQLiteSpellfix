package com.vinaysshenoy.sqlitespellfix.sample.db

import android.content.ContentValues
import android.content.Context
import android.support.annotation.WorkerThread
import android.util.Log
import io.requery.android.database.sqlite.SQLiteCustomExtension
import io.requery.android.database.sqlite.SQLiteDatabase
import io.requery.android.database.sqlite.SQLiteDatabaseConfiguration
import io.requery.android.database.sqlite.SQLiteOpenHelper
import java.util.UUID

private const val LOG_TAG = "AppSqliteHelper"

class AppSqliteHelper(context: Context) : SQLiteOpenHelper(context, "app.db", null, 1) {

	override fun onCreate(db: SQLiteDatabase) {
		Log.d(LOG_TAG, "On Create")
		db.execSQL(
				"""
			create table "Test" (
				"id" text primary key,
				"text1" text not null
			)
			""".trimIndent()
		)

		val data = listOf(
				Item(UUID.randomUUID(), "Christmas is coming."),
				Item(
						UUID.randomUUID(), "If I don’t like something, I’ll stay away from it."
				),
				Item(
						UUID.randomUUID(), "Let me help you with your baggage."
				),
				Item(
						UUID.randomUUID(), "She advised him to come back at once."
				),
				Item(
						UUID.randomUUID(), "We have never been to Asia, nor have we visited Africa."
				),
				Item(
						UUID.randomUUID(), "The old apple revels in its authority."
				),
				Item(
						UUID.randomUUID(), "A glittering gem is not enough."
				),
				Item(
						UUID.randomUUID(),
						"There were white out conditions in the town; subsequently, the roads were impassable."
				)
		)

		data
				.map {
					ContentValues(2).apply {
						put("id", it.id.toString())
						put("text1", it.text)
					}
				}
				.forEach {
					db.insert("Test", null, it)
				}

		db.execSQL(
				"""
					create virtual table "Demo" using spellfix1
				""".trimIndent()
		)

		db.execSQL(
				"""
			insert into "Demo"(word) select "text1" from "Test"
		""".trimIndent()
		)

	}

	override fun onUpgrade(
		db: SQLiteDatabase,
		oldVersion: Int,
		newVersion: Int
	) {
		TODO("not implemented")
	}

	override fun createConfiguration(
		path: String?,
		openFlags: Int
	): SQLiteDatabaseConfiguration {
		Log.d(LOG_TAG, "On Create Configuration")
		val config = super.createConfiguration(path, openFlags)

		config.customExtensions.add(SQLiteCustomExtension("libspellfix3.so", "sqlite3_spellfix_init"))

		return config
	}

	@WorkerThread
	fun items(): List<ReadItem> {
		return readableDatabase
				.query(
						"""
			select * from "Test"
		""".trimIndent()
				)
				.run {
					generateSequence {
						if (moveToNext()) {
							ReadItem(
									id = UUID.fromString(getString(getColumnIndex("id"))),
									text = getString(getColumnIndex("text1")),
									editDistance = 0
							)
						} else null
					}.toList()
				}
	}

	@WorkerThread
	fun itemsMatching(pattern: String): List<ReadItem> {
		return readableDatabase
				.query(
						"""
			select "word", editdist3('$pattern', "word") as "editdist" from "Demo" order by "editdist" asc
		""".trimIndent()
				)
				.run {
					generateSequence {
						if (moveToNext()) {
							ReadItem(
//									id = UUID.fromString(getString(getColumnIndex("id"))),
									id = UUID.randomUUID(),
									text = getString(getColumnIndex("word")),
									editDistance = getInt(getColumnIndex("editdist"))
							)
						} else null
					}.toList()
				}
	}
}