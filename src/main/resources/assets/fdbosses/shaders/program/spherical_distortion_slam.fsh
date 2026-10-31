#version 150

uniform sampler2D DiffuseSampler;

uniform mat4 inverseProjection;
uniform mat4 inverseModelview;
uniform vec3 sphereRelativePosition;
uniform float sphereRadius;
uniform float effectStrength;

uniform float noiseTextureSize;
uniform sampler3D generatedNoise;

uniform float floorOffset;

in vec2 texCoord;

out vec4 fragColor;

#define PI 3.14


float makeSmooth(float p) {
    return smoothstep(0,1,p);
}

mat2 rotationMatrix(float angle) {
    return mat2(
            cos(angle), -sin(angle),
            sin(angle), cos(angle)
    );
}


float distanceToSphere(vec3 currentPos, vec3 spherePos, float sphereRadius) {
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
float distanceToCutSphere(vec3 pos, vec3 spherePos,float sphereRadius,float planePos) {

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

vec4 srcAlphaOneMinusSrcAlpha(vec4 src, vec4 dest) {
    return vec4(
            src.r * src.a + dest.r * (1.0 - src.a),
            src.g * src.a + dest.g * (1.0 - src.a),
            src.b * src.a + dest.b * (1.0 - src.a),
            src.a * src.a + dest.a * (1.0 - src.a)
    );
}

float noiseSlice(float x, float y, float z, float minVal, float maxVal) {
    maxVal = min(maxVal, 1.0);
    minVal = max(minVal, 0.0);
    vec3 samplePoint = vec3(x, y, z) / noiseTextureSize;
    float noise = texture(generatedNoise, samplePoint).r;
    if (noise < minVal) {
        return 0;
    } else if (noise > maxVal){
        return 0;
    } else {
        float d = maxVal - minVal;
        float val = (noise - minVal) / d;
        return val;
    }
}

void main(){

    vec3 rayDir = getRayDirection();
    vec3 spherePos = sphereRelativePosition;

    vec4 background = texture(DiffuseSampler, texCoord);

    float currentDistance = 0.0;
    float terminalDistance = 0.01;
    bool hit = false;
    vec3 hitPos;

    for (int i = 0; i < 100; i++) {

        vec3 pos = rayDir * currentDistance;

        float dist = distanceToCutSphere(pos, spherePos, sphereRadius, floorOffset);

        if (dist < terminalDistance) {
            hit = true;
            hitPos = rayDir * (currentDistance + dist);
            currentDistance += max(dist, 0.01);
            break;
        }

        if (dist > 100.0) {
            break;
        }

        currentDistance += max(dist, 0.01);
    }

    if (!hit) {
        fragColor = background;
        return;
    }

    float insideStep = 0.35;
    float overallDensity = 0;
    float overallSteps = 0;
    int totalSteps = int(ceil(2.0 * sphereRadius / insideStep)) + 2;

    for (int i = 0; i < totalSteps; i++) {

        vec3 pos = rayDir * (currentDistance);
        float dist = distanceToCutSphere(pos, spherePos, sphereRadius, floorOffset);

        if (dist > 0.01) {
            break;
        }

        vec3 rp = pos - spherePos;
        float density = noiseSlice(rp.x, rp.y, rp.z, 0.4, 1.0);

        float modifier = (1 - length(rp) / sphereRadius) * min(-dist / 2.0, 1.0);

        overallDensity += density * modifier;
        currentDistance += insideStep;
        overallSteps++;
    }

    float finalDensity = 1.0 - exp(-0.5 * overallDensity);

//    vec4 smokeColor = vec4(0.2, 0.2, 0.2, finalDensity);

    float uvShiftStrength = exp(-0.05 * length(sphereRelativePosition)) * 0.05;
    vec2 uvShift = vec2(cos(finalDensity * PI * 2) * finalDensity, sin(finalDensity  * PI * 2) * finalDensity) * uvShiftStrength;

    vec4 shiftedBG = texture(DiffuseSampler, texCoord + uvShift);

//    vec4 finalColor = srcAlphaOneMinusSrcAlpha(smokeColor, background);

    vec4 finalColor = shiftedBG;

    fragColor = finalColor;
}
