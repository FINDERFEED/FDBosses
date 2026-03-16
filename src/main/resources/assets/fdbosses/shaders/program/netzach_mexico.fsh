#version 150

uniform sampler2D DiffuseSampler;
uniform float percent;

in vec2 texCoord;

out vec4 fragColor;

float pixelBrightness(vec4 b){
    return max(b.x, max(b.y, b.z));
}

void main(){


    vec4 imgCol = texture(DiffuseSampler, texCoord);

    float brightness = pixelBrightness(imgCol);

    brightness = smoothstep(0., 1., brightness);

    vec4 baseCol = vec4(0.329,0.28,0.217,1.);
    vec4 secCol = vec4(0.743,0.478,0.239,1.);

    vec4 resultCol = mix(baseCol, secCol, brightness);

    float l = length((texCoord - 0.5) * 2);

    float lengthIntensity =  l - (1 - percent);
    float intensity = max(1,lengthIntensity);
    intensity = pow(intensity, 1) + percent;




    fragColor = resultCol * brightness * intensity;

}