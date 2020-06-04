package engineer.kaobei.model.comments

import com.google.gson.annotations.SerializedName

data class Media(
    @SerializedName("connections")
    val connections: String = "",
    @SerializedName("type")
    val type: String = ""
)