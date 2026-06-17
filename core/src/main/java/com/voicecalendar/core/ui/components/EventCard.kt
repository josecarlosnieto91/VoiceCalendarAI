package com.voicecalendar.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.voicecalendar.core.ui.theme.CategoryAdministrative
import com.voicecalendar.core.ui.theme.CategoryBirthday
import com.voicecalendar.core.ui.theme.CategoryMedical
import com.voicecalendar.core.ui.theme.CategoryOther
import com.voicecalendar.core.ui.theme.CategorySocial
import com.voicecalendar.core.ui.theme.CategoryTravel
import com.voicecalendar.core.ui.theme.CategoryWork
import com.voicecalendar.core.ui.theme.PriorityHigh
import com.voicecalendar.core.ui.theme.PriorityLow
import com.voicecalendar.core.ui.theme.PriorityNormal
import com.voicecalendar.core.ui.theme.PriorityUrgent
import com.voicecalendar.core.util.DateUtils
import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.model.EventCategory
import com.voicecalendar.domain.model.EventPriority

/**
 * Redesigned event card with category chip, priority icon, and relative time display.
 */
@Composable
fun EventCard(
    event: CalendarEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Title row with priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Priority icon
                val priorityColor = when (event.priority) {
                    EventPriority.URGENT -> PriorityUrgent
                    EventPriority.HIGH -> PriorityHigh
                    EventPriority.NORMAL -> PriorityNormal
                    EventPriority.LOW -> PriorityLow
                }
                if (event.priority != EventPriority.NORMAL) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = event.priority.displayName,
                        tint = priorityColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Category chip
                if (event.category != EventCategory.OTHER) {
                    Spacer(modifier = Modifier.width(8.dp))
                    val categoryColor = categoryColor(event.category)
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                event.category.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = categoryColor
                        )
                    )
                }
            }

            // Description
            if (event.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Info row: date/time, relative time, location
            Row(verticalAlignment = Alignment.CenterVertically) {
                val eDate = event.date
                val eStart = event.startTime
                if (eDate != null) {
                    InfoChip(
                        icon = Icons.Outlined.CalendarMonth,
                        text = DateUtils.formatShortDate(eDate)
                    )
                }
                if (eStart != null) {
                    InfoChip(
                        icon = Icons.Outlined.Schedule,
                        text = DateUtils.formatTime(eStart)
                    )
                }
                if (event.location.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    InfoChip(icon = Icons.Outlined.LocationOn, text = event.location)
                }
            }
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun categoryColor(category: EventCategory): Color {
    return when (category) {
        EventCategory.MEDICAL -> CategoryMedical
        EventCategory.WORK -> CategoryWork
        EventCategory.TRAVEL -> CategoryTravel
        EventCategory.SOCIAL -> CategorySocial
        EventCategory.BIRTHDAY -> CategoryBirthday
        EventCategory.ADMINISTRATIVE -> CategoryAdministrative
        EventCategory.OTHER -> CategoryOther
    }
}
