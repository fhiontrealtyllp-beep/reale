package com.example.mytestapp.feature.search.data.remote

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage

class AppWriteProvider(context: Context) {
    val client: Client = Client(context.applicationContext)
        .setEndpoint(AppWriteConstants.ENDPOINT)
        .setProject(AppWriteConstants.PROJECT_ID)
        .setSelfSigned(true)

    val account: Account = Account(client)
    val databases: Databases = Databases(client)
    val storage: Storage = Storage(client)
}
