package com.voicecalendar.data.repository

import com.voicecalendar.data.remote.api.LlmApi
import com.voicecalendar.data.remote.dto.LlmChatRequest
import com.voicecalendar.data.remote.dto.LlmMessage
import com.voicecalendar.domain.model.CalendarEventJson
import com.voicecalendar.domain.model.LlmConfig
import com.voicecalendar.domain.repository.LlmRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

class LlmRepositoryImpl @Inject constructor(
    private val llmApi: LlmApi
) : LlmRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun extractEventFromText(
        text: String,
        config: LlmConfig
    ): Result<CalendarEventJson> {
        return runCatching {
            val systemPrompt = buildPrompt()
            val request = LlmChatRequest(
                model = config.model,
                messages = listOf(
                    LlmMessage(role = "system", content = systemPrompt),
                    LlmMessage(role = "user", content = text)
                ),
                temperature = config.temperature,
                maxTokens = config.maxTokens
            )

            val response = llmApi.chatCompletion(config.endpointUrl, request)
            val content = response.choices.firstOrNull()?.message?.content
                ?: throw IllegalStateException("Empty LLM response")

            val cleaned = content
                .removePrefix("```json").removePrefix("```")
                .removeSuffix("```")
                .trim()

            val parsed = json.decodeFromString<CalendarEventJson>(cleaned)

            // Auto-generate reminders if LLM returned none
            val finalEvent = if (parsed.reminders.isEmpty()) {
                parsed.copy(reminders = generateDefaultReminders(parsed.category))
            } else parsed

            // Infer category from context if missing
            if (finalEvent.category.isBlank() || finalEvent.category == "other") {
                finalEvent.copy(category = inferCategoryFromText(text))
            } else finalEvent
        }
    }

    private fun buildPrompt(): String = buildString {
        appendLine("You are a precise calendar event parser. Extract event details from natural language.")
        appendLine()
        appendLine("IMPORTANT: Return ONLY valid JSON. No markdown, no explanation, no notes.")
        appendLine()
        appendLine("Use this EXACT schema:")
        appendLine("""{"title":"","date":"","time":"","duration_minutes":60,"location":"","category":"","priority":"","reminders":[]}""")
        appendLine()
        appendLine("=== DATE RULES ===")
        appendLine("- Resolve relative dates: 'tomorrow', 'next Monday', 'day after tomorrow', 'next week'")
        appendLine("- Today is ${java.time.LocalDate.now()}")
        appendLine("- Format dates as YYYY-MM-DD")
        appendLine("- For birthdays, infer the date if mentioned (e.g., 'Ana's birthday' on May 15)")
        appendLine("- If date is ambiguous, ask by outputting: {\"title\":\"CLARIFICATION_NEEDED\",\"date\":\"\",\"time\":\"\",\"duration_minutes\":60,\"location\":\"\",\"category\":\"\",\"priority\":\"\",\"reminders\":[]}")
        appendLine()
        appendLine("=== TIME RULES ===")
        appendLine("- Use 24h format HH:mm")
        appendLine("- Infer timezone from context if mentioned")
        appendLine("- Default duration: 60 minutes if not specified")
        appendLine("- If time is relative ('after lunch', 'in the evening'), use reasonable defaults:")
        appendLine("  * morning → 09:00")
        appendLine("  * afternoon → 14:00")
        appendLine("  * evening → 19:00")
        appendLine("  * night → 21:00")
        appendLine("  * lunch → 13:00")
        appendLine()
        appendLine("=== CATEGORY RULES ===")
        appendLine("Infer the category from context. One of:")
        appendLine("- medical: doctor, hospital, dentist, therapy, checkup, appointment, surgery, health, clinic")
        appendLine("- work: meeting, deadline, presentation, conference, interview, standup, review, office, client, project")
        appendLine("- travel: flight, trip, vacation, hotel, airport, journey, excursion, holiday, bus, train, plane")
        appendLine("- social: party, dinner, lunch, coffee, friends, hangout, gathering, celebration, night out")
        appendLine("- birthday: birthday, cumpleaños, anniversary of birth")
        appendLine("- administrative: tax, paperwork, visa, appointment, renewal, registration, bank")
        appendLine("- other: anything that doesn't fit above")
        appendLine()
        appendLine("=== PRIORITY RULES ===")
        appendLine("Infer priority from language intensity. One of:")
        appendLine("- urgent: ASAP, immediately, critical, urgent, as soon as possible, emergency, right now")
        appendLine("- high: important, must not miss, crucial, vital, key, deadline, due")
        appendLine("- normal: default for most events")
        appendLine("- low: casual, optional, if free, maybe, not important, whenever")
        appendLine()
        appendLine("=== REMINDER RULES ===")
        appendLine("Generate appropriate reminder times in minutes BEFORE the event.")
        appendLine("Default reminders by category (if user doesn't specify):")
        appendLine("- medical: [1440, 120, 30]  (1 day, 2 hours, 30 min)")
        appendLine("- work: [30, 15]")
        appendLine("- travel: [2880, 1440, 120]  (2 days, 1 day, 2 hours)")
        appendLine("- social: [1440, 60]  (1 day, 1 hour)")
        appendLine("- birthday: [10080, 1440]  (1 week, 1 day)")
        appendLine("- administrative: [1440, 60]  (1 day, 1 hour)")
        appendLine("- other: [60, 15]")
        appendLine()
        appendLine("Priority boost: add these extra reminders for high-priority events:")
        appendLine("- high: +[1440, 60]")
        appendLine("- urgent: +[120, 30, 5]")
        appendLine()
        appendLine("=== LOCATION RULES ===")
        appendLine("- If location is explicitly mentioned, include it")
        appendLine("- If it's virtual/online, set location to the platform name or URL")
        appendLine()
        appendLine("=== FINAL RULES ===")
        appendLine("- Never invent dates. If date is ambiguous, ask for clarification.")
        appendLine("- Never invent locations.")
        appendLine("- Keep title concise (max 10 words)")
        appendLine("- Duration: 60 min default. Infer from context: 'all day' = 480, 'full day' = 480, 'half day' = 240")
    }

    private fun generateDefaultReminders(category: String): List<Int> {
        return when (category.lowercase()) {
            "medical" -> listOf(1440, 120, 30)
            "work" -> listOf(30, 15)
            "travel" -> listOf(2880, 1440, 120)
            "social" -> listOf(1440, 60)
            "birthday" -> listOf(10080, 1440)
            "administrative" -> listOf(1440, 60)
            else -> listOf(60, 15)
        }
    }

    private fun inferCategoryFromText(text: String): String {
        val lower = text.lowercase()
        val keywords = mapOf(
            "medical" to listOf("doctor", "hospital", "dentist", "therapy", "checkup", "surgery", "health", "clinic", "medical", "appointment"),
            "work" to listOf("meeting", "deadline", "presentation", "conference", "interview", "standup", "review", "office", "client", "project", "work", "job"),
            "travel" to listOf("flight", "trip", "vacation", "hotel", "airport", "journey", "excursion", "holiday", "bus", "train", "plane", "travel"),
            "social" to listOf("party", "dinner", "lunch", "coffee", "friends", "hangout", "gathering", "celebration", "social"),
            "birthday" to listOf("birthday", "cumpleaños", "aniversario"),
            "administrative" to listOf("tax", "paperwork", "visa", "renewal", "registration", "bank", "administrative")
        )

        var bestCategory = "other"
        var maxScore = 0

        for ((category, words) in keywords) {
            val score = words.count { it in lower }
            if (score > maxScore) {
                maxScore = score
                bestCategory = category
            }
        }

        return bestCategory
    }
}
