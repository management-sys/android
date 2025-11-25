package com.example.attendancemanagementapp.di

import com.example.attendancemanagementapp.retrofit.service.AuthService
import com.example.attendancemanagementapp.retrofit.service.AuthorService
import com.example.attendancemanagementapp.retrofit.service.CommonCodeService
import com.example.attendancemanagementapp.retrofit.service.DepartmentService
import com.example.attendancemanagementapp.retrofit.service.EmployeeService
import com.example.attendancemanagementapp.retrofit.service.ProjectService
import com.example.attendancemanagementapp.retrofit.token.AuthInterceptor
import com.example.attendancemanagementapp.retrofit.token.TokenAuthenticator
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOnlyRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    /* 토큰 리프레시 전용 retrofit */
    @Provides
    @Singleton
    @AuthOnlyRetrofit
    fun provideAuthOnlyRetrofit(
        moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenAuthenticator: TokenAuthenticator,
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    /* 각종 서비스 */
    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideAuthorService(retrofit: Retrofit): AuthorService =
        retrofit.create(AuthorService::class.java)

    @Provides
    @Singleton
    fun provideCommonCodeService(retrofit: Retrofit): CommonCodeService =
        retrofit.create(CommonCodeService::class.java)

    @Provides
    @Singleton
    fun provideDepartmentService(retrofit: Retrofit): DepartmentService =
        retrofit.create(DepartmentService::class.java)

    @Provides
    @Singleton
    fun provideEmployeeService(retrofit: Retrofit): EmployeeService =
        retrofit.create(EmployeeService::class.java)

    @Provides
    @Singleton
    fun provideProjectService(retrofit: Retrofit): ProjectService =
        retrofit.create(ProjectService::class.java)
}