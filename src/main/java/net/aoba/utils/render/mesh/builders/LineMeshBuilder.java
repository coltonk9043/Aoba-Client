package net.aoba.utils.render.mesh.builders;

import com.mojang.blaze3d.pipeline.RenderPipeline;

public class LineMeshBuilder extends AbstractMeshBuilder {
	public LineMeshBuilder(RenderPipeline pipeline) {
		super(pipeline);
	}

	public void line(int indice1, int indice2) {
		ensureIndiceCapacity(2 * Integer.BYTES);
		indices.putInt(indice1);
		indices.putInt(indice2);

		indicesCount += 2;
	}
}
