/*
 * Copyright 2023 YDWK inc.
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
package io.github.ydwk.yde.entities.builder

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.CompletableDeferred

interface GenericEntityBuilder<T> {

    /**
     * The json that will be sent to the discord api.
     *
     * @return The json that will be sent to the discord api.
     */
    val json: JsonNode

    /**
     * Creates the new entity.
     *
     * @return A completable future that completes with the created entity.
     */
    fun create(): CompletableDeferred<T>
}
