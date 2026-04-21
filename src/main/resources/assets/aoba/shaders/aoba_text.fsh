#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

uniform sampler2D Sampler0;

in vec2 localUV;

out vec4 fragColor;

void main() {
    vec4 texSample = texture(Sampler0, localUV);
    float mask = min(texSample.r, texSample.a);
    vec4 color = vec4(1.0, 1.0, 1.0, mask);
    color *= ColorModulator;
    fragColor = vec4(color.rgb * color.a, color.a);
}
