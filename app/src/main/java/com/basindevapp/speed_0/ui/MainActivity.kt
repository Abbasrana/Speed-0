package com.basindevapp.speed_0.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.basindevapp.speed_0.*
import com.basindevapp.speed_0.model.SpeedModel
import com.basindevapp.speed_0.util.CustomProgressBar
import com.basindevapp.speed_0.util.DoubleClickListener
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity(), LocationListener {

/*    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var progress = 150*/

    lateinit var progressBar: CustomProgressBar
    lateinit var speedTxt: TextView
    lateinit var locationManager: LocationManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val viewModel: MainViewModel by viewModels()
    private var threshold = 0.0
    private var speedMode = SpeedModel()
    private var ringAlert: Ringtone? = null
    private var notification: Uri? = null
    var circleGreen: Drawable? = null
    var circleRed: Drawable? = null
    private val progressBarMaxKm = 150
    private val progressBarMaxMm = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ringAlert = RingtoneManager.getRingtone(applicationContext, notification)
        circleGreen = ContextCompat.getDrawable(this, R.drawable.circle)
        circleRed = ContextCompat.getDrawable(this, R.drawable.circle_red)
        setupViews()
        setupPermission()
        setupObservations()


    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }


    private fun setupViews() {
        progressBar = findViewById(R.id.progressBar)
        progressBar.setSegmentEnds(30, 30, progressBarMaxMm)
        progressBar.setMaxValue(progressBarMaxMm)
        speedTxt = findViewById(R.id.speedTxt)

/*        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            updateProgress()
            handler.postDelayed(runnable, 1000) // Update every second
        }*/
        //  handler.post(runnable)
        speedTxt.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View) {
                viewModel.setSpeedMode()
            }

            override fun onDoubleClick(v: View) {
                openBottomSheet()
            }
        })
    }
/*    private fun updateProgress() {
        if (progress <= 0) {
            handler.removeCallbacks(runnable)
        } else {
            progress -= 10 // Update progress by 10
            progressBar.setProgress(progress)
          //  handler.removeCallbacks(runnable) // Stop updating progress when it reaches 150
        }
    }*/

    private fun setupObservations() {
        viewModel.threshold.observe(this) {
            setSpeedThreshold(it)
        }
    }


    private fun openBottomSheet() {
        supportFragmentManager.let {
            ThresholdBottomSheetFragment.newInstance(Bundle()).apply {
                show(it, tag)
            }
        }
    }

    private fun setSpeedThreshold(speedModel: SpeedModel) {
        if(SpeedMode.KILOMETER.type.equals(speedModel.SpeedMode)){
            progressBar.setFirstSegmentAndDefaultSegment(50,speedModel.speed.toInt())
            progressBar.setMaxValue(progressBarMaxKm)
        }else{
            progressBar.setFirstSegmentAndDefaultSegment(30,speedModel.speed.toInt())
            progressBar.setMaxValue(progressBarMaxMm)
        }

        threshold = speedModel.speed
        speedMode = speedModel
    }

    private fun setupPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ), LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onLocationChanged(p0: Location) {
        val speed = p0.speed
        val currentSpeed = round(speed.toDouble(), 3, RoundingMode.HALF_UP)
        val kmphSpeed = round((currentSpeed * 3.6), 3, RoundingMode.HALF_UP)
        val finalSpeed = speedMode.speedModeValue * kmphSpeed

        speedTxt.text = "${finalSpeed.toInt()}\n${speedMode.SpeedMode}"

        progressBar.setProgress(finalSpeed.toInt())

        if (finalSpeed.toInt() > threshold) {
            playAlert()
        } else {
            if (ringAlert?.isPlaying == true) {
                ringAlert?.stop()
                speedTxt.setTextColor(resources.getColor(R.color.teal_700, theme))
            }
        }
    }

    private fun playAlert() {
        if (ringAlert?.isPlaying == false) {
            ringAlert?.play()
            speedTxt.setTextColor(Color.RED)
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(this)
    }

    private fun round(unRounded: Double, precision: Int, roundingMode: RoundingMode): Double {
        val bd = BigDecimal(unRounded)
        val rounded = bd.setScale(precision, roundingMode)
        return rounded.toDouble()
    }
}