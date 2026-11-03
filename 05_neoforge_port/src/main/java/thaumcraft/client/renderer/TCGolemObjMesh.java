package thaumcraft.client.renderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

/** Wavefront triangle/quad reader for the unmodified TC6 articulated models. */
final class TCGolemObjMesh {
    private static final Map<ResourceLocation, TCGolemObjMesh> CACHE = new HashMap<>();
    final Map<String, List<Vertex[]>> groups;
    record Vertex(Vector3f position, float u, float v, Vector3f normal) {}

    private TCGolemObjMesh(Map<String, List<Vertex[]>> groups) { this.groups = groups; }
    static void clear() { CACHE.clear(); }

    static TCGolemObjMesh get(ResourceLocation path) { return CACHE.computeIfAbsent(path, TCGolemObjMesh::load); }

    private static TCGolemObjMesh load(ResourceLocation path) {
        List<Vector3f> positions = new ArrayList<>(), normals = new ArrayList<>();
        List<float[]> uv = new ArrayList<>();
        Map<String, List<Vertex[]>> groups = new LinkedHashMap<>();
        String group = "default";
        try (BufferedReader reader = Minecraft.getInstance().getResourceManager().openAsReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] words = line.split("\\s+");
                switch (words[0]) {
                    case "v" -> positions.add(new Vector3f(Float.parseFloat(words[1]), Float.parseFloat(words[2]), Float.parseFloat(words[3])));
                    case "vn" -> normals.add(new Vector3f(Float.parseFloat(words[1]), Float.parseFloat(words[2]), Float.parseFloat(words[3])));
                    case "vt" -> uv.add(new float[]{Float.parseFloat(words[1]), 1 - Float.parseFloat(words[2])});
                    case "g", "o" -> group = words.length > 1 ? words[1] : "default";
                    case "f" -> {
                        if (words.length != 4 && words.length != 5) throw new IOException("Unsupported face: " + line);
                        Vertex[] face = new Vertex[words.length - 1];
                        for (int i = 1; i < words.length; i++) {
                            String[] indices = words[i].split("/", -1);
                            Vector3f position = positions.get(index(indices[0], positions.size()));
                            float[] texture = indices.length > 1 && !indices[1].isEmpty() ? uv.get(index(indices[1], uv.size())) : new float[]{0, 0};
                            Vector3f normal = indices.length > 2 && !indices[2].isEmpty() ? normals.get(index(indices[2], normals.size())) : new Vector3f();
                            face[i - 1] = new Vertex(position, texture[0], texture[1], normal);
                        }
                        Vector3f normal = new Vector3f(face[1].position).sub(face[0].position)
                                .cross(new Vector3f(face[2].position).sub(face[0].position)).normalize();
                        for (int i = 0; i < face.length; i++) if (face[i].normal.lengthSquared() == 0)
                            face[i] = new Vertex(face[i].position, face[i].u, face[i].v, normal);
                        groups.computeIfAbsent(group, key -> new ArrayList<>()).add(face);
                    }
                    default -> { }
                }
            }
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Invalid golem OBJ " + path, exception);
        }
        return new TCGolemObjMesh(groups);
    }

    private static int index(String value, int count) { int i = Integer.parseInt(value); return i < 0 ? count + i : i - 1; }

    void render(String group, PoseStack pose, VertexConsumer consumer, int light, int overlay, int color) {
        for (Vertex[] face : groups.getOrDefault(group, List.of())) {
            float meanU = 0, meanV = 0;
            for (Vertex v : face) { meanU += v.u / face.length; meanV += v.v / face.length; }
            for (int i = 0; i < 4; i++) {
                Vertex v = face[Math.min(i, face.length - 1)];
                float u = v.u + (v.u > meanU ? -.0005F : .0005F), textureV = v.v + (v.v > meanV ? -.0005F : .0005F);
                consumer.addVertex(pose.last(), v.position.x, v.position.y, v.position.z).setColor(color)
                        .setUv(u, textureV).setOverlay(overlay).setLight(light)
                        .setNormal(pose.last(), v.normal.x, v.normal.y, v.normal.z);
            }
        }
    }
}
