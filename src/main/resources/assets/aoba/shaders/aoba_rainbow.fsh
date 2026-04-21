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
    float Speed;
    float Scale;
    float Saturation;
    float Brightness;
    float Transparency;
};

uniform sampler2D Sampler0;

in vec2 localUV;

out vec4 fragColor;

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    float speed = Speed;
    float scale = Scale;
    float saturation = Saturation;
    float brightness = Brightness;
    float transparency = Transparency;

    vec2 pixel = gl_FragCoord.xy / Resolution;
    float hue = fract((pixel.x + pixel.y) * scale + Time * speed);
    vec3 rgb = hsv2rgb(vec3(hue, saturation, brightness));
    vec4 texSample = texture(Sampler0, localUV);
    float mask = min(texSample.r, texSample.a);
    vec4 color = vec4(rgb, transparency * mask) * ColorModulator;
    fragColor = vec4(color.rgb * color.a, color.a);
}
