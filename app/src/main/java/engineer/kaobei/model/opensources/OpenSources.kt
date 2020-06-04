package engineer.kaobei.model.opensources

import com.google.gson.annotations.SerializedName

data class OpenSources(
    @SerializedName("data")
    val `data`: List<OpenSource> = listOf()
)