package engineer.kaobei.model.fonts

import com.google.gson.annotations.SerializedName

data class Font(
    @SerializedName("font")
    val font: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("value")
    val value: String = ""
)