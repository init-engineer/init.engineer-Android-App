package engineer.kaobei.Model.Article


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("content")
    val content: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("created_diff")
    val createdDiff: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("updated_diff")
    val updatedDiff: String
)