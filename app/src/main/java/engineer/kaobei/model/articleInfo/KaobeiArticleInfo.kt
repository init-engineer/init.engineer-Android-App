package engineer.kaobei.model.articleInfo


import com.google.gson.annotations.SerializedName

data class KaobeiArticleInfo(
    @SerializedName("data")
    val articleInfo: ArticleInfo = ArticleInfo()
)