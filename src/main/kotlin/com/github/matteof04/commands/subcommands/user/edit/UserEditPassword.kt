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

package com.github.matteof04.commands.subcommands.user.edit

import com.github.matteof04.SmartApi
import com.github.matteof04.commands.Command
import com.github.matteof04.commands.CommandHandler
import com.github.matteof04.util.HttpException
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent

class UserEditPassword: Command {
    private val smartApi: SmartApi by KoinJavaComponent.getKoin().inject()

    override fun getCommandString() = "PASSWORD"

    override fun getHelpString() = """
        Usage: PASSWORD
        Change user password
    """.trimIndent()

    override fun execute(handler: CommandHandler, args: List<String>) {
        print("Old password: ")
        val oldPassword = System.console()?.readPassword()?.toString() ?: readlnOrNull()
        print("New password: ")
        val newPassword = System.console()?.readPassword()?.toString() ?: readlnOrNull()
        if (oldPassword != null && newPassword != null){
            try {
                runBlocking {
                    smartApi.editPassword(oldPassword, newPassword)
                    smartApi.logout()
                }
                println("Success. You need to login again.")
            }catch (e: HttpException){
                println("Login Error: ${e.message}")
            }
        }else{
            println("Passwords empty")
        }
    }
}