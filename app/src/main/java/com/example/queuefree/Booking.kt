package com.example.queuefree

import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate

class Booking(
    var idUser:   String = "",
    var nPartecipanti: Int = 0,
    var dd:       Long   = 0L,
    var mm:       Long   = 0L,
    var yy:       Long   = 0L,
    var startHour: Int = 0,
    var startMinute: Int = 0,
    var endHour: Int = 0,
    var endMinute: Int = 0
)