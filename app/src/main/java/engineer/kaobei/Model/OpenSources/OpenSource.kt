package engineer.kaobei.Model.OpenSources

import com.google.gson.annotations.SerializedName

data class OpenSource(
    @SerializedName("author")
    val author: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("url")
    val url: String = ""
)