package com.vinaysshenoy.sqlitespellfix.sample

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.vinaysshenoy.sqlitespellfix.sample.R.layout
import com.vinaysshenoy.sqlitespellfix.sample.db.ReadItem
import kotlinx.android.synthetic.main.activity_appdatabase.btn_search
import kotlinx.android.synthetic.main.activity_appdatabase.text_items
import kotlinx.android.synthetic.main.activity_appdatabase.textfield_searchterm

private const val LOG_TAG = "AppDatabaseActivity"

class AppDatabaseActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Log.d(LOG_TAG, "On Create")
		setContentView(layout.activity_appdatabase)
		btn_search.setOnClickListener {
			val searchTerm = textfield_searchterm.text.toString()

			if (searchTerm.isBlank())
				Toast.makeText(this, R.string.enter_search_term, Toast.LENGTH_SHORT).show()
			else fetchData(searchTerm)
		}
	}

	private fun fetchData(searchTerm: String) {
		@SuppressLint("StaticFieldLeak")
		val task = object : AsyncTask<Any, Nothing, List<ReadItem>>() {
			override fun doInBackground(vararg params: Any?) =
				(application as App).database.itemsMatching(searchTerm)

			override fun onPostExecute(result: List<ReadItem>) {
				text_items.text = result.map { "${it.text}, ${it.editDistance}" }
						.joinToString("\n")
			}
		}

		task.execute()
	}

}