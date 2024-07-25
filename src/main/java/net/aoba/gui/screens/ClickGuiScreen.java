package net.aoba.gui.screens;

import net.aoba.Aoba;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {
    private final MainMenuScreen parent;
    
    public ClickGuiScreen(MainMenuScreen parent) {
        super(Text.of("ClickGUI Screen"));
        
        this.parent = parent;
    }
    
    public void init() {
        super.init();

        Aoba.getInstance().hudManager.setClickGuiOpen(true);
    }
}