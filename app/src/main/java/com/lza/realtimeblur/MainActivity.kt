package com.lza.realtimeblur

import android.app.ListActivity
import android.os.Bundle
import android.widget.ListView
import androidx.fragment.app.FragmentActivity

class MainActivity: FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<ListView>(R.id.list).adapter = MyListAdapter(this, R.layout.list_item_blur)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        finish()
    }
}