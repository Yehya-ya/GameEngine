#version 330 core

uniform vec2 u_resolution;

void main()
{
    vec2 st = gl_FragCoord.xy/ u_resolution;
    float r = smoothstep(0.0, 0.5, st.x);
    float g = smoothstep(st.x+0.5, 1.0, st.y);
    float b = 1.0 - smoothstep(0.5, 1.0, st.y);
    gl_FragColor = vec4(0.0,g, .0,1.0);
}