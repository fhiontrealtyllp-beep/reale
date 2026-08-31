package com.realeapp.feature.search.data.remote

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage

object AppWriteProvider {

    private var _context: Context? = null

    lateinit var client: Client
        private set

    val account: Account by lazy { Account(client) }
    val databases: Databases by lazy { Databases(client) }
    val storage: Storage by lazy { Storage(client) }

    fun init(context: Context) {
        if (_context != null) return
        _context = context.applicationContext
        client = Client(context.applicationContext)
            .setEndpoint(AppWriteConstants.ENDPOINT)
            .setProject(AppWriteConstants.PROJECT_ID)
            .setSelfSigned(true)
    }
}
