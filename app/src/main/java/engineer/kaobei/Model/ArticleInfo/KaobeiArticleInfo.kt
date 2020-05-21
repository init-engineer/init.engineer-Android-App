package engineer.kaobei.Model.ArticleInfo


import com.google.gson.annotations.SerializedName

data class KaobeiArticleInfo(
    @SerializedName("data")
    val articleInfo: ArticleInfo = ArticleInfo()
)