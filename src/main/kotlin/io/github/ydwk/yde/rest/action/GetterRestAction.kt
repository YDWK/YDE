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
package io.github.ydwk.yde.rest.action

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

/** An action that can be executed while returning a result such as get, list, etc. */
class GetterRestAction<T>(private val deferred: CompletableDeferred<T> = CompletableDeferred()) :
    ExtendableAction<T>(deferred) {

    /**
     * Gets the action (executes it) and returns a [T] object.
     *
     * @return a [T] object.
     */
    suspend fun get(): T {
        return deferred.await()
    }

    /**
     * Gets the action (executes it) and returns a [Deferred] object.
     *
     * @return a [Deferred] object.
     */
    fun getAsync(): Deferred<T> {
        return deferred
    }

    /**
     * Executes the action and returns a [CompletableDeferred] object.
     *
     * @return a [CompletableDeferred] object.
     */
    fun executeCompletable(): CompletableDeferred<T> {
        return deferred
    }
}
