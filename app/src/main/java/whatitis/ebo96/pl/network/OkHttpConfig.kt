package whatitis.ebo96.pl.network

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient

val backupClient = OkHttpClient.Builder().addInterceptor { chain ->
    val request = chain.request()

    chain.proceed(request)
}.build()

class Response(){

    @SerializedName("test")
    lateinit var response: String
}