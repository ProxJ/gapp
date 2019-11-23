package com.example.myapplication

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class WorkoutSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_searc)

        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                doMySearch(query)
            }
        }
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL)
    }

    private fun doMySearch(query: String) {
        Log.d("Query", query)
    }
}
