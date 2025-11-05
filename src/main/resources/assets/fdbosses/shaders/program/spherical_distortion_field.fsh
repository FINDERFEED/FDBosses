#version 150

uniform sampler2D DiffuseSampler;

uniform mat4 inverseProjection;
uniform mat4 inverseModelview;
uniform vec3 sphereRelativePosition;
uniform float sphereRadius;
uniform float innerSphereRadius;

uniform float floorOffset;

in vec2 texCoord;

out vec4 fragColor;


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

    planePos = clamp(planePos, -sphereRadius, sphereRadius);

    pos -= spherePos;

    float w = sqrt(sphereRadius*sphereRadius-planePos*planePos);

    vec2 q = vec2( length(pos.xz), pos.y );

    float s = max( (planePos-sphereRadius)*q.x*q.x+w*w*(planePos+sphereRadius-2.0*q.y), planePos*q.x-w*q.y );

    return (s<0.0) ? length(q)-sphereRadius :
    (q.x<w) ? planePos - q.y     :
    length(q-vec2(w,planePos));
}


void main(){

    vec4 color = texture(DiffuseSampler, texCoord);

    vec3 rayDir = getRayDirection();

    int steps = 100;
    float currentDistance = 0.0;

    float density = 0.0;

    vec3 spherePos = sphereRelativePosition;

    int maxInsideSphereSteps = 10;
    float insideSphereStep = 0.1;

    for (int i = 0; i < steps; i++){

        vec3 pos = rayDir * currentDistance;
        float distOuter = distanceToCutSphere(pos, spherePos, sphereRadius, floorOffset);
        float distInner = distanceToCutSphere(pos, spherePos - vec3(0,0.05,0), innerSphereRadius, floorOffset);

        float dist = max(-distInner, distOuter);


        if (dist < 0.01){

            vec3 normal = normalize(pos - spherePos);
            float d = abs(dot(normalize(spherePos), normal));
            density = d;


//            for (int k = 1; k < maxInsideSphereSteps; k++){
//
//                float offset = insideSphereStep * k;
//                vec3 insidePos = pos + rayDir * offset;
//
//                float distOuter2 = distanceToCutSphere(insidePos, spherePos, sphereRadius, floorOffset);
//                float distInner2 = distanceToCutSphere(insidePos, spherePos - vec3(0,0.05,0), innerSphereRadius, floorOffset);
//
//                if (distOuter2 <= 0 && distInner2 >= 0){
//                    density += insideSphereStep;
//                }else{
//                    break;
//                }
//
//            }

            break;
        }else if (dist > 100){
            break;
        }

        currentDistance += dist;

    }


    vec3 col = color.rgb + density / 2;

    if (density == 0){
        fragColor = color;
    }else{
        fragColor = vec4(col.x,col.y,col.z,1.0);
    }

}