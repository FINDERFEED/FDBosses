#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

in vec2 texCoord;

out vec4 fragColor;

void main(){

    float depth = texture(DiffuseDepthSampler, texCoord).r;
    depth = depth * depth * depth * depth * depth * depth * depth * depth * depth * depth * depth * depth;

    fragColor = vec4(depth, depth, depth, 1.0);

}