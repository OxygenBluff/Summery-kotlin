package com.example.summery.network

import android.content.Context
import com.example.summery.data.ApiService
import com.example.summery.data.remote.interceptors.AuthInterceptor
import com.example.summery.data.remote.interceptors.TokenAuthenticator
import com.example.summery.local.EncryptedTokenManager
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File


//TODO NOT RECOMMENDED TO APP NAVHOST HERE
//THIS retrofit = initialized once and lives forever navhost different so..
//shared flow ?



//objcet not the java one!!
//kotlin objcet = SINGLETON! a class that can only have ONE SINGLE instance EVER!
// + ALSO no need to intialize it ever WOW
//like a static method in ajav just call it deirectly
object RetrofitInstance {
    public const val BASE_URL ="http://172.16.13.68:8082"   //10.0.2.2 is how android knows the localhost huh..172.16.13.211

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
            .authenticator (TokenAuthenticator(tokenManager))



        //cache
        appContext?.let { context ->
            val cacheSize = 10L * 1024L * 1024L // 10MB
            val cache = Cache(
                directory = File(context.cacheDir, "http_cache"),
                maxSize = cacheSize
            )
            builder.cache(cache)
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
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)//actually writes all the HTTP code automatically..
        //java's type erasure strikes again! oh retrofit is java...
        //.create creates a real implementations from the retrofit interface!
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

    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,

    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,

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

    //missing
    @SerializedName("dateCreation")
    val CreationDate: String?, //JSON GIVE STRINGGG


    val variants: List<VariantDTO>, //TODO create DTO.. 0.0

    val customizations: List<CustomizationDTO>, //TODO

    val reviews: List<ReviewDTO>, //TODO

    @SerializedName("noteMoyenne")
    val averageRating: Float?,

    //seller + description ??
    val description: String,
    val sellerId: Long?, // TODO , yikes a seller DTO .. 0.0

    val categories:List<CategoryDTO>,

    //new
    val lowestPrice:Double

)

@Serializable
data class CategoryDTO(
    val id: Long,
    @SerializedName("nom")
    val name: String,

    val description: String,
    //sub categories even tho won't be used i THINK
    @SerializedName("sousCategories")
    val subCategories: List<CategoryDTO> = emptyList()
)

@Serializable
data class VariantDTO(
    val id: Long,
    @SerializedName("attribut")
    val attribute: String,

    @SerializedName("valeur")
    val value: String,

    @SerializedName("prixDelta")
    val priceDelta: Double ?, //can be the same !! so null cosnsistent not 0

    @SerializedName("stockSupplementaire")
    val Stock: Int?,

    //hmm
    val mandatory: Boolean = false,

    //image
    val imageUrl: String

)

@Serializable
data class CustomizationDTO(
    val id: Long,
    val name: String,
    val extraPrice: Double?,
    val available: Boolean
)

@Serializable
data class ReviewDTO(
    val id: Long,
    @SerializedName("note")
    val rating: Int?,

    @SerializedName("commentaire")
    val comment: String?,

    val customerName: String?,
    val dateCreation: String?,
    val approuve: Boolean
)

//cart stuff
data class CartDTO(
    val id: Long,
    val items: List<CartItemDTO>,
    val totalCartPrice: Double,
    val appliedCouponCode: String?,
    val discountAmount: Double,
    val finalPrice: Double
)

data class CartItemDTO(
    val id: Long,
    val productName: String,
    val variantNames: List<String> = emptyList(),
    val unitPrice: Double,
    val quantity: Int,
    val subTotal: Double,
    val imageUrl: String?,
    val customizations: List<CustomizationDTO>,
    // 💀 No i'm actually done
    val branchId: Long,
)

// ://
data class AddToCartRequestDTO(
    val productId: Long,
    val variantIds: List<Long>,
    val quantity: Int,
    val customizationIds: List<Long>
)

//post review FINALLY
data class ReviewRequestDTO(
    @SerializedName("note")
    val rating: Int,
    @SerializedName("commentaire")
    val comment: String?,
    val productId: Long
)

data class ReviewResponseDTO(
    @SerializedName("id")
    val ProductId: Long,
    @SerializedName("note")
    val rating: Int,
    @SerializedName("commentaire")
    val comment: String?,
    val customerName: String,
    @SerializedName("dateCreation")
    val creationDate: String,
    @SerializedName("approuve")
    val approved: Boolean =false
)

//change cart item qty
data class CartChangeQuantityRequest(
    val quantity: Int
)

//coupon
data class CouponRequestDTO(
    val code:String
)


//Addresses
data class AddressResponseDTO(
    //huh no DTO in backend nice or not nice!
    val id: Long?,
    @SerializedName("rue")
    val street: String,
    @SerializedName("ville")
    val city: String,
    @SerializedName("codePostal")
    val postalCode: String,
    @SerializedName("pays")
    val country: String,
    val principal: Boolean = false
)

//we go again! part 2
data class BranchResponseDTO(
    val id: Long,

    val name: String,
    val description: String?,
    val logo: String?,

    val address: String?,
    val city: String?,

    val latitude: Double?,
    val longitude: Double?,

    val phone: String?,
    val email: String?,

    val openingTime: String?,
    val closingTime: String?,

    val active: Boolean,
    val rating: Double?,

    //staff list ? nahh
    //products list ? hmm but won't fetch it at the start tho..
    //added it JUST IN CASE i use it

    val products: List<ProductResponseDTO>? = null

)

//ORDER ITEM













