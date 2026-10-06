package com.example.summery.data.remote.interceptors

import AuthResponseDTO
import com.example.summery.local.EncryptedTokenManager
import com.example.summery.network.RetrofitInstance
import com.google.gson.Gson
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Route
import java.io.IOException

//inerceptor.. tokens
class AuthInterceptor(private  val tokenManager: EncryptedTokenManager): Interceptor {
    @Throws(IOException::class)
    //java needs this for network calls or try catch..
    override  fun intercept (chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()

        //pff comparing bug?
        val path = originalRequest.url.encodedPath

        //1- no token -> signup or login only OOR just let them no injcetions
        if(path.contains("/auth/") || path.contains("refresh")){
            return chain.proceed(originalRequest)
        }

        val token = tokenManager.getAccessToken()
        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        //else inject bearer
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization","Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}

//refreshing tokens is seperate... OkHtpp Ok
//ONLY works if it gets a 401 Unauth oh..
class TokenAuthenticator(
    private val tokenManager: EncryptedTokenManager
) : Authenticator { // IMPLEMENTS OkHttp's Authenticator INTERFACE

    override fun authenticate(route: Route?, response: okhttp3.Response): Request? {
        //route ? -> provied by OkHttps (Ip/proxy info)
        //response = the failed 401 response objcet
        //: Request return = tells OkHtpp to RETRY the original request with the new headers
        //BUT returning null -> tells it to accept the failure -> passes 401 back again

        println("DEBUG_REFRESH: === Authenticator Triggered ===")

        // we're having a loop..
        //so if it already tried rfresshing -> STOP
        //ORR endsWith (better than contains!!) already fired then failed again with 401
        //also stop don't keep looping
        //null
        if (response.request.header("Authorization-Refreshed") != null ||
            response.request.url.encodedPath.endsWith("refresh-token")
        ) {
            println("DEBUG_REFRESH: Abandoning refresh (Already retried or was refresh call)")
            return null
        }

        //ahhh concurrency lock!!
        //we have uhh 4 requests just on the home screen..
        //all of them will get 401 if token expired BUTTT
        // ONLY ONE THREAD at a time enters this bloc to refresh the token!!
        synchronized(this) {
            val currentAccessToken = tokenManager.getAccessToken()
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            //so let's say thread one entered and refresh the access token
            //WILL NOT BE THE SAME one that's stored(new refreshred) and thread 2's own request token
            //thread 2 realizes IT WAS ALREADY REFRESHED!  currentAccessToken != requestToken
            //JUST use the newly stored one :D

            if (currentAccessToken != null && currentAccessToken != requestToken) {
                println("DEBUG_REFRESH: Token was already refreshed by another request thread!")

                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .header("Authorization-Refreshed", "true")
                    .build()
            }

            val refreshToken = tokenManager.getRefreshToken()
            println("DEBUG_REFRESH: Saved Refresh Token = '$refreshToken'")

            //JUST IN CASE.. don't tryna refresh an empty token..
            if (refreshToken.isNullOrBlank()) {
                println("DEBUG_REFRESH: Refresh token is NULL or BLANK in TokenManager!")
                tokenManager.clearTokens()
                return null
            }


            // new clean OkHtpp client NO interceptor in it nothing no token authenticator either
            val rawClient = OkHttpClient()
            val refreshUrl = "${RetrofitInstance.BASE_URL}/api/auth/refresh-token"

            //new refresh request, Builder = POST request, also empty
            val refreshRequest = Request.Builder()
                .url(refreshUrl)
                .post("".toRequestBody(null))
                .header("Authorization", "Bearer $refreshToken")
                .build()

            //the spring response ->
            return try {
                val refreshResponse = rawClient.newCall(refreshRequest).execute()
                //execute = SYNCHRONOUS
                val responseCode = refreshResponse.code
                val rawJson = refreshResponse.body?.string()
                //body?.string, we know this by now reads the raw response ONCE ONLY otherwise gets consumed

                println("DEBUG_REFRESH: Refresh HTTP Code = $responseCode")
                println("DEBUG_REFRESH: Raw JSON = $rawJson")

                if (refreshResponse.isSuccessful && !rawJson.isNullOrBlank()) {
                    //fun fact, isSuccessful -> 200 to 299 code range

                    val newTokens = Gson().fromJson(rawJson, AuthResponseDTO::class.java)
                    //low level extraction and manual deserialization into AuthResponseDTO ours

                    if (!newTokens?.accessToken.isNullOrBlank()) {
                        println("DEBUG_REFRESH: SUCCESS! New Access Token saved.")
                        tokenManager.saveTokens(
                            accessToken = newTokens.accessToken,
                            refreshToken = newTokens.refreshToken ?: refreshToken
                        )
                        //save new

                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${newTokens.accessToken}")
                            .header("Authorization-Refreshed", "true")
                            .build()
                        //okay.. the ORIGINAL request that triggered the 401
                        // copy it swap Authorization to Auth-Refreshed
                        //RETURN IT -> OkHttp executes it AGAIN

                    } else {
                        println("DEBUG_REFRESH: Parsed accessToken was null/empty!")
                        tokenManager.clearTokens()
                        null
                    }
                } else {
                    println("DEBUG_REFRESH: Server rejected refresh token (HTTP $responseCode)")
                    tokenManager.clearTokens()
                    null
                    //AHA WHY REJECTED ?
                    //REFRESH TOKEN (not access) itself must be expired THIS IS WHEN YOU REDIRECT TO LOGINN
                    //BUT redirecting here is bad
                    //-> Emit a shared flowed from THE token manager can u beleive it when u clear the tokens
                    //then collect it inside the top level comppsable main acitivty for me
                    //and boom LaunchedEffect and redirect
                }
            } catch (e: Exception) {
                println("DEBUG_REFRESH: Exception during refresh call -> ${e.message}")
                tokenManager.clearTokens()
                null
            }
        }
    }
}