package net.aoba.utils.render;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import net.aoba.utils.render.core.IRenderer;
import net.aoba.utils.render.core.RenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

public class RenderManager {
	private static final RenderManager INSTANCE = new RenderManager();
	public static final Matrix4f projection = new Matrix4f();
	public static final Matrix4f view = new Matrix4f();
	public static Vec3d center;

	private final Render2D render2D;
	private final Render3D render3D;
	private final List<IRenderer> activeRenderers;

	private RenderManager() {
		this.render2D = new Render2D();
		this.render3D = new Render3D();
		this.activeRenderers = new ArrayList<>();
	}

	public static void updateRenderProperties(Matrix4f proj, Matrix4f view) {
		projection.set(proj);

		Matrix4f invProjection = new Matrix4f(projection).invert();
		Matrix4f invView = new Matrix4f(view).invert();

		Vector4f center4 = new Vector4f(0, 0, 0, 1).mul(invProjection).mul(invView);
		center4.div(center4.w);

		Vec3d camera = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
		center = new Vec3d(camera.x + center4.x, camera.y + center4.y, camera.z + center4.z);
	}

	public static RenderManager getInstance() {
		return INSTANCE;
	}

	public Render2D get2D() {
		return render2D;
	}

	public Render3D get3D() {
		return render3D;
	}

	public void beginFrame(IRenderer renderer) {
		renderer.clearStorageFrame();
		activeRenderers.clear();
		registerRenderer(renderer);
	}

	public void endFrame() {
		for (IRenderer renderer : activeRenderers) {
			if (renderer.isBuilding()) {
				renderer.end();
			}
			renderer.render();
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
}