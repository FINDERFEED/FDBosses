#version 330


uniform vec2 size;
uniform vec2 xyOffset;
uniform float sections;
uniform float octaves;
uniform float time;

in vec2 texCoord0;
in vec4 vertexColor;



out vec4 fragColor;

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

float transformValue(float val){
    return val;
}

void main(){


    vec2 uv = normalizeCoords(texCoord0) + xyOffset;

    float value = perlinNoise(uv.x + 100, uv.y + 100, time, 1, 8);

    float noiseAmplitude = 0.4;
    value += noiseAmplitude; value /= (noiseAmplitude * 2);
    value = transformValue(value);
    value = clamp(value,0,1);


    float evalue = 1 - value;

    vec4 col = mix(vec4(0,0,0,0.1), vec4(1.0,0.5,0.,1.), value);
    vec4 colglow = mix(vec4(0,0,0,0.1), vec4(1.0,1.0,0.,1.), value);
    vec4 col2 = mix(vec4(0,0,0,0.1), vec4(0.0,0.5,1.,1.), evalue);
    vec4 col2glow = mix(vec4(0,0,0,0.1), vec4(0.0,0.5,1.,1.), evalue * evalue * evalue * evalue);

    vec4 color = srcAlphaOneMinusSrcAlpha(col,col2);

    colglow.rgb -= 0.75;
    colglow = clamp(colglow,vec4(0),vec4(999));

    color += colglow;

    color.a = 1;

    fragColor = color;
}


