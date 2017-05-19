package co.uk.thejvm.thing.rxtwitter.data

data class Tweet(
        val content: String,
        val dateLabel: String = "",
        val imageUri: String = "",
        val userName: String = ""
)
