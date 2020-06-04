package engineer.kaobei

import engineer.kaobei.model.articleInfo.KaobeiArticleInfo
import engineer.kaobei.model.articles.KaobeiArticleList
import engineer.kaobei.model.comments.KaobeiComments
import engineer.kaobei.model.kaobeluser.BeanKaobeiUser
import engineer.kaobei.model.link.KaobeiLink
import engineer.kaobei.model.userarticles.UserArticles
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

const val BASE_URL =  "https://kaobei.engineer"

interface KaobeiEngineerService {

    @GET("api/frontend/user/profile")
    fun profile(
        @Header("Authorization") accessToken: String
    ): Call<BeanKaobeiUser>

    @GET("api/frontend/social/cards/api/dashboard")
    fun userArticleList(
        @Header("Authorization") accessToken: String,
        @Query("page") page: String? = null
    ): Call<UserArticles>

    @GET("api/frontend/social/cards")
    fun articleList(
        @Query("page") page: String? = null
    ): Call<KaobeiArticleList>

    @GET("api/frontend/social/cards/{id}/show")
    fun show(
        @Path("id") id: String
    ): Call<KaobeiArticleInfo>

    @GET("api/frontend/social/cards/{id}/links")
    fun links(
        @Path("id") id: String
    ): Call<KaobeiLink>

    @GET("api/frontend/social/cards/{id}/comments")
    fun comments(
        @Path("id") id: String,
        @Query("page") page: String? = null
    ): Call<KaobeiComments>

    @Headers("Accept: application/json")
    @Multipart
    @POST("api/frontend/social/cards/api/publish")
    fun publishArticle(
        @Header("Authorization") accessToken: String,
        @Part requestBody: RequestBody
    ): Call<ResponseBody>

}