package io.github.warleysr.ankipadroid

import org.json.JSONArray
import org.json.JSONObject

class ConfigUtils {
    companion object {
        private var config: JSONObject? = null
        private var languages: JSONObject? = null
        private var regions: JSONArray? = null
        private var availableLanguages = HashMap<String, String>()
        private var availableVoices = HashMap<String, String>()
        private var availableRegions = ArrayList<String>()

        fun initialize(configJson: String) {
            config = JSONObject(configJson)
            languages = config!!.getJSONObject("languages")
            regions = config!!.getJSONArray("regions")

            for (key in languages!!.keys()) {
                val code = languages!!.getJSONArray(key)[0].toString()
                val voice = languages!!.getJSONArray(key)[0].toString()

                availableLanguages[code] = key
                availableVoices[code] = voice
            }

            for (idx in 0..<regions!!.length()) {
                availableRegions.add(regions!![idx].toString())
            }
        }

        fun getAvailableLanguages(): HashMap<String, String> {
            return availableLanguages
        }

        fun getAvailableRegions(): ArrayList<String> {
            return availableRegions
        }

        fun getVoiceByLanguage(language: String): String? {
            return availableVoices[language]
        }
    }
}