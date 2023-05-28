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

class HostResetAssoc: Command {
    private val smartApi: SmartApi by KoinJavaComponent.getKoin().inject()

    override fun getCommandString() = "RESET"

    override fun getHelpString() = """
        Usage: RESET <deviceId>
        Reset the association for the host with the given host ID and for all the devices associated to it
    """.trimIndent()

    override fun execute(handler: CommandHandler, args: List<String>) {
        try {
            val hostId = UUID.fromString(args.first())
            println("""
                |<=== WARNING! ===>
                |If you continue, the host and all the devices associated to it will be removed from your account.
                |To continue, type "Yes, i want to reset my host" after this message""".trimMargin())
            if (readlnOrNull() == "Yes, i want to reset my host") {
                runBlocking {
                    smartApi.resetHostAssoc(hostId)
                }
                println("Done")
            }else{
                println("Device reset aborted")
            }
        }catch (e: HttpException){
            println("Network Error: ${e.message}")
        }catch (e: IllegalArgumentException){
            println("Malformed UUID")
        }catch (e: NoSuchElementException){
            println("Malformed UUID")
        }
    }
}