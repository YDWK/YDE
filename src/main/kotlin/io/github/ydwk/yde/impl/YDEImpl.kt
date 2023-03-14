/*
 * Copyright 2022 YDWK inc.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package io.github.ydwk.yde.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.ydwk.yde.YDE
import io.github.ydwk.yde.builders.message.IMessageCommandBuilder
import io.github.ydwk.yde.builders.slash.ISlashCommandBuilder
import io.github.ydwk.yde.builders.user.IUserCommandBuilder
import io.github.ydwk.yde.cache.*
import io.github.ydwk.yde.entities.Bot
import io.github.ydwk.yde.entities.Channel
import io.github.ydwk.yde.entities.Guild
import io.github.ydwk.yde.entities.User
import io.github.ydwk.yde.entities.builder.EntityBuilder
import io.github.ydwk.yde.entities.channel.DmChannel
import io.github.ydwk.yde.entities.channel.GuildChannel
import io.github.ydwk.yde.entities.channel.enums.ChannelType
import io.github.ydwk.yde.entities.guild.Member
import io.github.ydwk.yde.entities.message.embed.builder.EmbedBuilder
import io.github.ydwk.yde.exceptions.ApplicationIdNotSetException
import io.github.ydwk.yde.impl.builders.message.IMessageCommandBuilderImpl
import io.github.ydwk.yde.impl.builders.slash.SlashBuilderImpl
import io.github.ydwk.yde.impl.builders.user.IUserCommandBuilderImpl
import io.github.ydwk.yde.impl.entities.GuildImpl
import io.github.ydwk.yde.impl.entities.UserImpl
import io.github.ydwk.yde.impl.entities.builder.EntityBuilderImpl
import io.github.ydwk.yde.impl.entities.channel.DmChannelImpl
import io.github.ydwk.yde.impl.entities.channel.guild.GuildChannelImpl
import io.github.ydwk.yde.impl.entities.message.embed.builder.EmbedBuilderImpl
import io.github.ydwk.yde.impl.rest.RestApiManagerImpl
import io.github.ydwk.yde.rest.EndPoint
import io.github.ydwk.yde.rest.RestApiManager
import io.github.ydwk.yde.util.EntityToStringBuilder
import java.util.concurrent.CompletableFuture
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class YDEImpl(
    protected open var token: String? = null,
    open var applicationId: String? = null,
    protected open val client: OkHttpClient,
    protected open var guildIdList: MutableList<String> = mutableListOf(),
    open override val githubRepositoryUrl: String,
    open override val wrapperVersion: String,
) : YDE {
    val logger: Logger = LoggerFactory.getLogger(YDEImpl::class.java)

    protected val allowedCache: MutableSet<CacheIds> = mutableSetOf()
    val cache: Cache = PerpetualCache(allowedCache)
    val memberCache: MemberCache = MemberCacheImpl(allowedCache)

    override val objectNode: ObjectNode
        get() = JsonNodeFactory.instance.objectNode()

    override val objectMapper: ObjectMapper
        get() = ObjectMapper()

    override val restApiManager: RestApiManager
        get() {
            val botToken = token ?: throw IllegalStateException("Bot token is not set")
            return RestApiManagerImpl(botToken, this, client)
        }

    override fun createDmChannel(userId: Long): CompletableFuture<DmChannel> {
        return this.restApiManager
            .post(
                this.objectMapper
                    .createObjectNode()
                    .put("recipient_id", userId)
                    .toString()
                    .toRequestBody(),
                EndPoint.UserEndpoint.CREATE_DM)
            .execute {
                val jsonBody = it.jsonBody
                if (jsonBody == null) {
                    throw IllegalStateException("json body is null")
                } else {
                    DmChannelImpl(this, jsonBody, jsonBody["id"].asLong())
                }
            }
    }

    override fun getMemberById(guildId: Long, userId: Long): Member? {
        return memberCache[guildId.toString(), userId.toString()]
    }

    override fun getMembers(): List<Member> {
        return memberCache.values().map { it }
    }

    override fun getUserById(id: Long): User? {
        return cache[id.toString(), CacheIds.USER] as User?
    }

    override fun getUsers(): List<User> {
        return cache.values(CacheIds.USER).map { it as User }
    }

    override fun requestUser(id: Long): CompletableFuture<User> {
        return this.restApiManager.get(EndPoint.UserEndpoint.GET_USER, id.toString()).execute {
            val jsonBody = it.jsonBody
            if (jsonBody == null) {
                throw IllegalStateException("json body is null")
            } else {
                UserImpl(jsonBody, jsonBody["id"].asLong(), this)
            }
        }
    }

    override fun requestGuild(guildId: Long): CompletableFuture<Guild> {
        return this.restApiManager
            .get(EndPoint.GuildEndpoint.GET_GUILD, guildId.toString())
            .execute {
                val jsonBody = it.jsonBody
                if (jsonBody == null) {
                    throw IllegalStateException("json body is null")
                } else {
                    GuildImpl(this, jsonBody, jsonBody["id"].asLong())
                }
            }
    }

    override fun getGuildById(id: String): Guild? {
        return cache[id, CacheIds.GUILD] as Guild?
    }

    override fun getChannelById(id: Long): Channel? {
        return cache[id.toString(), CacheIds.CHANNEL] as Channel?
    }

    override fun getChannels(): List<Channel> {
        return cache.values(CacheIds.CHANNEL).map { it as Channel }
    }

    override fun requestChannelById(id: Long): CompletableFuture<Channel> {
        return this.restApiManager
            .get(EndPoint.ChannelEndpoint.GET_CHANNEL, id.toString())
            .execute {
                val jsonBody = it.jsonBody
                if (jsonBody == null) {
                    throw IllegalStateException("json body is null")
                } else {
                    val channelType = ChannelType.fromInt(jsonBody["type"].asInt())
                    if (ChannelType.isGuildChannel(channelType)) {
                        GuildChannelImpl(this, jsonBody, jsonBody["id"].asLong())
                    } else {
                        DmChannelImpl(this, jsonBody, jsonBody["id"].asLong())
                    }
                }
            }
    }

    override fun requestGuildChannelById(id: Long, guildId: Long): CompletableFuture<GuildChannel> {
        return this.restApiManager
            .get(EndPoint.ChannelEndpoint.GET_CHANNEL, id.toString())
            .execute {
                val jsonBody = it.jsonBody
                if (jsonBody == null) {
                    throw IllegalStateException("json body is null")
                } else {
                    GuildChannelImpl(this, jsonBody, jsonBody["id"].asLong())
                }
            }
    }

    override fun requestGuildChannels(guildId: Long): CompletableFuture<List<GuildChannel>> {
        return this.restApiManager
            .get(EndPoint.GuildEndpoint.GET_GUILD_CHANNELS, guildId.toString())
            .execute { it ->
                val jsonBody = it.jsonBody
                jsonBody?.map { GuildChannelImpl(this, it, it["id"].asLong()) }
                    ?: throw IllegalStateException("json body is null")
            }
    }

    override val entityBuilder: EntityBuilder
        get() = EntityBuilderImpl(this)

    override val slashBuilder: ISlashCommandBuilder
        get() =
            SlashBuilderImpl(
                this, guildIdList, applicationId ?: throw ApplicationIdNotSetException())

    override val userCommandBuilder: IUserCommandBuilder
        get() =
            IUserCommandBuilderImpl(
                this, guildIdList, applicationId ?: throw ApplicationIdNotSetException())

    override val messageCommandBuilder: IMessageCommandBuilder
        get() =
            IMessageCommandBuilderImpl(
                this, guildIdList, applicationId ?: throw ApplicationIdNotSetException())

    override val embedBuilder: EmbedBuilder
        get() = EmbedBuilderImpl(this)

    override fun setGuildIds(vararg guildIds: String) {
        guildIds.forEach { this.guildIdList.add(it) }
    }

    override fun setAllowedCache(vararg cacheTypes: CacheIds) {
        allowedCache.addAll(cacheTypes.toSet())
    }

    override fun setDisallowedCache(vararg cacheTypes: CacheIds) {
        allowedCache.removeAll(cacheTypes.toSet())
    }

    override val bot: Bot? = null
        get() {
            while (field == null) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            } // wait for bot to be set
            return field
        }

    override fun toString(): String {
        return EntityToStringBuilder(this, this).add("applicationId", applicationId).toString()
    }
}
