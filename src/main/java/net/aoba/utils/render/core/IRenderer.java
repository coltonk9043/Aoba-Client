package net.aoba.utils.render.core;

public interface IRenderer {
	void begin();

	void end();

	boolean isBuilding();

	void reset();

	void render();

	void clearStorageFrame();
}