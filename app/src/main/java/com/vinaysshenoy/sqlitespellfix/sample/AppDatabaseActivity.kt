package com.vinaysshenoy.sqlitespellfix.sample

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.vinaysshenoy.sqlitespellfix.sample.R.layout
import com.vinaysshenoy.sqlitespellfix.sample.db.ReadItem
import kotlinx.android.synthetic.main.activity_appdatabase.*

private const val LOG_TAG = "AppDatabaseActivity"

class AppDatabaseActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d(LOG_TAG, "On Create")
    setContentView(layout.activity_appdatabase)
    btn_search_levenshtein.setOnClickListener {
      val searchTerm = textfield_searchterm.text.toString()

      if (searchTerm.isBlank())
        Toast.makeText(this, R.string.enter_search_term, Toast.LENGTH_SHORT).show()
      else fetchData(true, searchTerm)
    }

    btn_search_fuzzy.setOnClickListener {
      val searchTerm = textfield_searchterm.text.toString()

      if (searchTerm.isBlank())
        Toast.makeText(this, R.string.enter_search_term, Toast.LENGTH_SHORT).show()
      else fetchData(false, searchTerm)
    }
  }

  private fun fetchData(spellfix: Boolean, searchTerm: String) {
    @SuppressLint("StaticFieldLeak")
    val task = object : AsyncTask<Any, Nothing, Pair<List<ReadItem>, Long>>() {
      override fun doInBackground(vararg params: Any?): Pair<List<ReadItem>, Long>? {

        val now = System.currentTimeMillis()
        val database = (application as App).database
        val items = if (spellfix) database.itemsMatching(searchTerm) else database.items(searchTerm)

        return items to System.currentTimeMillis() - now
      }

      override fun onPostExecute(result: Pair<List<ReadItem>, Long>) {

        val (items, duration) = result

        text_items.text = getString(R.string.display_results, items.joinToString("\n") { "${it.text}, ${it.editDistance}" }, duration)
      }
    }

    task.execute()
  }

}