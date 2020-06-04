package engineer.kaobei.model.articles

import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("next")
    val next: String = ""
)