#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform AobaShaderParams {
    vec4 StartColor;
    vec4 EndColor;
    float Angle;
};

uniform sampler2D Sampler0;

in vec2 localUV;

out vec4 fragColor;

void main() {
    vec4 startColor = StartColor;
    vec4 endColor = EndColor;
    float angle = radians(Angle);

    vec2 dir = vec2(cos(angle), sin(angle));
    float t = dot(localUV - 0.5, dir) + 0.5;
    t = clamp(t, 0.0, 1.0);

    vec4 color = mix(startColor, endColor, t) * ColorModulator;
    vec4 texSample = texture(Sampler0, localUV);
    color.a *= min(texSample.r, texSample.a);
    fragColor = vec4(color.rgb * color.a, color.a);
}
