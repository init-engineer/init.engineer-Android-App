package engineer.kaobei.model.ReviewArticles


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("pagination")
    val pagination: Pagination = Pagination()
)