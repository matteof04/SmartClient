/*
 * Copyright (C) 2023 Matteo Franceschini <matteof5730@gmail.com>
 *
 * This file is part of SmartClient.
 * SmartClient is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * SmartClient is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with SmartClient.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.matteof04.commands.subcommands.param

import com.github.matteof04.SmartApi
import com.github.matteof04.commands.Command
import com.github.matteof04.commands.CommandHandler
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent

class HostParam: Command {
    private val smartApi: SmartApi by KoinJavaComponent.getKoin().inject()

    override fun getCommandString() = "SERVER_URL"

    override fun getHelpString() = """
        Usage: SERVER_URL
        Change the server URL
    """.trimIndent()

    override fun execute(handler: CommandHandler, args: List<String>) {
        print("New server url: ")
        val newBaseUrl = readlnOrNull()
        newBaseUrl?.let {
            runBlocking {
                smartApi.changeBaseUrl(it)
                smartApi.logout()
                println("Success. You need to login again.")
            }
        }
    }
}