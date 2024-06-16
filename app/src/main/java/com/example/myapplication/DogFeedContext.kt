package com.example.myapplication

import java.time.LocalDateTime

//klasa przechowująca dane o karmieniu psa (zbiór na dane, używane w DogFeedMethodActivity)
class DogFeedContext(
    var gramsPerDay: Int = 0,
    var feedingFrequency: Int = 0,
    var feedingHours: List<LocalDateTime>)