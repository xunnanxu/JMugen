#version 330

layout (location = 0) out vec4 color;

in DATA
{
    vec2 tc;
} fs_in;

uniform sampler2D tex;
uniform bool silhouette_mode;
uniform float alpha_modifier = 1f;

vec4 apply_modifier(vec4 color);

void main()
{
    if (silhouette_mode) {
        if (fs_in.tc.s < 0.001 || fs_in.tc.s > 0.999 || fs_in.tc.t < 0.001 || fs_in.tc.t > 0.999) {
            color = vec4(1, 0, 0, 1);
        }
        else {
            color = apply_modifier(texture(tex, fs_in.tc));
        }
    }
    else {
        color = apply_modifier(texture(tex, fs_in.tc));
    }
}

vec4 apply_modifier(vec4 color)
{
    color.a = color.a * alpha_modifier;
    return color;
}