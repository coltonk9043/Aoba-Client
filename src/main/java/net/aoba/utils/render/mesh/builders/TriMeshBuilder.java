package net.aoba.utils.render.mesh.builders;

import com.mojang.blaze3d.pipeline.RenderPipeline;

public class TriMeshBuilder extends AbstractMeshBuilder {
	public TriMeshBuilder(RenderPipeline pipeline) {
		super(pipeline);
	}

	public void triangle(int indice1, int indice2, int indice3) {
		ensureIndiceCapacity(3 * Integer.BYTES);
		indices.putInt(indice1);
		indices.putInt(indice2);
		indices.putInt(indice3);

		indicesCount += 3;
	}
}
