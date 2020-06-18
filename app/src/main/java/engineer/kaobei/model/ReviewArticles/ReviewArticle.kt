package engineer.kaobei.model.ReviewArticles


import com.google.gson.annotations.SerializedName

data class ReviewArticle(
    @SerializedName("content")
    val content: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("created_diff")
    val createdDiff: String = "",
    @SerializedName("failed")
    val failed: Int = 0,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("image")
    val image: String = "",
    @SerializedName("review")
    val review: Int = 0,
    @SerializedName("succeeded")
    val succeeded: Int = 0,
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("updated_diff")
    val updatedDiff: String = ""
)