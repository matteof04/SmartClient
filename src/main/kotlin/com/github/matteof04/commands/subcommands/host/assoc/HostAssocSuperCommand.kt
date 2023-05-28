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

import com.github.matteof04.commands.subcommands.SuperCommand

class HostAssocSuperCommand: SuperCommand() {
    init {
        subCommandHandler.registerCommand(HostBeginAssoc())
        subCommandHandler.registerCommand(HostResetAssoc())
        subCommandHandler.registerCommand(HostHouseAssoc())
    }

    override fun getCommandString() = "ASSOC"

    override fun getHelpString() = """
        Usage: HOST <subcommand>
        Host-related association commands
    """.trimIndent()
}