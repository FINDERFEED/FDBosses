#version 150

uniform sampler2D DiffuseSampler;

uniform mat4 modelview;
uniform mat4 projection;
uniform mat4 inverseProjection;
uniform vec2 ScreenSize;
uniform vec3 sphereRelativePosition;
uniform float sphereRadius;

in vec2 texCoord;

out vec4 fragColor;


float distanceToSphere(vec3 currentPos, vec3 spherePos, float sphereRadius){
    float l = length(spherePos - currentPos) - sphereRadius;
    return l;
}

float distanceToEllipse(vec3 pos, vec3 ellipsePos, vec3 r){

    pos -= ellipsePos;

    float k0 = length(pos/r);
    float k1 = length(pos/(r*r));
    return k0*(k0-1.0)/k1;
}




void main(){

    vec4 color = texture(DiffuseSampler, texCoord);

    vec2 uv = (texCoord - 0.5) * 2.0;
    float ar = (ScreenSize.y / ScreenSize.x);
//    uv.y *= (ScreenSize.y / ScreenSize.x);

    vec4 rayDirW = inverseProjection * vec4(uv.x,uv.y, 1.0, 1.0);
    rayDirW /= rayDirW.w;

    vec3 rayDir = normalize(rayDirW.xyz);

    int steps = 100;
    float currentDistance = 0.0;
    int wasTargetHit = 0;

    vec4 p = vec4(sphereRelativePosition.x, sphereRelativePosition.y, sphereRelativePosition.z, 1.0);

    float actualRadius = sphereRadius;

    vec4 res = modelview * p;
    vec3 spherePos = res.xyz;


    for (int i = 0; i < steps; i++){

        vec3 pos = rayDir * currentDistance;
        float dist = distanceToSphere(pos, spherePos, sphereRadius);

        if (dist < 0.01){
            wasTargetHit = 1;
            break;
        }else if (dist > 100){
            break;
        }

        currentDistance += dist;

    }


    vec3 col = vec3(currentDistance * 0.25);

    if (wasTargetHit == 0){
        fragColor = color;
    }else{
        fragColor = vec4(col.x,col.y,col.z,1.0);
    }

}