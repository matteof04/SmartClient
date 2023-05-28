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

import com.github.matteof04.models.AppConf
import com.github.matteof04.models.JWTToken
import com.github.matteof04.models.device.ChangeUpdateFrequency
import com.github.matteof04.models.device.Device
import com.github.matteof04.models.device.DeviceId
import com.github.matteof04.models.host.Host
import com.github.matteof04.models.host.HostId
import com.github.matteof04.models.house.*
import com.github.matteof04.models.thdata.ThermoHygrometerDataHistory
import com.github.matteof04.models.user.*
import com.github.matteof04.util.HttpException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

private fun getBaseUrl(): String {
    val textAppConf = SmartApi::class.java.classLoader.getResource("application.conf")?.readText()
    val appConf: AppConf? = textAppConf?.let { Json.decodeFromString(it) }
    return appConf?.url ?: ""
}

class SmartApi {
    private var baseUrl = getBaseUrl()
    private val accessTokenStorage = mutableListOf<JWTToken>()
    private val refreshTokenStorage = mutableListOf<JWTToken>()
    private var refreshEnable = true
    private val client = HttpClient(Apache){
        install(ContentNegotiation) {
            json()
        }
    }
    private fun getRefreshString() = refreshTokenStorage.lastOrNull()?.token ?: ""
    private fun getAccessString() = accessTokenStorage.lastOrNull()?.token ?: ""

    init {
        client.plugin(HttpSend).intercept { request ->
            val originalCall = execute(request)
            if (originalCall.response.status.value == 401 && refreshEnable) {
                refresh()
                execute(request)
            } else {
                originalCall
            }
        }
    }

    suspend fun changeBaseUrl(newBaseUrl: String){
        baseUrl = newBaseUrl
    }
    suspend fun login(mail: String, password: String){
        val userLogin = UserLogin(mail, password)
        val response = client.post("$baseUrl/user/login") {
            contentType(ContentType.Application.Json)
            setBody(userLogin)
        }
        if (response.status != HttpStatusCode.OK){
            throw HttpException(response.status.value, response.status.description)
        }
        refreshTokenStorage.add(response.body())
        refresh()
        refreshEnable = true
    }
    suspend fun logout(){
        refreshTokenStorage.removeIf { true }
        accessTokenStorage.removeIf { true }
        refreshEnable = false
    }
    private suspend fun refresh(){
        val response = client.get("$baseUrl/user/refresh"){
            bearerAuth(getRefreshString())
        }
        when(response.status){
            HttpStatusCode.OK -> accessTokenStorage.add(response.body())
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    //USER-RELATED Functions
    suspend fun getUserDetail(): UserDTO {
        val response = client.get("$baseUrl/user/detail"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun editMail(newMail: String) {
        val response = client.post("$baseUrl/user/editMail"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(ChangeMailRequest(newMail))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun editPassword(oldPassword: String, newPassword: String) {
        val response = client.post("$baseUrl/user/editPassword"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(ChangePasswordRequest(oldPassword, newPassword))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun enableUser(userId: UUID) {
        val response = client.post("$baseUrl/user/enable"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(UserId(userId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun disableUser(userId: UUID) {
        val response = client.post("$baseUrl/user/disable"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(UserId(userId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    //DEVICE-RELATED Functions
    suspend fun getDeviceDetail(deviceId: UUID): Device {
        val response = client.get("$baseUrl/device/detail/$deviceId"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun listDevicesByOwner(): List<Device> {
        val response = client.get("$baseUrl/device/listOwner"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun listDevicesByHouse(houseId: UUID): List<Device> {
        val response = client.get("$baseUrl/device/listHouse/$houseId"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun listDevicesByHost(hostId: UUID): List<Device> {
        val response = client.get("$baseUrl/device/listHost/$hostId"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun changeUpdateFrequency(deviceId: UUID, newUpdFreq: UInt){
        val response = client.post("$baseUrl/device/changeUpdateFrequency"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(ChangeUpdateFrequency(deviceId, newUpdFreq))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun beginDeviceAssoc(newDeviceId: UUID) {
        val response = client.post("$baseUrl/device/beginAssoc"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(DeviceId(newDeviceId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun deviceHouseAssoc(deviceId: UUID, houseId: UUID) {
        val response = client.post("$baseUrl/device/houseAssoc"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(DeviceHouseAssoc(deviceId, houseId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun resetDeviceAssoc(deviceId: UUID) {
        val response = client.post("$baseUrl/device/resetAssoc"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(DeviceId(deviceId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun registerNewDevice(): DeviceId {
        val response = client.get("$baseUrl/device/register"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun enableDevice(deviceId: UUID) {
        val response = client.post("$baseUrl/device/enable"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(DeviceId(deviceId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun disableDevice(deviceId: UUID) {
        val response = client.post("$baseUrl/device/disable"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(DeviceId(deviceId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    //HOST-RELATED Functions
    suspend fun getHostDetail(hostId: UUID): Host {
        val response = client.get("$baseUrl/host/detail/$hostId"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun listHostsByOwner(): List<Host> {
        val response = client.get("$baseUrl/host/listOwner"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun listHostsByHouse(houseId: UUID): List<Host> {
        val response = client.get("$baseUrl/host/listHouse/$houseId"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun beginHostAssoc(newHostId: UUID) {
        val response = client.post("$baseUrl/host/beginAssoc"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(HostId(newHostId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun hostHouseAssoc(hostId: UUID, houseId: UUID) {
        val response = client.post("$baseUrl/host/houseAssoc"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(HostHouseAssoc(hostId, houseId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun resetHostAssoc(hostId: UUID) {
        val response = client.post("$baseUrl/host/resetAssoc"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(HostId(hostId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun registerNewHost(): HostId {
        val response = client.get("$baseUrl/host/register"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun enableHost(hostId: UUID) {
        val response = client.post("$baseUrl/host/enable"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(HostId(hostId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun disableHost(hostId: UUID) {
        val response = client.post("$baseUrl/host/disable"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(HostId(hostId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    //HOUSE-RELATED Functions
    suspend fun getHouseDetail(houseId: UUID): House {
        val response = client.get("$baseUrl/house/detail/$houseId"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun listHouseByOwner(): List<House> {
        val response = client.get("$baseUrl/house/list"){
            bearerAuth(getAccessString())
        }
        when(response.status.value){
            200 -> return response.body()
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun newHouse(houseName: String) {
        val response = client.post("$baseUrl/house/new"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(NewHouse(houseName))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun updateHouse(houseId: UUID, newHouseName: String) {
        val user = getUserDetail()
        val response = client.post("$baseUrl/house/update"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(House(houseId, newHouseName, user.id))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    suspend fun deleteHouse(houseId: UUID) {
        val response = client.post("$baseUrl/house/delete"){
            bearerAuth(getAccessString())
            contentType(ContentType.Application.Json)
            setBody(HouseId(houseId))
        }
        when(response.status.value){
            200 -> {}
            else -> throw HttpException(response.status.value, response.status.description)
        }
    }
    //THDATA-RELATED Functions
    suspend fun getThDataDetail(dataId: UUID): ThermoHygrometerDataHistory {
        val response = client.get("$baseUrl/thdata/detail/$dataId"){
            bearerAuth(getAccessString())
        }
        if(response.status != HttpStatusCode.OK){
            throw HttpException(response.status.value, response.status.description)
        }
        return response.body()
    }
    suspend fun getThDataList(deviceId: UUID): List<ThermoHygrometerDataHistory> {
        val response = client.get("$baseUrl/thdata/list/$deviceId"){
            bearerAuth(getAccessString())
        }
        if(response.status != HttpStatusCode.OK){
            throw HttpException(response.status.value, response.status.description)
        }
        return response.body()
    }
}
