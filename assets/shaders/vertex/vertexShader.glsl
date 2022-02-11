#version 330 core
layout (location=0) in vec2 aPos;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main()
{
    gl_Position = projectionMatrix *( viewMatrix *(transformationMatrix * vec4(aPos,0.0f, 1.0)));
}