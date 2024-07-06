package net.aoba.api;

import net.aoba.cmd.Command;
import net.aoba.module.Module;

import java.util.List;

public interface IAddon {
    void onIntialize();
    List<Module> modules();
    List<Command> commands();
}
