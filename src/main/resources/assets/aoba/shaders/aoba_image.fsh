#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform AobaShaderParams {
    vec4 Tint;
};

uniform sampler2D Sampler0;

in vec2 localUV;

out vec4 fragColor;

void main() {
    vec4 texSample = texture(Sampler0, localUV);
    vec4 color = texSample * Tint * ColorModulator;
    fragColor = vec4(color.rgb * color.a, color.a);
}
