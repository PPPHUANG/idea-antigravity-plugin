package com.github.ppphuang.idea_antigravity_plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import java.io.File
import java.util.Locale
import com.intellij.notification.NotificationAction
import com.intellij.openapi.options.ShowSettingsUtil

class OpenInAntigravityAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project? = e.project
        val virtualFile: VirtualFile? = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val editor = e.getData(CommonDataKeys.EDITOR)

        if (project == null || virtualFile == null) {
            return
        }

        val filePath = virtualFile.path
        val line = if (editor != null) editor.caretModel.primaryCaret.logicalPosition.line + 1 else 1
        val column = if (editor != null) editor.caretModel.primaryCaret.logicalPosition.column + 1 else 1

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
        
        // 2. Check Standard Locations
        
        try {
            val command = when {
                System.getProperty("os.name").lowercase().contains("mac") -> {
                    // Use CLI directly on Mac as well, avoiding URL scheme issues
                    arrayOf(antigravityCmd, "--goto", "$filePath:$line:$column")
                }
                System.getProperty("os.name").lowercase().contains("windows") -> {
                    arrayOf("cmd", "/c", antigravityCmd, "--goto", "$filePath:$line:$column")
                }
                else -> {
                    arrayOf(antigravityCmd, "--goto", "$filePath:$line:$column")
                }
            }

            val processBuilder = ProcessBuilder(*command)
            processBuilder.start()
            
        } catch (ex: Exception) {
            Notifications.Bus.notify(
                Notification(
                    "Antigravity Notification Group",
                    "Failed to Open Antigravity",
                    "Error: ${ex.message}",
                    NotificationType.ERROR
                ),
                project
            )
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null && e.getData(CommonDataKeys.VIRTUAL_FILE) != null
    }

    override fun getActionUpdateThread(): com.intellij.openapi.actionSystem.ActionUpdateThread {
        return com.intellij.openapi.actionSystem.ActionUpdateThread.BGT
    }
}
