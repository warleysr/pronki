package io.github.warleysr.pronki.api

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException

class GeminiAPI {

    companion object {

        suspend fun generateContent(
            apiKey: String,
            modelName: String,
            prompt: String,
            onSuccess: (String?, Int?) -> Unit,
            onFailure: (String?) -> Unit
        ) {
            val model = GenerativeModel(
                modelName = modelName,
                apiKey = apiKey,
                safetySettings = arrayListOf()
            )
            try {
                val content = model.generateContent(prompt)
                println("############# Gemini generated content #############")
                println(content.text)
                println("##################################")
                onSuccess(content.text, content.usageMetadata?.totalTokenCount)
            } catch (exception: GoogleGenerativeAIException) {
                exception.printStackTrace()
                onFailure(exception.localizedMessage)
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