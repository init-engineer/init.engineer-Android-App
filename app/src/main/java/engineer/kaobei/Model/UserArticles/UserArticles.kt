package engineer.kaobei.Model.UserArticles


import com.google.gson.annotations.SerializedName

data class UserArticles(
    @SerializedName("data")
    val `data`: List<UserArticle> = listOf(),
    @SerializedName("meta")
    val meta: Meta = Meta()
)