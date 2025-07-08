#version 330 core

layout (location = 0) in vec4 pos;
layout (location = 1) in vec4 color;

uniform Matrices {
    mat4 projectionMatrix;
    mat4 modelViewMatrix;
};

out vec4 outColor;

void main() {
    gl_Position = projectionMatrix * modelViewMatrix * pos;
    outColor = color;
}