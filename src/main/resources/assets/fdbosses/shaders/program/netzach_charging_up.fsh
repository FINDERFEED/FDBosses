#version 330

uniform sampler2D DiffuseSampler;

uniform mat4 projection;
uniform mat4 modelView;

uniform vec3 effectRelativePosition;
uniform float effectRadius;
uniform float innerRadius;
uniform float time;
uniform float effectStrength;

uniform vec2 OutSize;

in vec2 texCoord;
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

void main(){
    vec4 effectScreenPos = projection * modelView * vec4(effectRelativePosition, 1.0);

    float projectionScale = projection[0][0] / effectScreenPos.w;
    float radiusOnScreen = effectRadius * projectionScale;
    float innerRadiusOnScreen = innerRadius * projectionScale;

    effectScreenPos /= effectScreenPos.w;

    if (effectScreenPos.z <= 0){
        fragColor = texture(DiffuseSampler, texCoord);
        return;
    }

    float aspect = OutSize.y / OutSize.x;
    effectScreenPos.y *= aspect;

    vec2 uv = (texCoord - 0.5) * 2;
    uv.y *= aspect;
    uv -= effectScreenPos.xy;

    float r = length(uv);

    vec2 uvn = uv / r;
    float radiusAtPoint = (perlinNoise3d(uvn.x, uvn.y, 0.0, 7) + 1.0) * 0.5;

    float sinValue = sin(r * 50.0 + radiusAtPoint * 10.0 + time * 10.0) * 0.5 + 0.5;

    float noisyRadius =  (radiusOnScreen + radiusOnScreen * 0.1 * perlinNoise3d(uvn.x, uvn.y, 0.0, 10.0));

    float radMod = smoothstep(0.0, 1.0, max(0.0, 1.0 - r / noisyRadius)) * smoothstep(0.0, 1.0, min(1.0, r / innerRadiusOnScreen));

    sinValue *= radMod;
    float distortionAtPoint = (perlinNoise3d(uv.x, uv.y, time * 0.1, 10.0) + 1.0) * 0.5;

    float strength = effectStrength;

    vec2 displacement = vec2(sin(distortionAtPoint * PI * 2.0), cos(distortionAtPoint * PI * 2.0)) * sinValue * 0.05 * strength;

    vec2 samplePosition = clamp(texCoord + displacement, vec2(0.0), vec2(1.0));

    fragColor = texture(DiffuseSampler, samplePosition);
}
