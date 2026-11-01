#version 150

uniform sampler2D DiffuseSampler;

uniform mat4 inverseProjection;
uniform mat4 inverseModelview;
uniform vec3 sphereRelativePosition;
uniform float maxSphereRadius;
uniform float sphereRadius;
uniform float effectStrength;

uniform float noiseTextureSize;
uniform sampler3D generatedNoise;

uniform float floorOffset;

in vec2 texCoord;

out vec4 fragColor;

#define PI 3.14


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
    float overallDistortionDensity = 0;
    float overallSmokeDensity = 0;
    int totalSteps = int(ceil(2.0 * sphereRadius / insideStep)) + 2;


    float randomRadiusPickMod = 12 / noiseTextureSize;
    float sphereDensity = 0.0;
    for (int i = 0; i < totalSteps; i++) {

        vec3 pos = rayDir * (currentDistance);
        float dist = distanceToCutSphere(pos, spherePos, sphereRadius, floorOffset);

        if (dist > 0.01) {
            break;
        }

        vec3 rp = pos - spherePos;
        float rpLength = length(rp);

        float distortionDensity = noiseSlice(rp.x, rp.y, rp.z, 0.4, 1.0);
        float modifier = (1 - rpLength / sphereRadius) * min(-dist / 2.0, 1.0);
        overallDistortionDensity += distortionDensity * modifier;


        vec3 direction = normalize(rp);
        float radiusOffset = maxSphereRadius * 2 - sphereRadius;
        vec3 smokePos = (rp + direction * radiusOffset) * 0.5;
        float densitySmoke = noiseSlice(smokePos.x, smokePos.y, smokePos.z, 0.6, 1.0);

        float distMod = max(0.0, min(1.0, rpLength - sphereRadius * (1 - effectStrength * effectStrength)));
        overallSmokeDensity += densitySmoke * modifier * modifier * distMod;

        currentDistance += insideStep;


        float outerRadius = sphereRadius * 0.5;
        float innerRadius = max(0.0, outerRadius - 1);
        float shellWidth = max(outerRadius - innerRadius, 0.0001);
        float t = clamp((rpLength - innerRadius) / shellWidth, 0.0, 1.0);
        float sphereDensityMod = 1.0 - abs(t * 2.0 - 1.0);
        sphereDensityMod = smoothstep(0.0, 1.0, sphereDensityMod);
        sphereDensityMod *= clamp(-dist, 0.0, 1.0);
        sphereDensity += 0.25 * sphereDensityMod * insideStep;

    }

    float finalDensity = 1.0 - exp(-0.5 * overallDistortionDensity);
    float finalDensitySmoke = 1.0 - exp(-overallSmokeDensity);
    float finalSphereDensity = 1.0 - exp(-sphereDensity);

    float uvShiftStrength = exp(-0.05 * length(sphereRelativePosition)) * 0.05;
    vec2 uvShift = vec2(cos(finalDensity * PI * 2) * finalDensity, sin(finalDensity  * PI * 2) * finalDensity) * uvShiftStrength * effectStrength;

    float shiftStr = effectStrength * 0.01 * finalDensitySmoke;
    vec2 redShift = vec2(0.0, shiftStr);
    vec2 greenShift = vec2(shiftStr, -shiftStr);
    vec2 blueShift = vec2(-shiftStr, -shiftStr);

    vec4 shiftedBGR = texture(DiffuseSampler, texCoord + uvShift + redShift);
    vec4 shiftedBGG = texture(DiffuseSampler, texCoord + uvShift + greenShift);
    vec4 shiftedBGB = texture(DiffuseSampler, texCoord + uvShift + blueShift);

    vec4 smoke = vec4(0.8, 0.6, 0.3, finalDensitySmoke);
    vec4 smoke2 = vec4(0.7, 0.4, 0.3, finalDensitySmoke);
    vec4 smoke3 = vec4(0.6, 0.6, 0.3, finalDensitySmoke);
    smoke = srcOneSrcAlpha(smoke, smoke2);
    smoke = srcOneSrcAlpha(smoke, smoke3);
    vec4 sphere = vec4(0.7, 0.6, 0.3, finalSphereDensity);
    sphere = srcOneSrcAlpha(sphere, sphere);

    smoke += sphere;

    vec4 finalColor = srcAlphaOneMinusSrcAlpha(smoke * effectStrength * effectStrength, vec4(shiftedBGR.r, shiftedBGG.g, shiftedBGB.b, shiftedBGR.a));
    fragColor = vec4(finalColor.rgb, 1.0);

}
