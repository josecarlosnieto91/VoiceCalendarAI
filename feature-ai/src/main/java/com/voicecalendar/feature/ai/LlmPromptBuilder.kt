package com.voicecalendar.feature.ai

/**
 * Builder for LLM system prompts used in event extraction.
 * Extracted from LlmRepositoryImpl for modularity.
 */
object LlmPromptBuilder {

    fun buildSystemPrompt(): String = buildString {
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
        appendLine()
        appendLine("=== TIME RULES ===")
        appendLine("- Use 24h format HH:mm")
        appendLine("- Default duration: 60 minutes if not specified")
        appendLine()
        appendLine("=== CATEGORY RULES ===")
        appendLine("Infer the category from context. One of: medical, work, travel, social, birthday, administrative, other")
        appendLine()
        appendLine("=== PRIORITY RULES ===")
        appendLine("Infer priority from language intensity. One of: urgent, high, normal, low")
        appendLine()
        appendLine("=== REMINDER RULES ===")
        appendLine("Generate appropriate reminder times in minutes BEFORE the event.")
        appendLine("Default reminders by category:")
        appendLine("- medical: [1440, 120, 30]")
        appendLine("- work: [30, 15]")
        appendLine("- travel: [2880, 1440, 120]")
        appendLine("- social: [1440, 60]")
        appendLine("- birthday: [10080, 1440]")
        appendLine("- administrative: [1440, 60]")
        appendLine("- other: [60, 15]")
    }
}
