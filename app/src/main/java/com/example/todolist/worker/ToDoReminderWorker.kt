package com.example.todolist.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todolist.R

class ToDoReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val taskName = inputData.getString(nameKey)

        makeTaskReminderNotification(
            applicationContext.resources.getString(R.string.remember_to_finish, taskName),
            applicationContext
        )

        return Result.success()
    }

    companion object {
        const val nameKey = "NAME"
    }
}
