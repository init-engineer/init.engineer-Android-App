package engineer.kaobei.Model.Articles

import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("next")
    val next: String = ""
)