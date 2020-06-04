package engineer.kaobei.model.kaobeluser

import com.google.gson.annotations.SerializedName

data class BeanKaobeiUser(
    @SerializedName("data")
    val `data`: KaobeiUser = KaobeiUser()
)