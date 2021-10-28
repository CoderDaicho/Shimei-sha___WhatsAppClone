package com.example.android.shimei_sha

import com.google.firebase.firestore.FieldValue
import java.lang.reflect.Field

data class User(
    val name:String,
    val imageUrl:String,
    val thumbImage:String,
    val uid:String,
    val deviceToken:String,
    val status:String,
    val onlineStatus: FieldValue
    ){
    constructor():this("","","","","","", FieldValue.serverTimestamp())
    constructor(name: String,imageUrl: String,thumbImage: String,uid: String):this(
        name,
        imageUrl,
        thumbImage,
        uid,
        "",
        "Hey there I am using Shimei-Sha",
        FieldValue.serverTimestamp()


    )

}

