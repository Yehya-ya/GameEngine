#version 330 core

uniform vec2 point1;
uniform vec2 point2;
uniform vec2 u_resolution;
uniform float u_time;

void main()
{
    vec2 st = gl_FragCoord.xy/u_resolution;

    float r = length(point1 - st) < (0.1 + sin(u_time) *0.01) ? 1.0:0.0;
    float b = length(point2 - st) < (0.1 + sin(u_time) *0.01) ? 1.0:0.0;


    gl_FragColor = vec4(r,.0,b,1.0);
}