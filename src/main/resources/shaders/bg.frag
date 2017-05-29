#version 330

layout (location = 0) out vec4 color;

in DATA
{
    vec2 tc;
} fs_in;

uniform sampler2D tex;
uniform bool silhouette_mode;

void main()
{
    if (silhouette_mode) {
        if (fs_in.tc.s < 0.001 || fs_in.tc.s > 0.999 || fs_in.tc.t < 0.001 || fs_in.tc.t > 0.999) {
            color = vec4(1, 0, 0, 1);
        }
        else {
            color = texture(tex, fs_in.tc);
        }
    }
    else {
        color = texture(tex, fs_in.tc);
        if (color.a == 0.0) {
            discard;
        }
    }
}