package com.voicecalendar.feature.llm

/**
 * Builds system prompts for event extraction with different LLM providers.
 */
object LlmPromptBuilder {

    fun buildSystemPrompt(provider: String, locale: String = "en"): String = buildString {
        appendLine("You are a calendar event parser. Extract event details from natural language.")
        appendLine()
        appendLine("Return ONLY valid JSON without markdown wrapping. Use this exact structure:")
        appendLine("""{"title":"","description":"","date":"YYYY-MM-DD","start_time":"HH:mm","end_time":"HH:mm","location":"","reminder_minutes":15,"all_day":false}""")
        appendLine()
        appendLine("Rules:")
        appendLine("- Title is required. If unclear, summarize in max 5 words.")
        appendLine("- Date: if relative ('tomorrow', 'next monday'), use the actual date.")
        appendLine("- Time: 24h format. Infer end_time as start+1h if not specified.")
        appendLine("- If no specific time, set all_day=true and omit start_time/end_time.")
        appendLine("- reminder_minutes: 0=none, 10, 15, 30, 60, 1440 (1 day)")
        appendLine("- Location: only if explicitly mentioned.")
        appendLine("- Respond in the user's language: $locale")
    }
}
