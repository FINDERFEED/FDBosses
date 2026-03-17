#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 ScreenSize;
uniform vec4 vignetteColor;
uniform float size;


in vec2 texCoord;

out vec4 fragColor;

#define sq2 1.41421356


vec4 srcAlphaOneMinusSrcAlpha(vec4 src, vec4 dest) {
    return vec4(
    src.r * src.a + dest.r * (1.0 - src.a),
    src.g * src.a + dest.g * (1.0 - src.a),
    src.b * src.a + dest.b * (1.0 - src.a),
    src.a * src.a + dest.a * (1.0 - src.a)
    );
}

void main(){

    float len = length(texCoord - 0.5) * 2;
    float alphaMod = 0;

    if (len > size){
        float l = len - size;
        float d = sq2 - size;
        alphaMod = min(1, l / d);
    }

    vec4 finalColor = texture(DiffuseSampler, texCoord);

    finalColor = srcAlphaOneMinusSrcAlpha(vignetteColor * alphaMod, finalColor);

    fragColor = finalColor;

}