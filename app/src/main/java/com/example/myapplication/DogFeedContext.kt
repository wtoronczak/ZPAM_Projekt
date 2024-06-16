package com.example.myapplication

import java.time.LocalDateTime


class DogFeedContext(
    var gramsPerDay: Int = 0,
    var feedingFrequency: Int = 0,
    var feedingHours: List<LocalDateTime>)