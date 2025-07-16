package xyz.chronosirius.accordion

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp
class Accordion: Application() {

}

@Module
@InstallIn(SingletonComponent::class)
object Network {

    @Provides
    @Singleton
    fun provideDiscordApiClient(sp: SharedPreferences): DiscordApiClient {
        return DiscordApiClient(sp)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
    }
}

