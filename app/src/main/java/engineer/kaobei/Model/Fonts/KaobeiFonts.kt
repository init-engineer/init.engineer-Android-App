package engineer.kaobei.Model.Fonts


import com.google.gson.annotations.SerializedName

data class KaobeiFonts(
    @SerializedName("options")
    val options: List<Font> = listOf()
)