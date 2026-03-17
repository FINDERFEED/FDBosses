#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 ScreenSize;
uniform float percent;
uniform float time;


in vec2 texCoord;

out vec4 fragColor;

float pixelBrightness(vec4 b){
    return max(b.x, max(b.y, b.z));
}


vec4 srcOneSrcAlpha(vec4 src, vec4 dest){

    return vec4(
    src.r + dest.r * src.a,
    src.g + dest.g * src.a,
    src.b + dest.b * src.a,
    src.a + dest.a * src.a
    );
}



//  https://www.shadertoy.com/view/Xt3cDn
uint baseHash(uvec3 p)
{
    const uint PRIME32_2 = 2246822519U, PRIME32_3 = 3266489917U;
    const uint PRIME32_4 = 668265263U, PRIME32_5 = 374761393U;
    uint h32 =  p.z + PRIME32_5 + p.x*PRIME32_3;
    h32 = PRIME32_4*((h32 << 17) | (h32 >> (32 - 17)));
    h32 += p.y * PRIME32_3;
    h32 = PRIME32_4*((h32 << 17) | (h32 >> (32 - 17)));
    h32 = PRIME32_2*(h32^(h32 >> 15));
    h32 = PRIME32_3*(h32^(h32 >> 13));
    return h32^(h32 >> 16);
}


//  https://www.shadertoy.com/view/Xt3cDn
float hash13(uvec3 x)
{
    uint n = baseHash(x);
    return float(n)*(1.0/float(0xffffffffU));
}

float hash13(vec3 x) {
    return hash13(
    uvec3(abs(x.x), abs(x.y), abs(x.z))
    );
}



//  https://www.shadertoy.com/view/Xt3cDn
uint baseHash(uint p)
{
    const uint PRIME32_2 = 2246822519U, PRIME32_3 = 3266489917U;
    const uint PRIME32_4 = 668265263U, PRIME32_5 = 374761393U;
    uint h32 = p + PRIME32_5;
    h32 = PRIME32_4*((h32 << 17) | (h32 >> (32 - 17))); //Initial testing suggests this line could be omitted for extra perf
    h32 = PRIME32_2*(h32^(h32 >> 15));
    h32 = PRIME32_3*(h32^(h32 >> 13));
    return h32^(h32 >> 16);
}


//  https://www.shadertoy.com/view/Xt3cDn
float hash11(float xf)
{

    uint x = uint(xf);
    uint n = baseHash(x);
    return float(n)*(1.0/float(0xffffffffU));
}


vec4 whiteNoiseAddition(vec3 uvt, vec4 base){

    float sectionSize = 5.;

    vec2 sections = ScreenSize.xy / sectionSize;

    vec2 sectionsXY = floor(uvt.xy * sections);


    float noise = hash13(
    uvec3(abs(sectionsXY.x), abs(sectionsXY.y), abs(uvt.z * 100.))
    );

    vec4 basecol = vec4(base.x, base.y, base.z, base.w);
    if (basecol.w == 0){
        basecol.w = 1;
    }

    vec4 res = srcOneSrcAlpha(basecol, vec4(noise  * 0.125));

    return res;
}


vec4 chainsawInHalf(vec3 uvt){

    float sinOffset = sin(uvt.y * 500. + uvt.z * 10.) * (2. / ScreenSize.x);

    float pixelsMaxOffset = 7.;
    float inTexelsOffset = pixelsMaxOffset / ScreenSize.x;

    float texelLineDelta = 0.005;


    float time = uvt.z;
    float fr = fract(time / 2.);

    float lineY = 1. - fr;

    float leftRightGlithIntensity = 22.;

    float lineUpDownGlitch = hash11(lineY * 100.);

    lineY += lineUpDownGlitch * 0.04;

    if (uvt.y < lineY - texelLineDelta){

        float p = hash11(lineY * leftRightGlithIntensity + time);
        float currentOffsetDown = mix(-inTexelsOffset, inTexelsOffset, p);


        return texture(DiffuseSampler, uvt.xy + vec2(currentOffsetDown + sinOffset,0.));
    }else if (uvt.y > lineY + texelLineDelta) {

        float p = hash11(lineY * leftRightGlithIntensity + 50. + time);
        float currentOffsetUp = mix(-inTexelsOffset, inTexelsOffset, p);

        return texture(DiffuseSampler, uvt.xy + vec2(currentOffsetUp + sinOffset, 0.));
    }else{
        float addition = hash13(vec3(
        uvt.x * 300.,
        uvt.y * 300.,
        uvt.z * 30.
        ));
        return texture(DiffuseSampler, vec2(uvt.x , lineY)) + addition * 0.2;
    }


}

vec4 whiteLines(vec4 color, vec3 uvt){


    float lineY = uvt.y * 10.0;
    int lineIndex = int(lineY);



    float h = hash13(vec3(0., lineY, uvt.z * 20.));
    float h2 = hash13(vec3(100.32, lineY, uvt.z * 20.));

    float lineWidth = mix(0.4,1., h2);

    float lineStartPos = mix(-3.,3.,h);

    float x = uvt.x;
    float y = uvt.y;
    if (x > lineStartPos && x < lineStartPos + lineWidth){

        if (fract(lineY) < 0.03){

            float addition = hash13(vec3(
            uvt.x * 1000.,
            uvt.y * 1000.,
            32.032
            ));

            float hw = lineWidth / 2.;
            float p;
            if (x > lineStartPos + hw){
                p = 1. - (x - lineStartPos - hw) / hw;
            }else{
                p = (x - lineStartPos) / hw;
            }
            addition *= p * p * 0.5;

            color = srcOneSrcAlpha(color, vec4(addition));

        }

    }


    return color;
}

vec4 srcAlphaOneMinusSrcAlpha(vec4 src, vec4 dest) {
    return vec4(
    src.r * src.a + dest.r * (1.0 - src.a),
    src.g * src.a + dest.g * (1.0 - src.a),
    src.b * src.a + dest.b * (1.0 - src.a),
    src.a * src.a + dest.a * (1.0 - src.a)
    );
}

#define sq2 1.41421356

vec4 addVignette(vec4 color, vec3 uvt){

    float len = length(uvt.xy - 0.5) * 2;
    float alphaMod = 0;
    float size = 1;

    if (len > size){
        float l = len - size;
        float d = sq2 - size;
        alphaMod = min(1, l / d);
    }


    alphaMod = max(len - 0.5,0) / 0.9;

    vec4 finalColor = srcAlphaOneMinusSrcAlpha(vec4(0,0,0,1) * (alphaMod + 0.001), color);

    return finalColor;
}

vec4 mexicofy(vec4 color){

    float brightness = pixelBrightness(color);

    brightness = smoothstep(0., 1., brightness);

    vec4 baseCol = vec4(0.329,0.28,0.217,1.);
    vec4 secCol = vec4(0.743,0.478,0.239,1.);

    vec4 resultCol = mix(baseCol, secCol, brightness);

    float l = length((texCoord - 0.5) * 2);

    float lengthIntensity =  l - (1 - percent);
    float intensity = max(1,lengthIntensity);
    intensity = pow(intensity, 1) + percent;

    color = resultCol * brightness * intensity;

    return color;
}



void main(){

    vec3 uvt = vec3(texCoord.x, texCoord.y, time / 20.);

    vec4 base = chainsawInHalf(uvt);

    base = whiteNoiseAddition(uvt, base);

    if (fract(uvt.z) < 0.4){
        base = whiteLines(base, uvt);
    }

    base = mexicofy(base);

    base = addVignette(base, uvt);

    fragColor = base;

}