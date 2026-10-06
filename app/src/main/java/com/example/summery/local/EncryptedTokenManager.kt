package com.example.summery.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

//dataStore + encryped sahred preferences actually
class EncryptedTokenManager(context: Context){
    //this contenxt thingy man...

    //1-HARDWARE BACKED (!!) master key for the device
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    //2-Encrypted storage file
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context, "secure_token_storage",masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM

        //SIV= synthetic initlization vector
        //variable names are encrypted :)

        //GCM = Galois/Counter Mode
        //-> turns the TOKEN now into gibberish
        //AH this thing destorys the values if it detects any manipulation ??
        //O:
    )

    //saving tokens here (AuthResposneDTO)
    fun saveTokens(accessToken:String, refreshToken:String){
        sharedPreferences.edit().apply{
            putString("access_token",accessToken)
            putString("refresh_token",refreshToken)
            apply()
        }
    }

    //NOW any request (except auth and signup) needs headers -> tokens fetching
    fun getAccessToken(): String? = sharedPreferences.getString("access_token",null)

    fun getRefreshToken(): String? = sharedPreferences.getString("refresh_token",null)


    private val _loggeddOutEvent = MutableSharedFlow<Unit>(
        extraBufferCapacity=1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val loggedOutEvent = _loggeddOutEvent.asSharedFlow()


    //log out ! = clear all tokens
    //go back to login, how to signal this ? EMIT A SHARED FLOW
    fun clearTokens(){
        sharedPreferences.edit().clear().apply()

        _loggeddOutEvent.tryEmit(Unit)
    }
}