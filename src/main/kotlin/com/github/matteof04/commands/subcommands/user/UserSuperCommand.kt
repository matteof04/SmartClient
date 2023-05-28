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

package com.github.matteof04.commands.subcommands.user

import com.github.matteof04.commands.subcommands.SuperCommand
import com.github.matteof04.commands.subcommands.user.edit.UserEditSuperCommand

class UserSuperCommand: SuperCommand() {
    init {
        subCommandHandler.registerCommand(UserDetail())
        subCommandHandler.registerCommand(UserEditSuperCommand())
        subCommandHandler.registerCommand(UserEnable())
        subCommandHandler.registerCommand(UserDisable())
    }

    override fun getCommandString() = "USER"

    override fun getHelpString() = """
        Usage: USER <subcommand>
        User-related commands
    """.trimIndent()
}