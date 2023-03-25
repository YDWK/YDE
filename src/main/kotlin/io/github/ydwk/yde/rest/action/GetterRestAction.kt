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

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Future

/** An action that can be executed while returning a result such as get, list, etc. */
class GetterRestAction<T> : CompletionStage<T> by CompletableFuture(), ExtendableAction<T>() {
    private val completableFuture: CompletableFuture<T> = CompletableFuture()

    /**
     * Gets the action (executes it) and returns a [T] object.
     *
     * @return a [T] object.
     */
    fun get(): T {
        return completableFuture.get()
    }

    /**
     * Gets the action (executes it) and returns a [Future] object.
     *
     * @return a [Future] object.
     */
    fun getAsync(): Future<T> {
        return completableFuture
    }

    /**
     * Executes the action and returns a [CompletableFuture] object.
     *
     * @return a [CompletableFuture] object.
     */
    fun executeCompletable(): CompletableFuture<T> {
        return completableFuture
    }
}
