package engineer.kaobei

import engineer.kaobei.model.ReviewArticle.SingleReviewArticle
import engineer.kaobei.model.ReviewArticles.ReviewArticles
import engineer.kaobei.model.articleInfo.KaobeiArticleInfo
import engineer.kaobei.model.articles.KaobeiArticleList
import engineer.kaobei.model.comments.KaobeiComments
import engineer.kaobei.model.kaobeluser.BeanKaobeiUser
import engineer.kaobei.model.link.KaobeiLink
import engineer.kaobei.model.userarticles.UserArticles
import okhttp3.MultipartBody
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
        @Part content: MultipartBody.Part,
        @Part themeStyle: MultipartBody.Part,
        @Part fontStyle: MultipartBody.Part,
        @Part avatar: MultipartBody.Part
    ): Call<ResponseBody>

    @Headers("Accept: application/json")
    @Multipart
    @POST("api/frontend/social/cards/api/publish")
    fun publishArticleNoImg(
        @Header("Authorization") accessToken: String,
        @Part content: MultipartBody.Part,
        @Part themeStyle: MultipartBody.Part,
        @Part fontStyle: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("api/frontend/social/cards/api/review")
    fun reviewArticleList(
        @Header("Authorization") accessToken: String,
        @Query("page") page: String? = null
    ): Call<ReviewArticles>

    @GET("api/frontend/social/cards/api/review/{id}/succeeded")
    fun approveArticle(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String
    ): Call<SingleReviewArticle>

    @GET("api/frontend/social/cards/api/review/{id}/failed")
    fun denyArticle(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String
    ): Call<SingleReviewArticle>

}