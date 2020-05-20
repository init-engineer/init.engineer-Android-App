package engineer.kaobei.Model.UserArticles

import com.google.gson.annotations.SerializedName

data class UserArticle(
    @SerializedName("banned_remarks")
    val bannedRemarks: String = "",
    @SerializedName("content")
    val content: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("created_diff")
    val createdDiff: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("image")
    val image: String = "",
    @SerializedName("is_banned")
    val isBanned: Int = 0,
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("updated_diff")
    val updatedDiff: String = ""
)