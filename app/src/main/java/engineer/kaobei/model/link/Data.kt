package engineer.kaobei.model.link

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("connections")
    val connections: String = "",
    @SerializedName("like")
    val like: Int = 0,
    @SerializedName("share")
    val share: Int = 0,
    @SerializedName("type")
    val type: String = "",
    @SerializedName("url")
    val url: String = ""
)