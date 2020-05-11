package engineer.kaobei.Model.Themes


import com.google.gson.annotations.SerializedName

data class Theme(
    @SerializedName("additional")
    val additional: Additional = Additional(),
    @SerializedName("background_color")
    val backgroundColor: String = "#121212",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("text_color")
    val textColor: String = "#FAFAFA",
    @SerializedName("value")
    val value: String = ""
)