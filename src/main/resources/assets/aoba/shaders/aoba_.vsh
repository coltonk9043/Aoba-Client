#version 330

// Shared vertex shader for all Aoba UI shader effects.
// Passes through position and local UV coordinates.
layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform Projection {
    mat4 ProjMat;
};

in vec3 Position;
in vec2 UV0;

out vec2 localUV;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    localUV = UV0;
}
