package xyz.chronosirius.accordion.repositories

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import xyz.chronosirius.accordion.DiscordApiClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Repositories {

    @Provides
    @Singleton
    fun provideDirectMessagesRepository(apiClient: DiscordApiClient): DirectMessagesRepository {
        return DirectMessagesRepository(apiClient)
    }

    @Provides
    @Singleton
    fun provideGatewayRepository(): GatewayRepository {
        return GatewayRepository()
    }

    @Provides
    @Singleton
    fun provideGuildRepository(apiClient: DiscordApiClient): GuildRepository {
        return GuildRepository(apiClient)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(apiClient: DiscordApiClient): SettingsRepository {
        return SettingsRepository(apiClient)
    }
}