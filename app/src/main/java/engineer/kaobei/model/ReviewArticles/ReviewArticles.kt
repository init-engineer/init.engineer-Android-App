package engineer.kaobei.model.ReviewArticles


import com.google.gson.annotations.SerializedName

data class ReviewArticles(
    @SerializedName("data")
    val `data`: List<ReviewArticle> = listOf(),
    @SerializedName("meta")
    val meta: Meta = Meta()
)