#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;

    /*
     * Legacy TC6 particle rendering used alphaFunc threshold 1/255.
     * Vanilla 1.21.1 POSITION_TEX_COLOR / POSITION_COLOR_TEX_LIGHTMAP uses a much higher cutoff,
     * which removes the soft outer alpha ramp from particles.png.
     */
    if (color.a < 0.003921569) {
        discard;
    }

    /*
     * Important correction:
     * UV2 is a lightmap texture coordinate, not a brightness scalar. The previous
     * shader multiplied RGB directly by texCoord2, which can evaluate near zero
     * for the packed legacy value 200 and make the whole flare invisible.
     *
     * TC6 passes brightness=200 to TexturedQuadTC. For this exact compatibility
     * shader, interpret that legacy value as 200/255 intensity. The caller path is
     * already restricted to the knowledge flare case with brightness=200/blend=1.
     */
    color.rgb *= 0.78431374;

    fragColor = color * ColorModulator;
}