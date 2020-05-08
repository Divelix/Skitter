varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;
uniform vec2 u_resolution;
uniform float u_time;
uniform vec2 u_mouse;

float Circle(vec2 uv, vec2 p, float r, float blur) {
    float d = length(uv-p);
    return smoothstep(r, r-blur, d);
}

float Ring(vec2 uv, vec2 p, float r, float w, float blur) {
    float c1 = Circle(uv, p, r-w/2., blur);
    float c2 = Circle(uv, p, r+w/2., blur);
    return c2-c1;
}

void main() {
    float aspectRatio = u_resolution.x / u_resolution.y;
    vec2 mouse = (u_mouse / u_resolution) - .5;
    vec2 uv = v_texCoord0 - .5;
    mouse.x *= aspectRatio;
    uv.x *= aspectRatio;

//    float x = fract(u_time*0.1)-.5;
    float c = 0.;
    c += Ring(uv, mouse, .03, .01, .005);
//    c += Circle(uv, mouse,  .15, .15);
    vec2 mask = vec2(c*(uv-mouse));

    vec4 color = texture2D(u_sampler2D, v_texCoord0 + mask);
    gl_FragColor = color;
//    gl_FragColor = vec4(mask, .0, 1.);
}