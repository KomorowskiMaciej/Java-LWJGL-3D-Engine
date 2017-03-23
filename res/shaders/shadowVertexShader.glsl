#version 150

in vec3 in_position;
in vec2 in_textureCoords;

out vec2 textureCoords;


uniform mat4 mvpMatrix;
uniform float numberOfRows;
uniform vec2 offset;


void main(void){
	gl_Position = mvpMatrix * vec4(in_position, 1.0);
	textureCoords = (in_textureCoords/numberOfRows) + offset;
}