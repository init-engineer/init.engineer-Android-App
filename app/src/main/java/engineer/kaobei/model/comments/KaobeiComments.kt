package engineer.kaobei.model.comments

import com.google.gson.annotations.SerializedName

data class KaobeiComments(
    @SerializedName("data")
    val `data`: List<Comment> = listOf(),
    @SerializedName("meta")
    val meta: Meta = Meta()
)