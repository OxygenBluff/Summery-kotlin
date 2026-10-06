package com.example.summery.data.remote.api

import com.example.summery.network.ApiErrorDTO
import com.google.gson.Gson
import retrofit2.Response

//FINALLY  a U N I F I E D api safe call method: injects tokens AND uses the parseErorr method!

suspend fun <T> ApiCall(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val response= apiCall()

        if(response.isSuccessful && response.body()!=null) {
            Result.success(response.body()!!)
        }else{
            val errorMessage = parseErrorMessage(response)
            Result.failure(Exception(errorMessage))
        }

    } catch(e:Exception){
        val exceptionSafeMessage =
            //hmm ? TODO maybe just filter java's backend ones that reveal IP
            "Seems like we can't reach the server at this moment.."
        //Result.failure(Exception(exceptionSafeMessage))
        Result.failure(Exception(e.message))


        //TODO what is response body is void (DELETE endpints ?)
        //-> Response<Unit> in kotlin edge case ?
        // successful will be true but data -> null
    }

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