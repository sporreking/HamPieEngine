#version 440

uniform sampler2D t_sampler;

in vec2 pass_texCoords;

out vec4 out_color;

void main()
{
	//out_color = vec4(pass_texCoords, 0, 1);
	out_color = texture2D(t_sampler, pass_texCoords);
}