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

package com.github.matteof04.commands.subcommands.house

import com.github.matteof04.SmartApi
import com.github.matteof04.commands.Command
import com.github.matteof04.commands.CommandHandler
import com.github.matteof04.util.HttpException
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent
import java.util.*

class HouseUpdate: Command {
    private val smartApi: SmartApi by KoinJavaComponent.getKoin().inject()

    override fun getCommandString() = "UPDATE"

    override fun getHelpString() = """
        Usage: UPDATE <houseId> <newName>
        Change the name to the house with the specified by the houseId
    """.trimIndent()

    override fun execute(handler: CommandHandler, args: List<String>) {
        try {
            val houseId = UUID.fromString(args[0])
            val newName = args[1]
            runBlocking {
                smartApi.updateHouse(houseId, newName)
                println("Updated")
            }
        }catch (e: HttpException){
            println("Network Error: ${e.message}")
        }catch (e: IllegalArgumentException){
            println("Malformed UUID")
        }catch (e: NoSuchElementException){
            println("Missing arguments")
        }
    }
}