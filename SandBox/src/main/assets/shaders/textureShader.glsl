#type vertex
#version 330 core
layout (location=0) in vec2 aPos;
layout (location=1) in vec2 aTexCoord;

uniform mat4 uTransformation;
uniform mat4 uViewProjection;

out vec2 fTexCoord;
void main()
{
    fTexCoord = aTexCoord;
    gl_Position = uViewProjection * uTransformation * vec4(aPos,0.0f, 1.0);
}

#type fragment
#version 330 core

uniform vec4 u_color;

in vec2 fTexCoord;

uniform sampler2D uTexture;

void main()
{
    gl_FragColor = texture(uTexture, fTexCoord);
}