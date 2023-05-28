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

package com.github.matteof04

import com.github.matteof04.commands.CommandHandler
import com.github.matteof04.commands.subcommands.*
import com.github.matteof04.commands.subcommands.device.DeviceSuperCommand
import com.github.matteof04.commands.subcommands.host.HostSuperCommand
import com.github.matteof04.commands.subcommands.house.HouseSuperCommand
import com.github.matteof04.commands.subcommands.param.Param
import com.github.matteof04.commands.subcommands.thdata.ThDataSuperCommand
import com.github.matteof04.commands.subcommands.user.UserSuperCommand
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.system.exitProcess

val smartModule = module { single { SmartApi() } }

fun initializeCommandHandler(): CommandHandler {
    val commandHandler = CommandHandler()
    commandHandler.registerCommand(Connect())
    commandHandler.registerCommand(Exit())
    commandHandler.registerCommand(Help())
    commandHandler.registerCommand(Logout())
    commandHandler.registerCommand(ThDataSuperCommand())
    commandHandler.registerCommand(DeviceSuperCommand())
    commandHandler.registerCommand(UserSuperCommand())
    commandHandler.registerCommand(Param())
    commandHandler.registerCommand(HostSuperCommand())
    commandHandler.registerCommand(HouseSuperCommand())
    commandHandler.registerCommand(License())
    return commandHandler
}

fun main(args: Array<String>) {
    if (License.getLicenseText().isNullOrBlank()) {
        exitProcess(1)
    }
    println("""
        SmartClient  Copyright (C) 2023  Matteo Franceschini
        This program comes with ABSOLUTELY NO WARRANTY; for details type `license'.
        This is free software, and you are welcome to redistribute it
        under certain conditions; type `license' for details.
        
    """.trimIndent())
    startKoin {
        modules(smartModule)
    }
    val commandHandler = initializeCommandHandler()
    while (true){
        print(">>>")
        val input = readlnOrNull()
        input?.let { commandHandler.processCommand(it) }
    }
}