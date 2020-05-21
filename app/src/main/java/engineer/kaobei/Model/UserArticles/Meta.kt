package engineer.kaobei.Model.UserArticles

import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("pagination")
    val pagination: Pagination = Pagination()
)