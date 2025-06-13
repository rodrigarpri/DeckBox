package com.example.deckbox.data.api

import com.example.deckbox.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiClient {
    private const val MTG_BASE_URL = "https://rodrigogp.alumnosatlantida.es/API_MTG/controllers/"
    private const val SCRYFALL_BASE_URL = "https://api.scryfall.com/"

    // Para poder entrar en nuestro hosting inseguro xd
    private val unsafeMtgClient = createUnsafeOkHttpClient()

    private val safeScryfallClient = OkHttpClient.Builder().build()

    val mtgApi: MtgApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MTG_BASE_URL)
            .client(unsafeMtgClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MtgApiService::class.java)
    }

    val scryfallApi: ScryfallApiService by lazy {
        Retrofit.Builder()
            .baseUrl(SCRYFALL_BASE_URL)
            .client(safeScryfallClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ScryfallApiService::class.java)
    }

    private fun createUnsafeOkHttpClient(): OkHttpClient {
        try {
            // Confiamos en nuestro hosting
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
            })

            val sslContext = SSLContext.getInstance("SSL").apply {
                init(null, trustAllCerts, SecureRandom())
            }

            return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .addInterceptor { chain ->

                    val newRequest = chain.request().newBuilder()
                        .addHeader("X-API-Key", BuildConfig.API_KEY)
                        .build()
                    chain.proceed(newRequest)
                }
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}