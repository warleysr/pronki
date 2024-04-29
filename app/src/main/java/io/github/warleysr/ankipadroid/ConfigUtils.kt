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
        private var availableLanguagesSorted: List<Pair<String, String>>? = null

        fun initialize(configJson: String) {
            config = JSONObject(configJson)
            languages = config!!.getJSONObject("languages")
            regions = config!!.getJSONArray("regions")

            for (key in languages!!.keys()) {
                val code = languages!!.getJSONArray(key)[0].toString()
                val voice = languages!!.getJSONArray(key)[1].toString()

                availableLanguages[code] = key
                availableVoices[code] = voice
            }

            for (idx in 0..<regions!!.length()) {
                availableRegions.add(regions!![idx].toString())
            }

            availableRegions.sort()
            availableLanguagesSorted = availableLanguages.map {Pair(it.key, it.value)}
            availableLanguagesSorted = availableLanguagesSorted!!.sortedBy { pair -> pair.second }
        }

        fun getAvailableLanguages(): HashMap<String, String> {
            return availableLanguages
        }

        fun getAvailableLanguagesSorted(): List<Pair<String, String>>? {
            return availableLanguagesSorted
        }

        fun getAvailableRegions(): ArrayList<String> {
            return availableRegions
        }

        fun getVoiceByLanguage(language: String): String {
            return availableVoices[language] ?: return "en-US-AvaMultilingualNeural"
        }
    }
}