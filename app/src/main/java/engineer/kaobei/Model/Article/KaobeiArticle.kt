package engineer.kaobei.Model.Article


import com.google.gson.annotations.SerializedName

data class KaobeiArticle(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("meta")
    val meta: Meta
)