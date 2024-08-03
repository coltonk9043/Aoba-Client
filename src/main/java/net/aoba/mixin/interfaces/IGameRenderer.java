package net.aoba.mixin.interfaces;

import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public interface IGameRenderer {
    @Accessor("programs")
    Map<String, ShaderProgram> getPrograms();
}
