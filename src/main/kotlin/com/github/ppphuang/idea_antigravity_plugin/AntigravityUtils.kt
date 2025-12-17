package com.github.ppphuang.idea_antigravity_plugin

import java.io.File
import java.util.Locale

object AntigravityUtils {
    val isWindows = System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")

    // Windows default fallback (User specific + Standard)
    private val WIN_DEFAULTS = listOf(
        System.getProperty("user.home") + "\\AppData\\Local\\Programs\\Antigravity\\bin\\antigravity.cmd"
    )

    // Mac/Linux default fallback
    private val MAC_DEFAULTS = listOf(
        "/usr/local/bin/antigravity",
        "/Applications/Antigravity.app/Contents/Resources/app/bin/antigravity"
    )

    fun findAntigravityCmd(): String? {
        // 0. Check Configured Path
        val settingsPath = AntigravitySettingsState.instance.antigravityPath
        if (settingsPath.isNotBlank()) {
            val file = File(settingsPath)
            if (file.exists()) {
                return file.absolutePath
            }
        }

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

        // 1.5 Try 'where' or 'which' command
        try {
            val whereCmd = if (isWindows) "where" else "which"
            val process = ProcessBuilder(whereCmd, "antigravity").start()
            val ret = process.waitFor()
            if (ret == 0) {
                val output = process.inputStream.bufferedReader().readText().trim().lines().firstOrNull()
                if (!output.isNullOrBlank()) {
                    val file = File(output)
                    if (file.exists()) return file.absolutePath
                }
            }
        } catch (ignored: Exception) {}

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
}
