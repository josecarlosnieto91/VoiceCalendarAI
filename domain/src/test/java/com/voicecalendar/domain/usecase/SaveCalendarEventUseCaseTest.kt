package com.voicecalendar.domain.usecase

import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.repository.CalendarRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveCalendarEventUseCaseTest {

    private val calendarRepository: CalendarRepository = mockk()
    private lateinit var useCase: SaveCalendarEventUseCase

    @Before
    fun setup() {
        useCase = SaveCalendarEventUseCase(calendarRepository)
    }

    @Test
    fun `given new event, when saved, then returns saved event with id`() = runTest {
        val event = CalendarEvent(title = "Test Event")
        coEvery { calendarRepository.saveEvent(any()) } returns Result.success(
            event.copy(eventId = 1L)
        )

        val result = useCase(event)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.eventId == 1L)
    }

    @Test
    fun `given existing event, when updated, then returns updated event`() = runTest {
        val event = CalendarEvent(title = "Updated", eventId = 1L)
        coEvery { calendarRepository.updateEvent(any()) } returns Result.success(event)

        val result = useCase(event)

        assertTrue(result.isSuccess)
    }
}
