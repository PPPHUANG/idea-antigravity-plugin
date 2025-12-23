package com.github.ppphuang.idea_antigravity_plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.notification.NotificationAction
import com.intellij.openapi.options.ShowSettingsUtil

class OpenProjectInAntigravityAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return

        val projectPath = project.basePath ?: return
        
        val antigravityCmd = AntigravityUtils.findAntigravityCmd()
        
        if (antigravityCmd == null) {
            val notification = Notification(
                "Antigravity Notification Group",
                "Antigravity Not Found",
                "Could not find 'antigravity' executable in PATH or standard locations.",
                NotificationType.ERROR
            )
            notification.addAction(NotificationAction.createSimple("Open Settings") {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, AntigravitySettingsConfigurable::class.java)
            })
            Notifications.Bus.notify(notification, project)
            return
        }

        try {
            val command = when {
                System.getProperty("os.name").lowercase().contains("windows") -> {
                    // Windows might need cmd /c
                   arrayOf("cmd", "/c", antigravityCmd, projectPath)
                }
                else -> {
                    arrayOf(antigravityCmd, projectPath)
                }
            }

            val processBuilder = ProcessBuilder(*command)
            processBuilder.start()

        } catch (ex: Exception) {
            Notifications.Bus.notify(
                Notification(
                    "Antigravity Notification Group",
                    "Failed to Open Project in Antigravity",
                    "Error: ${ex.message}",
                    NotificationType.ERROR
                ),
                project
            )
        }
    }

    override fun update(e: AnActionEvent) {
        // Only enable if there is a project open
        e.presentation.isEnabledAndVisible = e.project != null && e.project?.basePath != null
    }

    override fun getActionUpdateThread(): com.intellij.openapi.actionSystem.ActionUpdateThread {
        return com.intellij.openapi.actionSystem.ActionUpdateThread.BGT
    }
}
