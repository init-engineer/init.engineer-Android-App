package engineer.kaobei.Model.Themes

import com.google.gson.annotations.SerializedName

data class KaobeiThemes(
    @SerializedName("themes")
    val themes: List<Theme> = listOf()
)