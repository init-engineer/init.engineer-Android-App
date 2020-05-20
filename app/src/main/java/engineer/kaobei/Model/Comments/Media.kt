package engineer.kaobei.Model.Comments

import com.google.gson.annotations.SerializedName

data class Media(
    @SerializedName("connections")
    val connections: String = "",
    @SerializedName("type")
    val type: String = ""
)