#version 150

uniform sampler2D DiffuseSampler;

uniform mat4 inverseProjection;
uniform mat4 inverseModelview;
uniform vec3 sphereRelativePosition;
uniform float sphereRadius;
uniform float innerSphereRadius;
uniform float effectStrength;

uniform float floorOffset;

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



float makeSmooth(float p){
    return smoothstep(0,1,p);
}

mat2 rotationMatrix(float angle){
    return mat2(
        cos(angle), -sin(angle),
        sin(angle), cos(angle)
    );
}


float distanceToSphere(vec3 currentPos, vec3 spherePos, float sphereRadius){
    float l = length(spherePos - currentPos) - sphereRadius;
    return l;
}

vec3 getRayDirection(){

    vec2 uv = (texCoord - 0.5) * 2.0;
    vec4 rayDirW = inverseModelview * inverseProjection * vec4(uv.x,uv.y, 1.0, 1.0);
    rayDirW /= rayDirW.w;

    return normalize(rayDirW.xyz);
}


//https://www.shadertoy.com/view/stKSzc
float distanceToCutSphere(vec3 pos, vec3 spherePos,float sphereRadius,float planePos){

    planePos = clamp(planePos, -sphereRadius + 0.01, sphereRadius - 0.01);

    pos -= spherePos;

    float w = sqrt(sphereRadius*sphereRadius-planePos*planePos);

    vec2 q = vec2( length(pos.xz), pos.y );

    float s = max( (planePos-sphereRadius)*q.x*q.x+w*w*(planePos+sphereRadius-2.0*q.y), planePos*q.x-w*q.y );

    return (s<0.0) ? length(q)-sphereRadius :
    (q.x<w) ? planePos - q.y     :
    length(q-vec2(w,planePos));
}

vec4 srcOneSrcAlpha(vec4 src, vec4 dest){

    return vec4(
    src.r + dest.r * src.a,
    src.g + dest.g * src.a,
    src.b + dest.b * src.a,
    src.a + dest.a * src.a
    );
}


bool isInsideSphere(){

    vec3 spherePos = sphereRelativePosition;
    float len = length(spherePos);

    return len <= innerSphereRadius;

}

void main(){


    vec3 rayDir = getRayDirection();

    int steps = 100;
    float currentDistance = 0.0;

    float density = 0.0;

    vec3 spherePos = sphereRelativePosition;

    int maxInsideSphereSteps = 10;
    float insideSphereStep = 0.1;

    vec3 hitPos;


    for (int i = 0; i < steps; i++){

        vec3 pos = rayDir * currentDistance;


        float distOuter;
        float distInner;

        if (isInsideSphere()){
            distOuter = distanceToSphere(pos, spherePos, sphereRadius);
            distInner = distanceToSphere(pos, spherePos, innerSphereRadius);
        }else{
            distOuter = distanceToCutSphere(pos, spherePos, sphereRadius, floorOffset);
            distInner = distanceToCutSphere(pos, spherePos - vec3(0,0.05,0), innerSphereRadius, floorOffset);
        }


        float dist = max(-distInner, distOuter);


        if (dist < 0.01){

            if (abs(dist - distOuter) < 0.01){
                vec3 normal = normalize(pos - spherePos);
                float d = abs(dot(normalize(spherePos), normal));
                density = d;
            }else{
                density = 1;
            }

            hitPos = pos;

            break;
        }else if (dist > 100){
            break;
        }

        currentDistance += dist;

    }



    if (density == 0){
        vec4 color = texture(DiffuseSampler, texCoord);
        fragColor = color;
    }else{



        float distanceToScreenEdgeX = min(texCoord.x, 1 - texCoord.x);
        float distanceToScreenEdgeY = min(texCoord.y, 1 - texCoord.y);
        float screenEdgeModifier = clamp(min(distanceToScreenEdgeY, distanceToScreenEdgeX) / 0.1, 0, 1);


        vec3 noisePos = hitPos;
        float sections = 0.5;
        float sections2 = 0.15;
        float distanceToHitPos = length(hitPos);

        if (length(hitPos) < 0.01){
            float z = length(sphereRelativePosition - innerSphereRadius) / 3.;
            noisePos = vec3(texCoord.x,texCoord.y, z);
            sections = 10;
            sections2 = 5;
            distanceToHitPos = 20;
        }

        float noise = perlinNoise(noisePos.x,noisePos.y,noisePos.z,sections,1);
        float noise2 = perlinNoise(noisePos.x,noisePos.y,noisePos.z,sections2,1);

        noise = (noise + 1) / 2;

        float rotationAngle = noise * noise * PI * 2;

        mat2 rotation = rotationMatrix(rotationAngle);

        float shiftDensityNoScreen = pow(density, 4) * effectStrength;
        float shiftDensity = shiftDensityNoScreen * screenEdgeModifier;


        vec3 col = vec3(pow(density, 4) * noise);



        float distanceModifier = min(3, 10 / distanceToHitPos);
        float rgbOffset = 0.0025 * distanceModifier;
        float distortionOffset = 0.01 * distanceModifier;


        vec2 offset = shiftDensity * (vec2(distortionOffset,0) * rotation);
        vec4 colorR = texture(DiffuseSampler, texCoord + offset + vec2(rgbOffset,rgbOffset) * shiftDensity);
        vec4 colorG = texture(DiffuseSampler, texCoord + offset);
        vec4 colorB = texture(DiffuseSampler, texCoord + offset + vec2(-rgbOffset,rgbOffset) * shiftDensity);

        vec4 addedColorMain = vec4(1f,0.8f,0.3f,1f);
        vec4 addedColorSecondary = vec4(0,0,0,0);

        vec4 addedColor = mix(addedColorMain, addedColorSecondary, noise2);
        addedColor = srcOneSrcAlpha(addedColor,addedColor) * shiftDensityNoScreen;


        fragColor = vec4(colorR.r,colorG.g,colorB.b,colorG.a);
        fragColor.rgb += addedColor.rgb * 0.025;

    }

}