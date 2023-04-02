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
package io.github.ydwk.yde.rest.methods

import io.github.ydwk.yde.entities.User
import io.github.ydwk.yde.entities.channel.DmChannel
import io.github.ydwk.yde.rest.action.GetterRestAction
import io.github.ydwk.yde.rest.action.RestExecutableRestAction

interface UserRestAPIMethods {
    /**
     * Creates a direct message channel with this user.
     *
     * @param id the id of the user.
     * @return a future containing the direct message channel.
     */
    fun createDm(id: Long): RestExecutableRestAction<DmChannel>

    /**
     * Request a user by its id.
     *
     * @param id the id of the user.
     * @return a future containing the user.
     */
    fun requestUser(id: Long): GetterRestAction<User>

    /**
     * Request to get a list of all the users that are visible to the bot.
     *
     * @return a future containing the list of users.
     */
    fun requestUsers(): GetterRestAction<List<User>>
}
