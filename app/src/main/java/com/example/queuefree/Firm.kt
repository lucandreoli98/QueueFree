package com.example.queuefree

class Firm(
    // campi da aggiungere giorni: Lun,Mar,Mer,Gio,Ven,Sab,Dom
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
    var maxTurn: Long = 0,
    var maxPartecipants: Long = 0
)