package xyz.chronosirius.accordion

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
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
    fun provideDiscordApiClient(): DiscordApiClient {
        return DiscordApiClient()
    }
}

