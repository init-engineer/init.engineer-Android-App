package engineer.kaobei.Model.Themes


import com.google.gson.annotations.SerializedName

data class Additional(
    @SerializedName("background_image")
    val backgroundImage: String = "",
    @SerializedName("footer")
    val footer: String = "",
    @SerializedName("header")
    val header: String = ""
)