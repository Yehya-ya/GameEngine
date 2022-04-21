#version 330 core
in vec3 fNormal;
in vec2 fTexCoords;

uniform sampler2D uTexture;
void main()
{
    vec4 albedo = texture(uTexture, fTexCoords);
    gl_FragColor = albedo;
}