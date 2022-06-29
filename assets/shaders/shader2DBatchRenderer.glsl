#type vertex
#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoord;
uniform mat4 uViewProjection;

out vec4 fColor;
out vec2 fTexCoord;
void main()
{
    fColor = aColor;
    fTexCoord = aTexCoord;
    gl_Position = uViewProjection * vec4(aPos, 1.0);
}

#type fragment
#version 330 core


in vec4 fColor;
in vec2 fTexCoord;

uniform sampler2D uTexture;

void main()
{
    gl_FragColor = fColor;
}