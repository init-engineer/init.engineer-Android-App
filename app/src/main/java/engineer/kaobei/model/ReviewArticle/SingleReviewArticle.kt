package engineer.kaobei.model.ReviewArticle


import com.google.gson.annotations.SerializedName

data class SingleReviewArticle(
    @SerializedName("data")
    val `data`: Data = Data()
)