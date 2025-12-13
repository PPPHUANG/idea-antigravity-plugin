package com.example.antigravity

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications

class OpenInAntigravityAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project? = e.project
        val virtualFile: VirtualFile? = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val editor = e.getData(CommonDataKeys.EDITOR)

        if (project == null || virtualFile == null) {
            return
        }

        // IntelliJ uses forward slashes even on Windows for VirtualFile.path
        val filePath = virtualFile.path
        
        // 1-based line number; default to 1 if no editor (e.g. invoked from project view)
        val line = if (editor != null) editor.caretModel.primaryCaret.logicalPosition.line + 1 else 1

        val encodedPath = URLEncoder.encode(filePath, StandardCharsets.UTF_8.toString()).replace("+", "%20")
        
        // Construct the URI
        // Scheme: antigravity://open?file=<path>&line=<line>
        val url = "antigravity://open?file=$encodedPath&line=$line"
        
        // DEBUG: Show notification and log to console
        println("Antigravity: Action triggered for $url")
        
        Notifications.Bus.notify(
            Notification(
                "Antigravity Notification Group",
                "Opening Antigravity",
                "URL: $url",
                NotificationType.INFORMATION
            ),
            project
        )
        
        BrowserUtil.browse(url)
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = project != null && file != null
    }
}
