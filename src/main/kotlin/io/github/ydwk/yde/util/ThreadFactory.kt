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
package io.github.ydwk.yde.util

import com.google.common.util.concurrent.ThreadFactoryBuilder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ThreadFactory(private val threadBuild: ThreadFactoryBuilder) {
    fun createThread(name: String, daemon: Boolean, block: Runnable): Thread {
        return threadBuild.setNameFormat("yde-$name-%d").setDaemon(daemon).build().newThread(block)
    }

    fun createThread(name: String, daemon: Boolean, block: () -> Unit): Thread {
        return createThread(name, daemon, Runnable(block))
    }

    fun createThread(name: String, block: () -> Unit): Thread {
        return createThread(name, true, Runnable(block))
    }

    fun getThreadByName(name: String): Thread? {
        return Thread.getAllStackTraces().keys.find { it.name == name }
    }

    fun createThreadExecutor(name: String): ExecutorService {
        return Executors.newSingleThreadExecutor { r -> createThread(name, true, r) }
    }

    fun getThreadExecutorByName(threadName: String): ExecutorService? {
        val thread = getThreadByName(threadName)
        return if (thread != null) {
            Executors.newSingleThreadExecutor { thread }
        } else {
            null
        }
    }
}
