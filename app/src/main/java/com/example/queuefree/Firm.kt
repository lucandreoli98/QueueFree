package com.example.queuefree

import java.io.Serializable

class Firm(
    var id:String = "",
    var nomeazienza: String = "",
    var email:    String = "",
    var password: String = "", // da cancellare
    var categoria: String = "",
    var location: String = "",
    var startHour: Long = 0,
    var startMinute: Long = 0,
    var endHour: Long = 0,
    var endMinute: Long = 0,
    var capienza: Long = 0,
    var descrizione: String = "",
    var maxTurn: Long = 1,
    var maxPartecipants: Long = 1,
    var giorni: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
): Serializable