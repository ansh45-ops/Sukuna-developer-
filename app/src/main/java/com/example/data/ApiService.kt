package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object ApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    private const val CK_API_BASE = "https://api.bigwinqaz.com/api/webapi/"
    private const val TRX_API = "https://draw.ar-lottery01.com/TrxWinGo/TrxWinGo_1M/GetHistoryIssuePage.json"

    private fun md5Hex(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02X".format(it) }
    }

    private fun random32Hex(): String {
        val chars = "0123456789abcdef"
        val sb = StringBuilder(32)
        for (i in 0 until 32) {
            sb.append(chars[Random.nextInt(chars.length)])
        }
        return sb.toString()
    }

    suspend fun fetchHistory(mode: GameMode): List<GameRecord> = withContext(Dispatchers.IO) {
        try {
            if (mode == GameMode.TRX) {
                fetchTrx()
            } else {
                fetchWinGo(mode)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun fetchWinGo(mode: GameMode): List<GameRecord> {
        val typeId = if (mode == GameMode.M30S) 30 else 1
        val randomStr = random32Hex()
        val timestamp = System.currentTimeMillis() / 1000

        // In CK API signature calculation: sorted keys excluding signature and timestamp
        // language=7&pageNo=1&pageSize=50&random=...&typeId=...
        val jsonParams = JSONObject().apply {
            put("language", 7)
            put("pageNo", 1)
            put("pageSize", 50)
            put("random", randomStr)
            put("typeId", typeId)
        }

        val jsonStr = jsonParams.toString()
        val signature = md5Hex(jsonStr)

        val finalBodyJson = JSONObject(jsonStr).apply {
            put("signature", signature)
            put("timestamp", timestamp)
        }

        val reqBody = finalBodyJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(CK_API_BASE + "GetNoaverageEmerdList")
            .post(reqBody)
            .addHeader("Accept", "application/json, text/plain, */*")
            .build()

        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) return emptyList()
            val raw = resp.body?.string() ?: return emptyList()
            val root = JSONObject(raw)
            if (root.optInt("code", -1) != 0) return emptyList()

            val data = root.optJSONObject("data") ?: return emptyList()
            val list = data.optJSONArray("list") ?: return emptyList()

            val results = mutableListOf<GameRecord>()
            for (i in 0 until list.length()) {
                val item = list.getJSONObject(i)
                val period = item.optString("issueNumber").ifEmpty {
                    item.optString("issue").ifEmpty { item.optString("period") }
                }
                val num = item.optInt("number", item.optInt("openNum", item.optInt("result", -1)))
                if (period.isNotEmpty() && num in 0..9) {
                    results.add(GameRecord(period, num))
                }
            }
            return results
        }
    }

    private fun fetchTrx(): List<GameRecord> {
        val url = "$TRX_API?ts=${System.currentTimeMillis()}"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .build()

        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) return emptyList()
            val raw = resp.body?.string() ?: return emptyList()
            val root = JSONObject(raw)
            val data = root.optJSONObject("data") ?: return emptyList()
            val list = data.optJSONArray("list") ?: return emptyList()

            val results = mutableListOf<GameRecord>()
            for (i in 0 until list.length()) {
                val item = list.getJSONObject(i)
                val period = item.optString("issueNumber").ifEmpty {
                    item.optString("issue").ifEmpty { item.optString("period") }
                }
                val num = item.optInt("number", item.optInt("openNum", item.optInt("drawNumber", -1)))
                if (period.isNotEmpty() && num in 0..9) {
                    results.add(GameRecord(period, num))
                }
            }
            return results
        }
    }

    fun generateSampleHistory(count: Int = 200, basePeriod: String = "2026102110010001"): List<GameRecord> {
        val result = mutableListOf<GameRecord>()
        var curNum = 5
        val base = basePeriod.toLongOrNull() ?: 2026102110010001L

        for (i in 0 until count) {
            val period = (base + i).toString()
            curNum = (curNum + Random.nextInt(1, 8)) % 10
            result.add(0, GameRecord(period, curNum))
        }
        return result
    }
}
