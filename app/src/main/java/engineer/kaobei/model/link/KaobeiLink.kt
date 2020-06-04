package engineer.kaobei.model.link

import com.google.gson.annotations.SerializedName

data class KaobeiLink(
    @SerializedName("data")
    val `data`: List<Data> = listOf(Data(), Data(), Data(), Data())
)