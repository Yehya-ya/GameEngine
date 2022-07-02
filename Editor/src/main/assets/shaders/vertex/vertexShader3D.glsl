#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec3 aNormal;
layout (location=2) in vec2 aTexCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

out vec3 fNormal;
out vec2 fTexCoords;

void main()
{
    fNormal = aNormal;
    fTexCoords = aTexCoords;
    gl_Position = projectionMatrix *( viewMatrix *(transformationMatrix * vec4(aPos, 1.0)));
}