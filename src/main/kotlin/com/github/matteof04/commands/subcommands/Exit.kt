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

package com.github.matteof04.commands.subcommands

import com.github.matteof04.commands.Command
import com.github.matteof04.commands.CommandHandler
import kotlin.system.exitProcess

class Exit: Command {
    override fun getCommandString() = "EXIT"
    override fun getHelpString() = """
        Usage: EXIT
        Exit the program
    """.trimIndent()

    override fun execute(handler: CommandHandler, args: List<String>) {
        println("Bye")
        exitProcess(0)
    }
}