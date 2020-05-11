package engineer.kaobei.Model.Comments


import com.google.gson.annotations.SerializedName

data class KaobeiComments(
    @SerializedName("data")
    val `data`: List<Comment> = listOf(),
    @SerializedName("meta")
    val meta: Meta = Meta()
)