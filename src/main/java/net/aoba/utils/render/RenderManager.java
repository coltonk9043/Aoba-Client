package net.aoba.utils.render;

import net.aoba.utils.render.core.IRenderer;
import net.aoba.utils.render.core.RenderContext;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;

public class RenderManager {
    private static final RenderManager INSTANCE = new RenderManager();
    
    private final NewRender2D render2D;
    private final NewRender3D render3D;
    private final List<IRenderer> activeRenderers;
    
    private RenderManager() {
        this.render2D = new NewRender2D();
        this.render3D = new NewRender3D();
        this.activeRenderers = new ArrayList<>();
    }
    
    public static RenderManager getInstance() {
        return INSTANCE;
    }
    
    public NewRender2D get2D() {
        return render2D;
    }
    
    public NewRender3D get3D() {
        return render3D;
    }
    
    public void beginFrame() {
        NewRender2D.clearStorageFrame();
        NewRender3D.clearStorageFrame();
        activeRenderers.clear();
    }
    
    public void endFrame(DrawContext context) {
        for (IRenderer renderer : activeRenderers) {
            if (renderer.isBuilding()) {
                renderer.end();
            }
            renderer.render(context);
        }
        activeRenderers.clear();
    }
    
    public void registerRenderer(IRenderer renderer) {
        if (!activeRenderers.contains(renderer)) {
            activeRenderers.add(renderer);
        }
    }
    
    public RenderContext createContext(DrawContext drawContext, float tickDelta) {
        return new RenderContext(drawContext, tickDelta);
    }
    
    public void begin2D() {
        render2D.begin();
        registerRenderer(render2D);
    }
    
    public void end2D() {
        if (render2D.isBuilding()) {
            render2D.end();
        }
    }
    
    public void begin3D() {
        render3D.begin();
        registerRenderer(render3D);
    }
    
    public void end3D() {
        if (render3D.isBuilding()) {
            render3D.end();
        }
    }
}