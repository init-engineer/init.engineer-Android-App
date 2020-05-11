package engineer.kaobei.Model.Comments


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("pagination")
    val pagination: Pagination = Pagination()
)