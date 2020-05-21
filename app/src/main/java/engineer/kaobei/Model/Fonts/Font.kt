package engineer.kaobei.Model.Fonts

import com.google.gson.annotations.SerializedName

data class Font(
    @SerializedName("font")
    val font: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("value")
    val value: String = ""
)