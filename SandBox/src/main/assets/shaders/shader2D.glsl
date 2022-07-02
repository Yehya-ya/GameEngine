#type vertex
#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec2 aTexCoord;

uniform mat4 uTransformation;
uniform mat4 uViewProjection;

out vec2 fTexCoord;
void main()
{
    fTexCoord = aTexCoord;
    gl_Position = uViewProjection * uTransformation * vec4(aPos, 1.0);
}

#type fragment
#version 330 core


in vec2 fTexCoord;

uniform vec4 uColor;
uniform float uTilingFactor;
uniform sampler2D uTexture;

void main()
{
    gl_FragColor = texture(uTexture, fTexCoord * uTilingFactor) * uColor;
}