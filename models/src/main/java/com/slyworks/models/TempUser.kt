package com.slyworks.models

data class TempUser(
    var firstName:String,
    var lastName:String,
    var password:String,
    var imageUri:String,
    var IDToken:String,
    var idTokenType: IDTokenType
)