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
package io.github.ydwk.yde.entities.guild.invite

enum class TargetType(private val value: Int) {
    STREAM(1),
    EMBEDDED_APPLICATION(2);

    companion object {
        /**
         * The [TargetType] by its value.
         *
         * @param value the value.
         * @return the [TargetType].
         */
        fun fromValue(value: Int): TargetType {
            return values().first { it.value == value }
        }
    }

    /**
     * The value of the [TargetType].
     *
     * @return the value.
     */
    fun getValue(): Int {
        return value
    }
}
