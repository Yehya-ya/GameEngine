#type vertex
#version 450
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoord;
layout (location=3) in float aTilingFactor;
layout (location=4) in float aTextureIndex;
layout (location=5) in float aEntityID;
uniform mat4 uViewProjection;

out vec4 fColor;
out vec2 fTexCoord;
out float fTilingFactor;
out flat float fTextureIndex;
out int fEntityID;
void main()
{
    fColor = aColor;
    fTexCoord = aTexCoord;
    fTilingFactor = aTilingFactor;
    fTextureIndex = aTextureIndex;
    fEntityID = int(aEntityID);
    gl_Position = uViewProjection * vec4(aPos, 1.0);
}

#type fragment
#version 450

layout(location = 0) out vec4 color;
layout(location = 1) out int color2;

in vec4 fColor;
in vec2 fTexCoord;
in float fTilingFactor;
in flat float fTextureIndex;
in flat int fEntityID;

uniform sampler2D uTextures[32];

void main()
{
    color = texture(uTextures[int(fTextureIndex)], fTexCoord * fTilingFactor) * fColor;
    color2 = fEntityID;
}