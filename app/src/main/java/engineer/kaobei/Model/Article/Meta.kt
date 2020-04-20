package engineer.kaobei.Model.Article


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("pagination")
    val pagination: Pagination
)