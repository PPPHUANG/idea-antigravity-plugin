package com.github.ppphuang.idea_antigravity_plugin

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.openapi.application.ApplicationManager

@State(
    name = "com.github.ppphuang.idea_antigravity_plugin.AntigravitySettingsState",
    storages = [Storage("AntigravityPluginSettings.xml")]
)
class AntigravitySettingsState : PersistentStateComponent<AntigravitySettingsState> {

    var antigravityPath: String = ""

    companion object {
        val instance: AntigravitySettingsState
            get() = ApplicationManager.getApplication().getService(AntigravitySettingsState::class.java)
    }

    override fun getState(): AntigravitySettingsState {
        return this
    }

    override fun loadState(state: AntigravitySettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
