package com.smsyard
data class Message(
    val id:Long,
    val sender:String,
    val body:String,
    val date:Long,
    var paid:Boolean=false)
