package com.basindevapp.speed_0.model

//speedMode Kilometer = 1, Meter = 1000

data class SpeedModel(
    var speed: Double = 50.0,
    var speedModeValue: Double = 1.0,
    var SpeedMode: String = com.basindevapp.speed_0.SpeedMode.KILOMETER.type
)
