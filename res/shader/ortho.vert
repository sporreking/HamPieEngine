#version 440

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

layout(location=0) in vec2 in_pos;
layout(location=1) in vec2 in_texCoords;

out vec2 pass_texCoords;

void main()
{
	pass_texCoords = in_texCoords;
	gl_Position = model * view * projection * vec4(in_pos, 0, 1);
}