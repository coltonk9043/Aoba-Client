package net.aoba.rendering;

import java.util.Arrays;

public class VertexCollector {
	float[] positions;
	float[] uvs;
	int count;
	final float ox, oy, ow, oh;

	VertexCollector(int initialCapacity, float ox, float oy, float ow, float oh) {
		positions = new float[initialCapacity * 2];
		uvs = new float[initialCapacity * 2];
		this.ox = ox; 
		this.oy = oy; 
		this.ow = ow; 
		this.oh = oh;
	}

	void vertex(float x, float y) {
		int capacity = positions.length / 2;
		if (count >= capacity) {
			int newSize = capacity * 2;
			positions = Arrays.copyOf(positions, newSize * 2);
			uvs = Arrays.copyOf(uvs, newSize * 2);
		}
		positions[count * 2] = x;
		positions[count * 2 + 1] = y;
		uvs[count * 2] = ow > 0 ? (x - ox) / ow : 0f;
		uvs[count * 2 + 1] = oh > 0 ? (y - oy) / oh : 0f;
		count++;
	}

	void tri(float x1, float y1, float x2, float y2, float x3, float y3) {
		vertex(x1, y1);
		vertex(x2, y2);
		vertex(x3, y3);
	}
}