package com.voicecalendar.feature.calendar

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Manages Google Calendar sync and account selection.
 */
class CalendarSyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val ACCOUNT_TYPE_GOOGLE = "com.google"
        const val AUTHORITY_CALENDAR = "com.android.calendar"
    }

    fun getGoogleAccounts(): List<Account> {
        val accountManager = AccountManager.get(context)
        return accountManager.getAccountsByType(ACCOUNT_TYPE_GOOGLE).toList()
    }

    fun requestSync(account: Account) {
        val bundle = Bundle().apply {
            putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        }
        ContentResolver.requestSync(account, AUTHORITY_CALENDAR, bundle)
    }
}
