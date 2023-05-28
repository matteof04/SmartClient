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

package com.github.matteof04.commands.subcommands.thdata

import com.github.matteof04.SmartApi
import com.github.matteof04.commands.Command
import com.github.matteof04.commands.CommandHandler
import com.github.matteof04.util.HttpException
import kotlinx.coroutines.runBlocking
import java.util.*
import org.koin.java.KoinJavaComponent
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.collections.List

class ThDataList: Command {
    private val smartApi: SmartApi by KoinJavaComponent.getKoin().inject()

    override fun getCommandString() = "LIST"
    override fun getHelpString() = """
        Usage: LIST <deviceId>
        List all the ThData record for the specified deviceId
    """.trimIndent()

    override fun execute(handler: CommandHandler, args: List<String>) {
        try {
            val deviceId = UUID.fromString(args.first())
            runBlocking {
                val data = smartApi.getThDataList(deviceId)
                data.forEach {
                    println("""
                        ID: ${it.id}
                        #    Temperature: ${it.temperature}°C
                        #    Humidity: ${it.humidity}%
                        #    Heat Index: ${it.heatIndex}°C
                        #    Battery Percentage: ${it.batteryPercentage}%
                        #    Timestamp: ${it.timestamp.atZone(ZoneId.systemDefault())}
                    """.trimIndent().trimMargin("#")
                    )
                }
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