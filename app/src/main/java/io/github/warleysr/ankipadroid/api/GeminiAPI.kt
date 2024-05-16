package io.github.warleysr.ankipadroid.api

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeminiAPI {

    companion object {

        fun generateContent(apiKey: String, prompt: String, onSuccess: (String?) -> Unit) {
            val model = GenerativeModel(modelName = "gemini-pro", apiKey = apiKey)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val content = model.generateContent(prompt)
                    onSuccess(content.text)
                } catch (exception: GoogleGenerativeAIException) {
                    println("Failure")
                }
            }
        }
    }
}