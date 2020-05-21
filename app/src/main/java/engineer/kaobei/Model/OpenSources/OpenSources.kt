package engineer.kaobei.Model.OpenSources

import com.google.gson.annotations.SerializedName

data class OpenSources(
    @SerializedName("data")
    val `data`: List<OpenSource> = listOf()
)