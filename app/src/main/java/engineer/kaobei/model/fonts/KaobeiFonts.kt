package engineer.kaobei.model.fonts

import com.google.gson.annotations.SerializedName

data class KaobeiFonts(
    @SerializedName("options")
    val options: List<Font> = listOf()
)