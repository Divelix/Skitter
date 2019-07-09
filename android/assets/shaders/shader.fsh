varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;
uniform float u_time;

void main() {
    vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color;
//    color.r = 1f - color.r * u_time * 1.1f;
//    color.g = 1f - color.g * u_time * 2f;
//    color.b = 1f - color.g * u_time * 5f;
    gl_FragColor = color;
//    gl_FragColor = vec4(1f, 0f, 1f, 1f);
}