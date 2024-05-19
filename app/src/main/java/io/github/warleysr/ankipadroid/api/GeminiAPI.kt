package io.github.warleysr.ankipadroid.api

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeminiAPI {

    companion object {

        suspend fun generateContent(apiKey: String, modelName: String, prompt: String, onSuccess: (String?) -> Unit) {
            val model = GenerativeModel(modelName = modelName, apiKey = apiKey)
            try {
                val content = model.generateContent(prompt)
                onSuccess(content.text)
            } catch (exception: GoogleGenerativeAIException) {
                println("Failure")
            }
        }

        fun getAvailableModels(): Map<String, String> {
            return mapOf(
                Pair("gemini-1.5-flash-latest", "Gemini 1.5 Flash"),
                Pair("gemini-1.5-pro-latest", "Gemini 1.5 Pro"),
                Pair("gemini-1.0-pro-latest", "Gemini 1.0 Pro"),
            )
        }
    }
}