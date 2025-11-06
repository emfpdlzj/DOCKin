package com.project.dockin.data.sync
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.project.dockin.data.api.Network
import com.project.dockin.data.api.WorkLogApi
import com.project.dockin.data.db.AppDb

class SyncWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val db = AppDb.get(applicationContext)
        val dao = db.workLogDao()
        val api = Network.retrofit(applicationContext).create(WorkLogApi::class.java)

        val pendings = dao.findPending()
        for (p in pendings) {
            runCatching {
                val res = api.create(WorkLogApi.CreateReq(p.title, p.content, null))
                dao.mark(p.localId, "synced", res.log_id)
            }.onFailure {
                dao.mark(p.localId, "failed", null)    // 다음 번에 재시도
            }
        }
        return Result.success()
    }
}