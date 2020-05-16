package engineer.kaobei.Model.Articles


import com.google.gson.annotations.SerializedName

data class KaobeiArticleList(
    @SerializedName("data")
    val `data`: List<Article>  = listOf(),
    @SerializedName("meta")
    val meta: Meta = Meta()
)