package engineer.kaobei.Model.KaobelUser

import com.google.gson.annotations.SerializedName

data class KaobeiUser(
    @SerializedName("active")
    val active: Boolean = false,
    @SerializedName("avatar")
    val avatar: String = "",
    @SerializedName("confirmed")
    val confirmed: Boolean = false,
    @SerializedName("email")
    val email: String = "",
    @SerializedName("first_name")
    val firstName: String = "",
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("last_name")
    val lastName: String = "",
    @SerializedName("timezone")
    val timezone: String = "",
    @SerializedName("uuid")
    val uuid: String = ""
)