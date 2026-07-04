package com.example.summery.network

import android.content.Context
import android.util.Log
import com.example.summery.data.ApiService
import com.example.summery.local.EncryptedTokenManager
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException

//objcet not the java one!!
//kotlin objcet = SINGLETON! a class that can only have ONE SINGLE instance EVER!
// + ALSO no need to intialize it ever WOW
//like a static method in ajav just call it deirectly
object RetrofitInstance {
    private const val BASE_URL ="http://192.168.1.101:8082"   //10.0.2.2 is how android knows the localhost huh..
    // http://192.168.1.115:8082/"
    //"https://summery2.onrender.com/" THIS is the render one btw

    //no more "https://summery-api-1.onrender.com/"

    //ApiService , it's using it.. api will be of type ApiService

    lateinit var tokenManager: EncryptedTokenManager
    //wth.. TODO

    //WILL HOLD THE CONTEXT for the cache
    private var appContext: Context? = null

    fun initCache(context: Context){
        this.appContext = context.applicationContext
    }

    private val okHttpClient by lazy {
        //needs context to find the CACHE folder! built in bruhh
        //gets from this variable i guess
        val builder = OkHttpClient.Builder()
            .addInterceptor (AuthInterceptor(tokenManager))

        // ** IF initCache  -> add some E-tag cache size 10MB ?
        //TODO let -> executes a bloc of code on an OBJCET -> referenced as it
        //TODO -> returns the result of the LAST EXPRESSION in the bloc
        //TODO let? -> run only if the OBJCET IS NOT NULL WOWO WRITE IT DOWNNNN ***

        Log.d("CACHE_DEBUG", "okHttpClient lazy block is FIRING! Checking appContext...")

        appContext?.let {context ->
            Log.d("CACHE_DEBUG", "🎉 SUCCESS: appContext is NOT null! Building cache...")
            val cacheSize = 10*1024*1024L
            val cacheDirectory = File(context.cacheDir, "http_cache")
            builder.cache(Cache(cacheDirectory,cacheSize))
        } ?: run {
            Log.e("CACHE_DEBUG", "❌ CRITICAL: appContext is NULL! Cache block was completely SKIPPED!")
        }
        builder.build()
    }

    val api : ApiService by lazy { //lazy = don't create this until someone needs it!
        //OHH retrofit never runs till first API call
        //by lazy = ONLY runs once: when API first accessed never again!
        //CUSTOM timeout -> make client pass it in client in builder
        //val client = OkHttpClient.Builder()
         //   .connectTimeout(10, TimeUnit.SECONDS)
         //   .readTimeout(10, TimeUnit.SECONDS)
          //  .writeTimeout(10, TimeUnit.SECONDS)
           // .build()
        //any scenario with different ones ?
        //1-connect -> for establishing the connection
        //2-read -> waiting for BIG DATA !! response !! read huh
        //3-write = UPLOADING so this might be the longest in practice!

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)//actually writes all the HTTP code automatically..
        //java's type erasure strikes again! oh retrofit is java...
        //.create creates a real implementations from the retrofit interface!
    }

    //RE USABLE! yes
    fun parseErrorMessage(response: Response<*>): String {
        // <*> = i don't care about the type
        //WHY ?
        //-> Error are UNPREDICTABLE !! sometimes nothing, sometones json soemtimes text!

        val errorJsonString = response.errorBody()?.string()
        //errorBody returns a ResponseBody objcet
        //like a pipe flowing not text !!
        //? = safe call operator, null it nothing ISNTEAD OF CRASHING THE APP
        // !! IMPORTANT !!

        //then why .string ? -> takes the flow and in a text sealed finally !
        //I/O operation = slow over network :)

        if(!errorJsonString.isNullOrBlank()){
            //wth is blank ? -> ee or a bunch of white spaces ! "   "
            //wow.. so much stuff
            try{
                val gson = Gson()

                //errorJsonString is json -> need to become an objcet!
                //gson.fromJson is the translator !
                //give it the jsonError
                //BUT also need to point it towards the data class blueprint O: =>  ApiErrorDTO::class.java
                val errorResponse = gson.fromJson(errorJsonString,ApiErrorDTO::class.java)
                return errorResponse.errorMessage
                //NOW fianlly can use the objcet dot notation !!
                //and return it :)

            }catch(e: Exception){
                return "Unexpected Error: ${response.code()}"
            }

        }
        return "Network Error: ${response.code()}"
        //what is the errorJsonString was null / empty ! no if !
    }


}

//inerceptor.. tokens
class AuthInterceptor(private  val tokenManager: EncryptedTokenManager): Interceptor {
    @Throws(IOException::class)
    //java needs this for network calls or try catch..
    override  fun intercept (chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()

        val token = tokenManager.getAccessToken()

        //1- no token -> signup or login only
        if(token.isNullOrBlank()){
            return chain.proceed(originalRequest)
        }
        //else injcet bearer
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization","Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}


//FINALLY  a U N I F I E D api safe call method: injects tokens AND uses the parseErorr method!

suspend fun <T> ApiCall(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val response= apiCall()

        if(response.isSuccessful && response.body()!=null) {
            Result.success(response.body()!!)
        }else{
            val errorMessage = RetrofitInstance.parseErrorMessage(response)
            Result.failure(Exception(errorMessage))
        }

    } catch(e:Exception){
        val exceptionSafeMessage =
            //hmm ? TODO maybe just filter java's backend ones that reveal IP
            "Seems like we can't reach the server at this moment.."
        Result.failure(Exception(exceptionSafeMessage))
    }

}

//Error ones DO NOT get returned by RetroFit wait 0.0
//does NOT parse errorBody() in erorr cases wow
//inside the UI -> to ApiErrorDTO ?
data class ApiErrorDTO(
    val errorStatus: Int,
    val errorMessage: String
)

//Page to kotlin

data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val number: Int // Current page index !!
)

//TODO switch these 2 calsses to their approariate files..
@Serializable
data class ProductResponseDTO(
    val id: Long,

    @SerializedName("nom")
    val name: String, //TODO that annoation.. basically the backend original name mapped
    //to this one, otherwise will be null here

    @SerializedName("prix")
    val price: Double,

    @SerializedName("prixPromo")
    val DiscountedPrice: Double?, //nullable oh kotlin ur amazing

    val stock: Int,
    val actif: Boolean,

    val images: List<String>, // URLs lmao i forgot strings

    //seller + description ??
    val description: String,
    val seller: String, // TODO , yikes a seller DTO .. 0.0

    val categories:List<CategoryDTO>

)

@Serializable
data class CategoryDTO(
    val id: Long,
    @SerializedName("nom")
    val name: String,

    val description: String
)





