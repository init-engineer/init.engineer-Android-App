package engineer.kaobei.Model.Comments

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("avatar")
    val avatar: String = "",
    @SerializedName("content")
    val content: String = "",
    @SerializedName("created")
    val created: String = "",
    @SerializedName("media")
    val media: Media = Media(),
    @SerializedName("name")
    val name: String = ""
)