#version 330


uniform vec2 size;
uniform float time;
uniform float progress;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

#define PI 3.14

float hash1(float p){
    p *= 434.0;
    p = fract(p * .1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

const uint k = 1103515245U;

vec3 hashwithoutsine33( uvec3 x )
{
    x = ((x>>8U)^x.yzx)*k;
    x = ((x>>8U)^x.yzx)*k;
    x = ((x>>8U)^x.yzx)*k;

    return vec3(x)*(1.0/float(0xffffffffU));
}

vec3 generateGradientVector(float x,float y,float z){

    return normalize((hashwithoutsine33(uvec3(abs(x)*2329.,abs(y)*1209.,abs(z)*2239.)) -0.5) * 2.);

}

float dotPr(float dx, float dy, float dz,float lx,float ly,float lz,float xo,float yo,float zo){

    vec3 gradient = generateGradientVector(
    dx + xo,
    dy + yo,
    dz + zo
    );

    vec3 toLocal = vec3(
    lx - xo,
    ly - yo,
    lz - zo
    );

    return dot(toLocal,gradient);
}

float fade(float t) {
    return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
}

float perlinNoise3d(float x,float y,float z,float sections){

    x = x * sections;
    y = y * sections;
    z = z * sections;

    float dx = floor(x);
    float dy = floor(y);
    float dz = floor(z);

    float lx = fract(x);
    float ly = fract(y);
    float lz = fract(z);

    float val1 = dotPr(dx,dy,dz,lx,ly,lz,0.,0.,0.);
    float val2 = dotPr(dx,dy,dz,lx,ly,lz,1.,0.,0.);

    float val3 = dotPr(dx,dy,dz,lx,ly,lz,0.,1.,0.);
    float val4 = dotPr(dx,dy,dz,lx,ly,lz,1.,1.,0.);

    float val5 = dotPr(dx,dy,dz,lx,ly,lz,0.,0.,1.);
    float val6 = dotPr(dx,dy,dz,lx,ly,lz,1.,0.,1.);

    float val7 = dotPr(dx,dy,dz,lx,ly,lz,0.,1.,1.);
    float val8 = dotPr(dx,dy,dz,lx,ly,lz,1.,1.,1.);


    lx = fade(lx);
    ly = fade(ly);
    lz = fade(lz);

    float val9 = mix(val1,val2,lx);
    float val10 = mix(val3,val4,lx);
    float val11 = mix(val5,val6,lx);
    float val12 = mix(val7,val8,lx);

    float val13 = mix(val9,val10,ly);
    float val14 = mix(val11,val12,ly);

    float final = mix(val13,val14,lz);

    return final;
}

float perlinNoise(float x,float y,float z,float sections,float octaves){

    float val = 0;

    float mod = 1;


    for (float i = 0; i < octaves;i++){
        float v = perlinNoise3d(x,y,z,sections);
        sections *= 2.0;
        val += v * mod;
        mod /= 2.0;
    }

    return val;
}


vec2 normalizeCoords(vec2 coord){
    float m = size.y / size.x;
    coord.y = coord.y * m;
    return coord;
}


float makeSmooth(float p){
    return smoothstep(0,1,p);
}


vec4 fromTransparentToColorToColor(vec4 color, vec4 target, float edge, float p){
    vec4 result;
    if (p < edge){
        float localP = makeSmooth(p / edge);
        result = mix(vec4(0,0,0,0), color, localP);
    }else{
        float localP = ((p - edge) / (1 - edge));
        result = mix(color, target, localP);
    }
    return result;
}

vec4 srcAlphaOneMinusSrcAlpha(vec4 dest, vec4 source){

    float asour = source.w;

    return vec4(
    source.r * asour + dest.r * (1. - asour),
    source.g * asour + dest.g * (1. - asour),
    source.b * asour + dest.b * (1. - asour),
    source.a * asour + dest.a * (1. - asour)
    );

}

vec4 srcOneSrcAlpha(vec4 src, vec4 dest){

    return vec4(
    src.r + dest.r * src.a,
    src.g + dest.g * src.a,
    src.b + dest.b * src.a,
    src.a + dest.a * src.a
    );
}


float transformValue(float val){
    return pow(val,8);
}

float transformNoiseValue(float value, float amplitude){
    value += 1; value /= 2;
    value /= amplitude;
    return value;
}

vec2 pixelate(vec2 uv, float pixelization){

    uv *= pixelization;
    uv = floor(uv);

    return uv / pixelization;
}

void main(){

    vec2 uv = normalizeCoords(texCoord0);
    vec2 uuv = texCoord0;

    float verticalStretch = 7;

    float xOffset = sin(PI / 4 + uuv.y * PI / 2) * 0.2;

    float offsetStrength = 1 - pow(1 - abs(1 - uuv.x / 0.5),1);

    if (uuv.x > 0.5){
        xOffset = -xOffset;
    }

    float prOffset = (1 - progress) * 0.2;

    float realX = uv.x + xOffset;
    float dist = 0.5 - realX;
    if (uuv.x > 0.5){
        dist = -dist;
        prOffset = prOffset;
    }

    float alpha = (dist - prOffset) / 0.25;
    alpha = clamp(alpha,0,1);
    alpha *= alpha;


    float c1 = perlinNoise((uv.x + xOffset * offsetStrength) * verticalStretch,uv.y + time,time,10, 3);
    c1 = transformNoiseValue(c1, 0.65);

    float c2 = perlinNoise((uv.x + xOffset * offsetStrength) * verticalStretch + 1000.52,uv.y + time,time,10, 3);
    c2 = transformNoiseValue(c2, 0.65);


    vec4 color1 = vec4(18/255.,74/255.,48/255.,alpha);
    color1 = srcOneSrcAlpha(color1,color1);
    vec4 color2 = vec4(21/255.,92/255.,100/255.,alpha);
    color2 = srcOneSrcAlpha(color2,color2);

    vec4 col1 = color1 * c1;
    vec4 col2 = color2 * c2;
    vec4 resulting = srcOneSrcAlpha(col1,col2);




    float flamesAlphaAtPos = perlinNoise3d(uv.x * 2.5,34.324,time,10) * 0.1 + 0.15;
    float flamesAlpha = clamp(1 - (1 - uuv.y) / flamesAlphaAtPos,0,1) * progress;
    float flamesAlphaAtPosUp = perlinNoise3d(uv.x * 2.5,534.324,time,10) * 0.1 + 0.15;
    float flamesAlphaUp = clamp(1 - uuv.y / flamesAlphaAtPos,0,1) * progress;

    float flamesNoiseOffset = perlinNoise3d(uv.x,uv.y,time / 2,6) * 0.05;
    float flamesNoiseOffset2 = perlinNoise3d(uv.x + 543.3,uv.y,time / 2,6) * 0.05;
    float c3 = perlinNoise(uv.x + flamesNoiseOffset,uv.y - time + 0.43,time,20, 5);
    c3 = transformNoiseValue(c3, 0.8);
    float c4 = perlinNoise(uv.x + flamesNoiseOffset2,uv.y + time + 0.43,time,20, 5);
    c4 = transformNoiseValue(c4, 0.8);

    vec4 flamesColorUp = vec4(18/255.,74/255.,48/255.,flamesAlphaUp);
    flamesColorUp = srcOneSrcAlpha(flamesColorUp,flamesColorUp);
    vec4 flamesColor = vec4(flamesColorUp.x,flamesColorUp.y,flamesColorUp.z,flamesAlpha);
    flamesColor = srcOneSrcAlpha(flamesColor,flamesColor);

    vec4 flamesResult = flamesColorUp * c3 + flamesColor * c4;

    resulting = vec4(resulting.x,resulting.y,resulting.z,alpha);
    resulting = resulting + flamesResult;
    resulting.a *= progress;

    fragColor = resulting;

}