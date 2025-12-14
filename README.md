# Switch2Antigravity

A productivity plugin for IntelliJ IDEA that seamlessly integrates with **Antigravity**. It allows you to open your current file directly in Antigravity, automatically navigating to the specific line and column where your cursor is positioned.

## Features

-   **Open in Antigravity**: Quickly open the current file in the Antigravity editor.
-   **Precise Navigation**: Automatically jumps to the exact line and column of your cursor.
-   **Configurable Path**: Support for custom Antigravity executable paths if it's not in the default location.
-   **Smart Detection**: Automatically detects Antigravity installation in standard locations or system PATH.

## Installation

1.  Clone this repository.
2.  Build the plugin using the Gradle wrapper:
    ```bash
    ./gradlew buildPlugin
    ```
3.  In IntelliJ IDEA, go to `Settings/Preferences` -> `Plugins` -> `Gear Icon` -> `Install Plugin from Disk...`.
4.  Select the generated ZIP file from the `build/distributions/` directory.

## Usage

You can launch Antigravity using any of the following methods:

-   **Right-Click Menu**: Right-click on any file in the Project View or Editor text area and select **"Open in Antigravity"**.
-   **Keyboard Shortcut**: Press `Shift + Alt + A` (default) to open the current file.
-   **Search Actions**: Press `Cmd/Ctrl + Shift + A`, type "Open in Antigravity", and press Enter.

## Configuration

If Antigravity is installed in a non-standard location, you can manually configure the executable path:

1.  Go to `Settings` (Windows/Linux) or `Preferences` (macOS).
2.  Navigate to **Antigravity** in the settings tree (usually under Tools or at the root level).
3.  In the "Antigravity executable path" field, enter or browse for the `antigravity` executable (or `antigravity.cmd` on Windows).
4.  Click **Apply** or **OK**.
