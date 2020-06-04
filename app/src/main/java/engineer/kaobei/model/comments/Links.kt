package engineer.kaobei.model.comments

import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("next")
    val next: String = ""
)