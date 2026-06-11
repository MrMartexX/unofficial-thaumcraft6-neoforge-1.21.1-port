#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;

    /*
     * TC6 ParticleEngine used GL alphaFunc threshold 1/255 for legacy FX layers.
     * Vanilla 1.21.1 POSITION_TEX_COLOR discards far more of the soft alpha ramp.
     */
    if (color.a < 0.003921569) {
        discard;
    }

    fragColor = color * ColorModulator;
}
