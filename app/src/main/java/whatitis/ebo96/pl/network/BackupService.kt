package whatitis.ebo96.pl.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT

interface BackupService {

    @Multipart
    @PUT("upload")
    fun uploadFile(): Single<Boolean>

    @GET("hello")
    fun test(): Single<Response>
}