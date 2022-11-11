package com.lza.realtimeblur

import android.os.Bundle
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.lza.realtimeblur.android.RealtimeBlurViewGroup

class MainActivity: FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<ListView>(R.id.list).adapter = MyListAdapter(this, R.layout.list_item_blur)
        val topBlurView = findViewById<RealtimeBlurViewGroup>(R.id.top_blur_view)
        val centerBlurView = findViewById<RealtimeBlurViewGroup>(R.id.center_blur_view)
        val bottomBlurView = findViewById<RealtimeBlurViewGroup>(R.id.bottom_blur_view)

        val radiusSeekBar = findViewById<SeekBar>(R.id.radius)
        val downFactorSeekBar = findViewById<SeekBar>(R.id.downFactors)

        val radiusTextView = findViewById<TextView>(R.id.radius_tv)
        val downFactorsTextView = findViewById<TextView>(R.id.downFactors_tv)

        radiusSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                topBlurView.blurRadius = progress.toFloat()
                centerBlurView.blurRadius = progress.toFloat()
                bottomBlurView.blurRadius = progress.toFloat()

                radiusTextView.text = "radius: ${progress}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        downFactorSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                topBlurView.blurFactor = progress.toFloat()
                centerBlurView.blurFactor = progress.toFloat()
                bottomBlurView.blurFactor = progress.toFloat()

                downFactorsTextView.text = "downFactor: ${progress}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        finish()
    }
}