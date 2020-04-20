package engineer.kaobei.Model.Article


import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("next")
    val next: String
)