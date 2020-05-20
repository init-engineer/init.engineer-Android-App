package engineer.kaobei.Model.ArticleInfo


import com.google.gson.annotations.SerializedName

data class ArticleInfo(
    @SerializedName("content")
    val content: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("created_diff")
    val createdDiff: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("image")
    val image: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("updated_diff")
    val updatedDiff: String = ""
)