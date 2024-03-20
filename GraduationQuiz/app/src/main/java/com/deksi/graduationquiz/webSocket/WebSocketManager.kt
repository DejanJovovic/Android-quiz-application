package com.deksi.graduationquiz.webSocket

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class WebSocketManager(private val listener: WebSocketEventListener) {

    private lateinit var webSocket: WebSocket
    private val gson = Gson()

    fun connect(okHttpClient: OkHttpClient, url: String) {
        Log.d("WebSocketManager", "Connecting to WebSocket server: $url")
        val request = Request.Builder().url(url).build()
        val listener = WebSocketListenerImpl()
        webSocket = okHttpClient.newWebSocket(request, listener)
    }

    fun sendMessage(message: String) {
        Log.d("WebSocketManager", "Sending message: $message")
        val json = gson.toJson(message)
        webSocket.send(json)
    }

    fun closeConnection() {
        Log.d("WebSocketManager", "Closing WebSocket connection")
        webSocket.close(1000, "Closing connection")
    }

    private inner class WebSocketListenerImpl : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocketManager", "WebSocket connection opened")
            listener.onConnectionOpened()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WebSocketManager", "Received message: $text")
            try {
                // Your parsing logic here
            } catch (e: JsonSyntaxException) {
                Log.e("WebSocketManager", "Error parsing JSON", e)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocketManager", "WebSocket connection closed: $reason")
            listener.onConnectionClosed()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocketManager", "WebSocket connection failure", t)
            listener.onConnectionFailure(t.message ?: "Unknown error")
        }
    }

    interface WebSocketEventListener {
        fun onConnectionOpened()
        fun onGameStateReceived(message: GameStateMessage)
        fun onConnectionClosed()
        fun onConnectionFailure(error: String)
    }

}