package co.uk.thejvm.thing.rxtwitter.data

import android.graphics.Bitmap

data class TweetViewModel (
    val content: String,
    var avatarImage: Bitmap,
    val dateLabel: String = "",
    val userName: String = ""
)
