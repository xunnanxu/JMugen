#version 330

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 tc;

uniform mat4 proj_mat;
uniform mat4 model_mat;
uniform mat4 scaling_mat;

out DATA {
    vec2 tc;
} vs_out;

void main()
{
    gl_Position = proj_mat * model_mat * scaling_mat * position;
    vs_out.tc = tc;
}