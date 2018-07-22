package com.vinaysshenoy.sqlitespellfix.sample

import android.app.Application
import com.vinaysshenoy.sqlitespellfix.sample.db.AppSqliteHelper

class App : Application() {

	val database by lazy {
		AppSqliteHelper(this)
	}
}