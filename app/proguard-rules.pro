# VoiceCalendarAI ProGuard Rules
-keepattributes *Annotation*

# Kotlin Serialization
-keepclassmembers class kotlinx.serialization.json.** { *; }
-keep,includedescriptorclasses class com.voicecalendar.**$$serializer { *; }
-keepclassmembers class com.voicecalendar.** {
    *** Companion;
}
-keepclasseswithmembers class com.voicecalendar.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

