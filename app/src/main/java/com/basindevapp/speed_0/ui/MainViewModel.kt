package com.basindevapp.speed_0

import android.hardware.camera2.params.MeteringRectangle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.basindevapp.speed_0.model.SpeedModel
import kotlin.math.roundToInt

class MainViewModel : ViewModel() {
    private val byDefaultThreshold = 50.0
    private val KILOMETER_CONSTANT = 1.0
    private val METER_CONSTANT = 0.6
    private var speedModeValue = SpeedMode.METER

    private var _thresholdLimit = MutableLiveData<List<Int>>()
    val thresholdLimit: LiveData<List<Int>> = _thresholdLimit

    private var _threshold = MutableLiveData<SpeedModel>()
    val threshold: LiveData<SpeedModel> = _threshold

    private var speedModel = SpeedModel()

    init {
        setThresshold(byDefaultThreshold)
        thresholdLimit()
    }

    //Currently hardcoded will add business logic in future
    fun thresholdLimit() = _thresholdLimit.postValue((1..150).toList())

    fun setThresshold(value: Double) {
        if (speedModeValue == SpeedMode.METER) {
            speedModel.apply {
                speed = value * METER_CONSTANT
                speedModeValue = METER_CONSTANT
                SpeedMode = com.basindevapp.speed_0.SpeedMode.METER.type
            }
        } else {
            speedModel.apply {
                speed = value * KILOMETER_CONSTANT
                speedModeValue = KILOMETER_CONSTANT
                SpeedMode = com.basindevapp.speed_0.SpeedMode.KILOMETER.type
            }
        }
        _threshold.postValue(speedModel)
    }

    fun setSpeedMode() {
        if (speedModeValue == SpeedMode.KILOMETER) {
            speedModel.apply {
                SpeedMode = com.basindevapp.speed_0.SpeedMode.METER.type
                speed = threshold.value?.speed?.times(METER_CONSTANT)
                    ?: (byDefaultThreshold * METER_CONSTANT)
                speedModeValue = METER_CONSTANT
            }
            speedModeValue = SpeedMode.METER
        } else {
            speedModel.apply {
                SpeedMode = com.basindevapp.speed_0.SpeedMode.KILOMETER.type
                speedModeValue = KILOMETER_CONSTANT
                speed = threshold.value?.speed?.div(METER_CONSTANT) ?: byDefaultThreshold
            }

            speedModeValue = SpeedMode.KILOMETER
        }
        _threshold.postValue(speedModel)
    }
}

enum class SpeedMode(val type: String) {
    KILOMETER("km/h"),
    METER("m/h");
}