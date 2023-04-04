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
import com.basindevapp.speed_0.util.DoubleClickListener
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity(), LocationListener {
    lateinit var progressBar: ProgressBar
    lateinit var speedTxt: TextView
    lateinit var locationManager: LocationManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val viewModel: MainViewModel by viewModels()
    private var threshold = 0
    private var speedMode = SpeedModel()
    private var ringAlert: Ringtone? = null
    private var notification: Uri? = null
    var circleGreen: Drawable? = null
    var circleRed: Drawable? = null
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
        progressBar.isIndeterminate = false
        speedTxt = findViewById(R.id.speedTxt)
        speedTxt.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View) {
                viewModel.setSpeedMode()
            }

            override fun onDoubleClick(v: View) {
                openBottomSheet()
            }
        })
    }

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
        progressBar.max = speedModel.speed
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(finalSpeed.toInt(), true)
        } else {
            progressBar.progress = finalSpeed.toInt()
        }

        if (finalSpeed.toInt() > threshold) {
            playAlert()
        } else {
            if (ringAlert?.isPlaying == true) {
                ringAlert?.stop()
                progressBar.progressDrawable = circleGreen
                speedTxt.setTextColor(resources.getColor(R.color.teal_700, theme))
            }
        }
    }

    private fun playAlert() {
        if (ringAlert?.isPlaying == false) {
            ringAlert?.play()
            progressBar.progressDrawable = circleRed
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