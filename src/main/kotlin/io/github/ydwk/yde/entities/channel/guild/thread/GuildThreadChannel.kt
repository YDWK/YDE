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
package io.github.ydwk.yde.entities.channel.guild.thread

import io.github.ydwk.yde.entities.Guild
import io.github.ydwk.yde.entities.channel.TextChannel
import io.github.ydwk.yde.entities.channel.guild.message.text.GuildTextChannel
import io.github.ydwk.yde.util.GetterSnowFlake

// TODO : Add functionality to this class
/** A thread channel in a guild. */
interface GuildThreadChannel : TextChannel {

    /**
     * The guild of this channel.
     *
     * @return the guild of this channel.
     */
    val guild: Guild

    /**
     * The text channel parent of this thread.
     *
     * @return The text channel parent of this thread.
     */
    var textChannelParent: GuildTextChannel

    /**
     * The number of messages sent (stops counting at 50).
     *
     * @return the number of messages sent.
     */
    var messageCount: Int

    /**
     * The number of members partcipating in the thread (stops counting at 50).
     *
     * @return The number of members partcipating in the thread
     */
    var memberCount: Int

    /**
     * The id of the last message present in the thread channel.
     *
     * @return the last message id.
     */
    var lastMessageId: GetterSnowFlake

    /**
     * The total amount of messages sent.
     *
     * @return The total amount of messages sent.
     */
    var totalMessagesSent: Int

    /**
     * The rate limit per user.
     *
     * @return the rate limit per user.
     */
    var rateLimitPerUser : Int

    /**
     * Extra information involving this thread.
     *
     * @return the [ThreadMetadata] object containg extra infomation about the thread.
     */
}
