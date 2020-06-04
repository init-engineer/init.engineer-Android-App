package engineer.kaobei.model.themes

import com.google.gson.annotations.SerializedName

data class KaobeiThemes(
    @SerializedName("themes")
    val themes: List<Theme> = listOf()
)