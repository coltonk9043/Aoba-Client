#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};
layout(std140) uniform AobaShaderParams {
    float Time;
    vec2 Resolution;
    vec4 Tint;
    float Radius;
    float Quality;
};

uniform sampler2D Sampler0;

in vec2 localUV;

out vec4 fragColor;

void main() {
    vec4 tint = Tint;
    float radius = Radius;
    float quality = max(Quality, 1.0);

    vec2 texSize = vec2(textureSize(Sampler0, 0));
    vec2 screenUV = gl_FragCoord.xy / texSize;

    vec3 color = vec3(0.0);
    float total = 0.0;

    for (float x = -radius; x <= radius; x += quality) {
        for (float y = -radius; y <= radius; y += quality) {
            if (x * x + y * y > radius * radius) continue;

            vec2 offset = vec2(x, y) / texSize;
            float weight = 1.0 - length(vec2(x, y)) / radius;
            color += texture(Sampler0, screenUV + offset).rgb * weight;
            total += weight;
        }
    }

    color /= total;
    color *= tint.rgb;
    vec4 result = vec4(color, tint.a) * ColorModulator;
    fragColor = vec4(result.rgb * result.a, result.a);
}
