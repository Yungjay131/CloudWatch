package com.slyworks.models

data class User(
    var firstName:String,
    var lastName:String,
    var password:String,
    var imageUri:String,
    var IDToken:String,
    var idTokenType: IDTokenType
)