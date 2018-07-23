package com.vinaysshenoy.sqlitespellfix.sample.db

import android.content.Context
import android.support.annotation.WorkerThread
import android.util.Log
import io.requery.android.database.sqlite.SQLiteCustomExtension
import io.requery.android.database.sqlite.SQLiteDatabase
import io.requery.android.database.sqlite.SQLiteDatabaseConfiguration
import io.requery.android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.util.UUID

private const val LOG_TAG = "AppSqliteHelper"

class AppSqliteHelper(context: Context) : SQLiteOpenHelper(context, "app.db", null, 1) {

  init {
    //We can't use an InMemoryDb because that cannot load extensions, so delete the older database file to recreate the data
    SQLiteDatabase.deleteDatabase(File("data/data/${context.packageName}/databases/app.db"))
  }

  override fun onCreate(db: SQLiteDatabase) {
    Log.d(LOG_TAG, "On Create")
    db.execSQL(
        """
			create table "Test" (
        "rowId" integer primary key autoincrement not null,
				"id" text unique not null,
				"text1" text not null,
        "searchText" text not null
			)
			""".trimIndent()
    )

    db.execSQL(
        """
					create virtual table "Demo" using spellfix1
				""".trimIndent()
    )


    names
        .map {
          Item(
              id = UUID.randomUUID(),
              text = it
          )
        }
        .map { it.toContentValues() }
        .forEach {
          db.insert("Test", null, it)
        }

    db.execSQL(
        """
			insert into "Demo"("rowid","word") select "rowId","searchText" from "Test"
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
  fun items(search: String = ""): List<ReadItem> {
    return readableDatabase
        .query("""
          select * from "Test" where "searchText" like '%$search%'
        """.trimIndent()
        ).run {
          use {
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

  }

  @WorkerThread
  fun itemsMatching(pattern: String): List<ReadItem> {
    return readableDatabase
        .query(
            """
			select "Test"."text1", editdist3('$pattern', "word") as "editdist"
        from "Demo" inner join "Test" on "Demo"."rowid" = "Test"."rowId"
        order by "editdist" asc
		""".trimIndent()
        )
        .run {
          use {
            generateSequence {
              if (moveToNext()) {
                ReadItem(
                    //									id = UUID.fromString(getString(getColumnIndex("id"))),
                    id = UUID.randomUUID(),
                    text = getString(getColumnIndex("text1")),
                    editDistance = getInt(getColumnIndex("editdist"))
                )
              } else null
            }.toList()
          }
        }
  }
}