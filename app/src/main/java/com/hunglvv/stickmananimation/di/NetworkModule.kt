package com.hunglvv.stickmananimation.di

import android.content.Context
import com.hunglvv.stickmananimation.library.utils.network.ConnectivityManagerNetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /*
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        client.addInterceptor(logging)

        return Retrofit.Builder()
                .client(client.build())
                .baseUrl(BuildConfig.NETWORK_RESOURCE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Singleton
    @Provides
    fun provideNetworkService(retrofit: Retrofit): APIService =
            retrofit.create(APIService::class.java)


    @Singleton
        @Provides
        fun provideDatabase(
            @ApplicationContext app: Context
        ) = Room.databaseBuilder(app, AppDatabase::class.java, "name")
            .fallbackToDestructiveMigration().build()

        @Singleton
        @Provides
        fun provideDao(db: AppDatabase) = db.nameDao()*/

    @Singleton
    @Provides
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ) = ConnectivityManagerNetworkMonitor(context)
}