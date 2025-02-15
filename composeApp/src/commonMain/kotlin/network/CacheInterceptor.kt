package network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class CacheInterceptor : Interceptor{

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        if (!response.isSuccessful || response.cacheControl().noCache()
            || response.cacheControl().mustRevalidate() || response.cacheControl().noStore()
        ) return response
        return response.newBuilder()
            .header("Cache-Control", "max-age=3600")
            .build()
    }
}