package com.basindevapp.speed_0.model

//speedMode Kilometer = 1, Meter = 1000

data class SpeedModel(
    var speed: Int = 100,
    var speedModeValue: Int = 1,
    var SpeedMode: String = com.basindevapp.speed_0.SpeedMode.KILOMETER.type
)
