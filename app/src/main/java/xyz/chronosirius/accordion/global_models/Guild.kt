package xyz.chronosirius.accordion.global_models

import androidx.compose.ui.graphics.Color
import xyz.chronosirius.accordion.data.DataObject

data class Guild(
    override val id: Long,
    val name: String,
    val icon: String? = null,
    val iconHash: String? = null,
    val splash: String? = null,
    val discoverySplash: String? = null,
    val owner: Boolean = false,
    val ownerId: Long? = null,
    val permissions: String? = null,
    val region: String? = null,
    val AFKChannelId: Long? = null,
    val AFKTimeout: Int = 300,
    val widgetEnabled: Boolean = false,
    val widgetChannelId: Long? = null,
    val verificationLevel: Int = 0,
    val defaultMessageNotifications: Int = 0,
    val explicitContentFilter: Int = 0,
    val roles: List<DataObject> = emptyList(),
    val emojis: List<DataObject> = emptyList(),
    val features: List<String> = emptyList(),
    val mfaLevel: Int = 0,
    val applicationId: String? = null,
    val systemChannelId: Long? = null,
    val systemChannelFlags: Int = 0,
    val rulesChannelId: Long? = null,
    val maxPresences: Int? = null,
    val maxMembers: Int? = null,
    val vanityUrlCode: String? = null,
    val description: String? = null,
    val banner: String? = null,
    val premiumTier: Int = 0,
    val premiumSubscriptionCount: Int = 0,
    val preferredLocale: String? = null,
    val publicUpdatesChannelId: Long? = null,
    val maxVideoChannelUsers: Int? = null,
    val maxStageVideoChannelUsers: Int? = null,
    val approximateMemberCount: Int? = null,
    val approximatePresenceCount: Int? = null,
    val welcomeScreen: DataObject? = null,
    val nsfwLevel: Int = 0,
    val stickers: List<DataObject> = emptyList(),
    val premiumProgressBarEnabled: Boolean = false,
    val safetyAlertsChannelId: Long? = null,
    val incidentsData: List<DataObject>? = null,
): Snowflaked() {

    // Additional properties can be added here as needed
    // For example, name, icon, owner, etc.

    companion object {
        fun fromJson(json: DataObject): Guild {
            val guild = Guild(
                id = json.getLong("id"),
                name = json.getString("name"),
                icon = json.getString("icon", null),
                iconHash = json.getString("icon_hash", null),
                splash = json.getString("splash", null),
                discoverySplash = json.getString("discovery_splash", null),
                owner = json.getBoolean("owner", false),
                ownerId = json.getLongOrNull("owner_id"),
                permissions = json.getString("permissions", null),
                region = json.getString("region", null),
                AFKChannelId = json.getLongOrNull("afk_channel_id"),
                AFKTimeout = json.getInt("afk_timeout", 300),
                widgetEnabled = json.getBoolean("widget_enabled", false),
                widgetChannelId = json.getLongOrNull("widget_channel_id"),
                verificationLevel = json.getInt("verification_level", 0),
                defaultMessageNotifications = json.getInt("default_message_notifications", 0),
                explicitContentFilter = json.getInt("explicit_content_filter", 0),
                roles = try {json.getObjectArray("roles").map { it } } catch (_: Exception) { emptyList() },
                emojis = try {json.getObjectArray("emojis").map { it } } catch (_: Exception) { emptyList() },
                features = json.getArray("features").map { it.toString() },
                mfaLevel = json.getInt("mfa_level", 0),
                applicationId = json.getString("application_id", null),
                systemChannelId = json.getLongOrNull("system_channel_id"),
                systemChannelFlags = json.getInt("system_channel_flags", 0),
                rulesChannelId = json.getLongOrNull("rules_channel_id"),
                maxPresences = json.getIntOrNull("max_presences"),
                maxMembers = json.getIntOrNull("max_members"),
                vanityUrlCode = json.getString("vanity_url_code", null),
                description = json.getString("description", null),
                banner = json.getString("banner", null),
                premiumTier = json.getInt("premium_tier", 0),
                premiumSubscriptionCount = json.getInt("premium_subscription_count", 0),
                preferredLocale = json.getString("preferred_locale", null),
                publicUpdatesChannelId = json.getLongOrNull("public_updates_channel_id"),
                maxVideoChannelUsers = json.getIntOrNull("max_video_channel_users"),
                maxStageVideoChannelUsers = json.getIntOrNull("max_stage_video_channel_users"),
                approximateMemberCount = json.getIntOrNull("approximate_member_count"),
                approximatePresenceCount = json.getIntOrNull("approximate_presence_count"),
                welcomeScreen = json.getObject("welcome_screen", null),
                nsfwLevel = json.getInt("nsfw_level", 0),
                stickers = try {json.getObjectArray("stickers").map { it } } catch (_: Exception) { emptyList() },
                premiumProgressBarEnabled = json.getBoolean("premium_progress_bar_enabled", false),
                safetyAlertsChannelId = json.getLongOrNull("safety_alerts_channel_id"),
                incidentsData = try { json.getObjectArray("incidents_data").map { it } } catch (_: Exception) { emptyList() }
            )
            // Parse other properties as needed
            return guild
        }
    }
}

data class GuildUIFolder(
    val id: Long? = null,
    val name: String? = null,
    val color: Int? = null,
    val guilds: List<Guild>
)
