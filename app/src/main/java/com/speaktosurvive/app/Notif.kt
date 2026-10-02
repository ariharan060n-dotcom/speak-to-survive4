package com.speaktosurvive.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat

object Notif {
    const val CH_GUARD = "guard"
    const val ID_GUARD = 1

    fun ensureChannels(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CH_GUARD) == null) {
            val ch = NotificationChannel(
                CH_GUARD, "Protection status", NotificationManager.IMPORTANCE_LOW
            )
            ch.description = "Shown while Speak to Survive is watching for your emergency triggers"
            ch.setShowBadge(false)
            nm.createNotificationChannel(ch)
        }
    }

    fun build(ctx: Context, text: String, sosActive: Boolean): Notification {
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val open = PendingIntent.getActivity(
            ctx, 0, Intent(ctx, MainActivity::class.java), flags
        )
        val b = NotificationCompat.Builder(ctx, CH_GUARD)
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setContentTitle(if (sosActive) "SOS ACTIVE" else "Speak to Survive")
            .setContentText(text)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(open)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)

        if (sosActive) {
            val safe = PendingIntent.getService(
                ctx, 2,
                Intent(ctx, GuardService::class.java).setAction(GuardService.ACTION_CANCEL_SOS),
                flags
            )
            b.addAction(0, "I'M SAFE", safe)
            b.setColor(Color.parseColor("#DC2626"))
        } else {
            val stop = PendingIntent.getService(
                ctx, 1,
                Intent(ctx, GuardService::class.java).setAction(GuardService.ACTION_STOP),
                flags
            )
            b.addAction(0, "Stop protection", stop)
        }
        return b.build()
    }
}
