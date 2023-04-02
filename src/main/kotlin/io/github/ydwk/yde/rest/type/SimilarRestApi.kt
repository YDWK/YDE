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
package io.github.ydwk.yde.rest.type

import io.github.ydwk.yde.rest.action.ExtendableAction
import io.github.ydwk.yde.rest.action.GetterRestAction
import io.github.ydwk.yde.rest.action.NoResultExecutableRestAction
import io.github.ydwk.yde.rest.action.RestExecutableRestAction
import io.github.ydwk.yde.rest.cf.CompletableFutureManager
import java.util.function.Function
import okhttp3.Headers

interface SimilarRestApi {
    fun header(name: String, value: String): SimilarRestApi

    fun addHeader(name: String, value: String): SimilarRestApi

    fun removeHeader(name: String): SimilarRestApi

    fun headers(headers: Headers): SimilarRestApi

    fun addReason(reason: String?): SimilarRestApi

    fun execute()

    fun <T : Any> execute(function: Function<CompletableFutureManager, T>): ExtendableAction<T>

    fun <T : Any> executeGetterRestAction(
        function: (t: CompletableFutureManager) -> T
    ): GetterRestAction<T> {
        return execute(function) as GetterRestAction<T>
    }

    fun <T : Any> executeExecutableRestAction(
        function: Function<CompletableFutureManager, T>
    ): RestExecutableRestAction<T> {
        return execute(function) as RestExecutableRestAction<T>
    }

    fun executeWithNoResult(): NoResultExecutableRestAction
}
