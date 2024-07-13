/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * A class to represent a generic module.
 */
package net.aoba.module;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.mixin.interfaces.IMinecraftClient;
import net.aoba.settings.Setting;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private String name;
    private String description;
    private Category category;
    private boolean state;

    protected KeybindSetting keyBind;
    private List<Setting<?>> settings = new ArrayList<Setting<?>>();

    protected final MinecraftClient MC = AobaClient.MC;
    protected final IMinecraftClient IMC = AobaClient.IMC;

    /**
     * Default constructor for the module, initializing it with a
     * default keybind setting.
     */
    public Module() {
        this(new KeybindSetting("default.key", "Default Key", InputUtil.UNKNOWN_KEY));
    }

    /**
     * Constructor for the module, allowing for a custom keybind.
     *
     * @param keyName        The identifier for the keybind setting.
     * @param keyDescription A description for the keybind setting.
     * @param keyCode        The key code corresponding to the keybind.
     */
    public Module(String keyName, String keyDescription, int keyCode) {
        this(new KeybindSetting(keyName, keyDescription, InputUtil.fromKeyCode(keyCode, 0)));
    }

    /**
     * Constructor for the module, initializing it with a specified keybind.
     *
     * @param keyBind The {@link KeybindSetting} to use for this module.
     */
    public Module(KeybindSetting keyBind) {
        this.keyBind = keyBind;
        this.addSetting(keyBind);
        SettingManager.registerSetting(this.keyBind, Aoba.getInstance().settingManager.modulesContainer);
    }

    /**
     * Retrieves the name of the module.
     *
     * @return The name of the module.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the module.
     *
     * @param name The name to assign to the module.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the description of the module.
     *
     * @return The description of the module.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the module.
     *
     * @param description The description to assign to the module.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the category of the module.
     *
     * @return The {@link Module.Category} assigned to this module.
     */
    public Module.Category getCategory() {
        return this.category;
    }

    /**
     * Sets the category for the module.
     *
     * @param category The {@link Module.Category} to assign to this module,
     *                 categorizing it for organizational purposes.
     */
    public void setCategory(Module.Category category) {
        this.category = category;
    }

    /**
     * Retrieves the keybind associated with the module.
     *
     * @return The {@link KeybindSetting} that represents the keybinding
     *         for this module.
     */
    public KeybindSetting getBind() {
        return this.keyBind;
    }

    /**
     * Gets the current state of the module.
     *
     * @return {@code true} if the module is enabled;
     *         {@code false} otherwise.
     */
    public boolean getState() {
        return this.state;
    }

    /**
     * Sets the state of the module, enabling or disabling it.
     * <p>
     * This method also triggers the corresponding actions by calling
     * {@link #onEnable()} or {@link #onDisable()} and invokes
     * {@link #onToggle()} to handle any toggle-related logic.
     * </p>
     *
     * @param state The desired state of the module; {@code true} to enable,
     *              {@code false} to disable.
     */
    public void setState(boolean state) {
        if (this.state = state) return;

        this.onToggle();

        if (state) {
            this.onEnable();
            this.state = true;
        } else {
            this.onDisable();
            this.state = false;
        }
    }

    /**
     * Adds a setting to the module.
     *
     * @param setting The {@link Setting} to be added to the module's settings list.
     */
    public void addSetting(Setting<?> setting) {
        this.settings.add(setting);
    }

    /**
     * Retrieves the list of settings associated with the module.
     *
     * @return A {@link List} of {@link Setting} objects that belong to this module.
     */
    public List<Setting<?>> getSettings() {
        return this.settings;
    }

    /**
     * Checks if the module has any settings defined.
     *
     * @return {@code true} if the module has one or more settings;
     * {@code false} otherwise.
     */
    public boolean hasSettings() {
        return !this.settings.isEmpty();
    }


    /**
     * Called when the module is disabled.
     * <p>
     * This method should contain the logic that needs to be executed
     * when the module is turned off, such as stopping certain processes
     * or resetting states.
     * </p>
     */
    public abstract void onDisable();

    /**
     * Called when the module is enabled.
     * <p>
     * This method should contain the logic that needs to be executed
     * when the module is activated, such as starting certain processes
     * or modifying game states.
     * </p>
     */
    public abstract void onEnable();

    /**
     * Called when the module's state is toggled.
     * <p>
     * This method serves as a general handler that is invoked
     * whenever the module is enabled or disabled. It can contain
     * logic that needs to be executed regardless of the current state.
     * </p>
     */
    public abstract void onToggle();

    /**
     * Checks if a specific key is currently pressed.
     *
     * @param button The key code of the key to check.
     *               A value of -1 indicates an invalid key.
     * @return {@code true} if the specified key is pressed;
     * {@code false} otherwise. If the button is less than 10
     * or equals -1, it will return {@code false}.
     */
    public boolean isKeyPressed(int button) {
        if (button == -1)
            return false;

        if (button < 10) // check
            return false;

        return InputUtil.isKeyPressed(MC.getWindow().getHandle(), button);
    }


    /**
     * Toggles the state of the module between enabled and disabled.
     * <p>
     * If the module is currently enabled, it will call the {@link #onDisable()} method
     * to handle any necessary actions during disabling. Conversely, if the module is
     * disabled, it will call the {@link #onEnable()} method to perform actions upon enabling.
     * </p>
     * The state is then updated accordingly.
     */
    public void toggle() {
        if (this.state) {
            this.onDisable();
        } else {
            this.onEnable();
        }
        this.setState(!this.getState());
    }


    /**
     * Returns the current status of the module as a string.
     */
    public String getStatus() {
        return this.state ? "Enabled" : "Disabled";
    }

    /**
     * Retrieves the current keybind as a string.
     */
    public String getKeyBindDisplayName() {
        return keyBind.displayName;
    }

    /**
     * Resets the module settings to their default values.
     */
    public void resetSettings() {
        for (Setting<?> setting : settings) {
            setting.resetToDefault();
        }
    }


    /**
     * Checks if the module belongs to a specified category.
     *
     * @param category The {@link Module.Category} to check against this module's category.
     * @return {@code true} if the module is in the specified category;
     *         {@code false} otherwise.
     */
    public final boolean isCategory(Module.Category category) {
        return category == this.category;
    }

    /**
     * Annotation to provide metadata about a module.
     * <p>
     * This annotation can be used to specify module information such as
     * name, description, category, and keybind.
     * </p>
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ModInfo {
        /**
         * @return The name of the module.
         */
        String name();

        /**
         * @return A brief description of the module's functionality.
         */
        String description();

        /**
         * @return The {@link Module.Category} to which the module belongs.
         */
        Module.Category category();

        /**
         * @return The keybind integer associated with the module.
         */
        int bind();
    }

    /**
     * Enum representing the various categories a module can belong to.
     */
    public static enum Category {
        Combat(), Movement(), Render(), World(), Misc();

        Module module;

        private Category() {
        }
    }
}
