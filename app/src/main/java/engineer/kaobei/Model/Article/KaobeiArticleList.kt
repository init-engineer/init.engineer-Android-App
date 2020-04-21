package engineer.kaobei.Model.Article


import com.google.gson.annotations.SerializedName

data class KaobeiArticleList(
    @SerializedName("data")
    val `data`: List<Article>,
    @SerializedName("meta")
    val meta: Meta
)