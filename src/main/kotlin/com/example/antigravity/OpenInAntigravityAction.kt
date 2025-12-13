package com.example.antigravity

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

class OpenInAntigravityAction : AnAction() {

    companion object {
        private val isWindows = System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")
        
        // Windows default fallback (User specific + Standard)
        private val WIN_DEFAULTS = listOf(
            "D:\\Program Files (x86)\\Antigravity\\bin\\antigravity.cmd",
            "C:\\Program Files\\Antigravity\\bin\\antigravity.cmd",
            "C:\\Program Files (x86)\\Antigravity\\bin\\antigravity.cmd",
            System.getProperty("user.home") + "\\AppData\\Local\\Programs\\Antigravity\\bin\\antigravity.cmd"
        )

        // Mac/Linux default fallback
        private val MAC_DEFAULTS = listOf(
            "/usr/local/bin/antigravity",
            "/Applications/Antigravity.app/Contents/Resources/app/bin/antigravity"
        )
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project? = e.project
        val virtualFile: VirtualFile? = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val editor = e.getData(CommonDataKeys.EDITOR)

        if (project == null || virtualFile == null) {
            return
        }

        val filePath = virtualFile.path
        val line = if (editor != null) editor.caretModel.primaryCaret.logicalPosition.line + 1 else 1
        val gotoArg = "$filePath:$line"
        
        val antigravityCmd = findAntigravityCmd()
        
        if (antigravityCmd == null) {
            Notifications.Bus.notify(
                Notification(
                    "Antigravity Notification Group",
                    "Antigravity Not Found",
                    "Could not find 'antigravity' executable in PATH or standard locations.",
                    NotificationType.ERROR
                ),
                project
            )
            return
        }
        
        try {
            val processBuilder = ProcessBuilder(antigravityCmd, "--goto", gotoArg)
            processBuilder.start()
            
            // Optional success notification (can be noisy, maybe comment out for prod)
             Notifications.Bus.notify(
                 Notification(
                     "Antigravity Notification Group",
                     "Opening in Antigravity",
                     "Executing: $antigravityCmd --goto $gotoArg",
                     NotificationType.INFORMATION
                 ),
                 project
             )
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
    
    private fun findAntigravityCmd(): String? {
        val binaryName = if (isWindows) "antigravity.cmd" else "antigravity"
        
        // 1. Check PATH
        val pathEnv = System.getenv("PATH") ?: ""
        val pathSeparator = File.pathSeparator
        val pathDirs = pathEnv.split(pathSeparator)
        
        for (dir in pathDirs) {
            val cmdFile = File(dir, binaryName)
            if (cmdFile.exists() && cmdFile.canExecute()) {
                return cmdFile.absolutePath
            }
        }
        
        // 2. Check Standard Locations
        val defaults = if (isWindows) WIN_DEFAULTS else MAC_DEFAULTS
        for (path in defaults) {
            val file = File(path)
            if (file.exists()) {
                // Ensure executable on Unix
                if (!isWindows && !file.canExecute()) {
                    file.setExecutable(true)
                }
                return file.absolutePath
            }
        }
        
        return null
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null && e.getData(CommonDataKeys.VIRTUAL_FILE) != null
    }
}
