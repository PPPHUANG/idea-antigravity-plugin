package com.github.ppphuang.idea_antigravity_plugin

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import com.intellij.openapi.options.ConfigurationException
import java.io.File

class AntigravitySettingsConfigurable : Configurable {

    private var mySettingsComponent: JPanel? = null
    private var myPathField: TextFieldWithBrowseButton? = null

    override fun getDisplayName(): String {
        return "Antigravity"
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return myPathField
    }

    override fun createComponent(): JComponent? {
        myPathField = TextFieldWithBrowseButton()
        mySettingsComponent = FormBuilder.createFormBuilder()
            .addLabeledComponent("Antigravity executable path:", myPathField!!)
            .addComponentFillVertically(JPanel(), 0)
            .panel
        return mySettingsComponent
    }

    override fun isModified(): Boolean {
        val settings = AntigravitySettingsState.instance
        return myPathField!!.text != settings.antigravityPath
    }

    override fun apply() {
        val path = myPathField!!.text
        if (path.isNotEmpty()) {
             val file = File(path)
             if (!file.exists() || !file.isFile) {
                 throw ConfigurationException("Invalid path: File does not exist or is not a file.")
             }
        }
        val settings = AntigravitySettingsState.instance
        settings.antigravityPath = path
    }

    override fun reset() {
        val settings = AntigravitySettingsState.instance
        myPathField!!.text = settings.antigravityPath
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
        myPathField = null
    }
}
