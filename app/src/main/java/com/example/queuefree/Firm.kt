package com.example.queuefree

class Firm(
    var nomeazienza: String = "",
    var email:    String = "",
    var password: String = "",
    var categoria: String = "",
    var location: String = "",
    var startHour: Long = 0,
    var startMinute: Long = 0,
    var endHour: Long = 0,
    var endMinute: Long = 0,
    var capienza: Long = 0,
    var descrizione: String = "",
    var maxTurn: Long = 4,
    var maxPartecipants: Long = 10
)