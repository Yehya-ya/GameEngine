#type vertex
#version 330 core
layout (location=0) in vec2 aPos;

uniform mat4 uTransformation;

void main()
{
    gl_Position = uTransformation * vec4(aPos,0.0f, 1.0);
}

#type fragment
#version 330 core

uniform vec4 u_color;

void main()
{
    gl_FragColor = u_color;
}