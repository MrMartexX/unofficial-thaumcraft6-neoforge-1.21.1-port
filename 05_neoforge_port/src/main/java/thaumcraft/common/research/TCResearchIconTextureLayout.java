package thaumcraft.common.research;

public record TCResearchIconTextureLayout(
        int textureWidth,
        int textureHeight,
        int frameWidth,
        int frameHeight,
        int frameCount,
        int frameSize,
        boolean vertical,
        boolean known
) {
    public static TCResearchIconTextureLayout fromDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            return unknown();
        }
        if (height > width && height % width == 0) {
            return new TCResearchIconTextureLayout(
                    width,
                    height,
                    width,
                    width,
                    height / width,
                    width,
                    true,
                    true
            );
        }
        if (width > height && width % height == 0) {
            return new TCResearchIconTextureLayout(
                    width,
                    height,
                    height,
                    height,
                    width / height,
                    height,
                    false,
                    true
            );
        }
        return new TCResearchIconTextureLayout(
                width,
                height,
                width,
                height,
                1,
                Math.min(width, height),
                false,
                true
        );
    }

    public static TCResearchIconTextureLayout unknown() {
        return new TCResearchIconTextureLayout(16, 16, 16, 16, 1, 16, false, false);
    }

    public int frameAt(long timeMillis, long frameTimeMillis) {
        if (frameCount <= 1 || frameTimeMillis <= 0) {
            return 0;
        }
        return (int) (Math.max(0L, timeMillis) / frameTimeMillis % frameCount);
    }

    public int uOffset(int frame) {
        return vertical ? 0 : clampedFrame(frame) * frameSize;
    }

    public int vOffset(int frame) {
        return vertical ? clampedFrame(frame) * frameSize : 0;
    }

    private int clampedFrame(int frame) {
        return Math.max(0, Math.min(frame, frameCount - 1));
    }
}
