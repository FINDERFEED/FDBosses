#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

#define POINT_COUNT 64

uniform float positions[POINT_COUNT * 2];
uniform float radiuses[POINT_COUNT];

uniform vec2 screenSize;
uniform float scale;
uniform float time;
uniform float offsetX;
uniform float offsetY;

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

//https://www.shadertoy.com/view/XlGcRh
float hashwithoutsine13(vec3 p3)
{
    p3  = fract(p3 * .1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}


const vec3 GRADIENTS[32] = vec3[](

    vec3( 1, 1, 0), vec3(-1, 1, 0),
    vec3( 1,-1, 0), vec3(-1,-1, 0),

    vec3( 1, 0, 1), vec3(-1, 0, 1),
    vec3( 1, 0,-1), vec3(-1, 0,-1),

    vec3( 0, 1, 1), vec3( 0,-1, 1),
    vec3( 0, 1,-1), vec3( 0,-1,-1),

    vec3( 0.5773503,  0.5773503,  0.5773503),
    vec3(-0.5773503,  0.5773503,  0.5773503),
    vec3( 0.5773503, -0.5773503,  0.5773503),
    vec3(-0.5773503, -0.5773503,  0.5773503),

    vec3( 0.5773503,  0.5773503, -0.5773503),
    vec3(-0.5773503,  0.5773503, -0.5773503),
    vec3( 0.5773503, -0.5773503, -0.5773503),
    vec3(-0.5773503, -0.5773503, -0.5773503),

    vec3( 0.7071068,  0.7071068, 0.0),
    vec3(-0.7071068,  0.7071068, 0.0),
    vec3( 0.7071068, -0.7071068, 0.0),
    vec3(-0.7071068, -0.7071068, 0.0),

    vec3( 0.7071068, 0.0,  0.7071068),
    vec3(-0.7071068, 0.0,  0.7071068),
    vec3( 0.7071068, 0.0, -0.7071068),
    vec3(-0.7071068, 0.0, -0.7071068),

    vec3(0.0,  0.7071068,  0.7071068),
    vec3(0.0, -0.7071068,  0.7071068),
    vec3(0.0,  0.7071068, -0.7071068),
    vec3(0.0, -0.7071068, -0.7071068)

);


vec3 generateGradientVector(float x,float y,float z){

//    return normalize((hashwithoutsine33(uvec3(abs(x)*2329.,abs(y)*1209.,abs(z)*2239.)) -0.5) * 2.);

    return GRADIENTS[uint(hashwithoutsine13(vec3(x, y, z)) * 32) & 31u];
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

float transformNoiseValue(float value, float amplitude){
    value += 1; value /= 2;
    value /= amplitude;
    return value;
}


void main() {

    vec4 color = texture(Sampler0, texCoord0) * vertexColor;

    float screenPosX = (texCoord0.x - 0.5) * screenSize.x / scale + offsetX;
    float screenPosY = ((1 - texCoord0.y) - 0.5) * screenSize.y / scale - offsetY;

    float shadowRadius = 800;

    float distSqr = screenPosX * screenPosX + screenPosY * screenPosY;

    if (distSqr > shadowRadius * shadowRadius){
        fragColor = vec4(0,0,0,1);
        return;
    }

    float shadowDensityDistance = 200;

    vec2 npos = vec2(screenPosX, screenPosY) * 0.01;

    float fog1 = perlinNoise(npos.x + 1000 - time , npos.y + 100, time * 4, 0.6, 5);
    fog1 = transformNoiseValue(fog1, 0.9);
    fog1 = smoothstep(0,1,fog1);
    fog1 = smoothstep(0,1,fog1);

    float fog2 = perlinNoise(npos.x + 100 - time * 0.5, npos.y + 100, time * 2, 0.3, 1);
    fog2 = transformNoiseValue(fog2, 1);

    float density = clamp(fog1 * fog2, 0, 1) * 0.6 + 0.4;

    float circularShadow = 0;
    float distToShadowSqr = (shadowRadius - shadowDensityDistance) * (shadowRadius - shadowDensityDistance);

    if (distSqr > distToShadowSqr){
        circularShadow = (sqrt(distSqr) - sqrt(distToShadowSqr)) / shadowDensityDistance;
    }


    for (int i = 0; i < POINT_COUNT; i++){

        float posX = positions[i * 2];
        float posY = positions[i * 2 + 1];

        float radius = radiuses[i];

        if (posX == 1000000 || radius < 0.001) {
            continue;
        }

        float dist = distance(vec2(screenPosX, screenPosY), vec2(posX, posY));
        float distMod = clamp(smoothstep(0,1,dist / radius), 0, 1);

        density *= distMod;

    }



    if (color.a == 0){
        color.rgb = vec3(0);
        color.a += density;
    }else{
        color.rgb *= (1 - density);
    }


    fragColor = color * ColorModulator + vec4(0,0,0,circularShadow);

}