package net.aoba.api;

import net.aoba.cmd.Command;
import net.aoba.module.Module;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.List;
import java.util.Optional;

/**
 * The IAddon interface defines the structure for creating addons in the Aoba system.
 * Implementing this interface allows the addon to be properly initialized and
 * to provide necessary modules and commands to the system.
 */
public interface IAddon {

    /**
     * This method is called when the addon is being initialized.
     * Implement any setup logic necessary for the addon here.
     */
    void onInitialize();

    /**
     * Returns a list of modules provided by this addon.
     * Modules encapsulate specific functionalities or features.
     *
     * @return a list of Module objects
     */
    List<Module> modules();

    /**
     * Returns a list of commands provided by this addon.
     * Commands are used to interact with the addon via a command-line interface.
     *
     * @return a list of Command objects
     */
    List<Command> commands();

    /**
     * Returns the name of the addon.
     * This is used to identify the addon within the system.
     *
     * @return the name of the addon
     */
    String getName();


    /**
     * Returns the id of the addon.
     * Used to get mod metadata.
     *
     * @return the id of the addon
     */
    String getId();


    /**
     * Returns the author of the addon.
     * This indicates who developed the addon.
     *
     * @return the author of the addon
     */
    String getAuthor();

    /**
     * Returns the version of the addon.
     * This can be useful for compatibility checks and updates.
     *
     * @return the version of the addon
     */
    default String getVersion() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(getId());
        if (modContainer.isPresent()) {
            ModMetadata metadata = modContainer.get().getMetadata();
            return metadata.getVersion().getFriendlyString();
        }
        return "Unknown";
    }
}
