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

package com.github.matteof04.commands.subcommands.host.assoc

import com.github.matteof04.SmartApi
import com.github.matteof04.commands.Command
import com.github.matteof04.commands.CommandHandler
import com.github.matteof04.util.HttpException
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent
import java.util.*

class HostHouseAssoc: Command {
    private val smartApi: SmartApi by KoinJavaComponent.getKoin().inject()

    override fun getCommandString() = "HOUSE"

    override fun getHelpString() = """
        Usage: HOUSE <hostId> <houseId>
        Associate the host with the given host ID to the house with the given house ID
    """.trimIndent()

    override fun execute(handler: CommandHandler, args: List<String>) {
        try {
            val hostId = UUID.fromString(args[0])
            val houseId = UUID.fromString(args[1])
            runBlocking {
                smartApi.hostHouseAssoc(hostId, houseId)
            }
            println("Success")
        }catch (e: HttpException){
            println("Network Error: ${e.message}")
        }catch (e: IllegalArgumentException){
            println("Malformed UUID")
        }catch (e: NoSuchElementException){
            println("Malformed UUID")
        }
    }
}