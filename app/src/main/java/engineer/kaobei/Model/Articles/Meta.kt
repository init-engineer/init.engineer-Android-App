package engineer.kaobei.Model.Articles


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("pagination")
    val pagination: Pagination = Pagination()
)