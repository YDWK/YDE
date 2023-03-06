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
package io.github.ydwk.yde.impl.entities.channel.guild.forum

import com.fasterxml.jackson.databind.JsonNode
import io.github.ydwk.yde.YDE
import io.github.ydwk.yde.entities.channel.guild.forum.ForumTag
import io.github.ydwk.yde.util.EntityToStringBuilder
import io.github.ydwk.yde.util.GetterSnowFlake

class ForumTagImpl(
    override val yde: YDE,
    override val json: JsonNode,
    override val idAsLong: Long,
) : ForumTag {
    override val isManaged: Boolean
        get() = json["moderated"].asBoolean()

    override val emojiId: GetterSnowFlake?
        get() = if (json.has("emoji_id")) GetterSnowFlake.of(json["emoji_id"].asText()) else null
    override val emojiName: String?
        get() = if (json.has("name")) json["name"].asText() else null

    override var name: String = json["name"].asText()

    override fun toString(): String {
        return EntityToStringBuilder(yde, this).toString()
    }
}
