# Legacy Thaumcraft FX Exact Extract

Generated from the local legacy source tree.

## Purpose

This document extracts the old Thaumcraft FX mechanics so the 1.21.1 port can reproduce the real system instead of using temporary replacement particles.

## FXDispatcher.drawWispyMotes

### drawWispyMotes overload 1

~~~java
public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) {
        drawWispyMotes(d, e, f, vx, vy, vz, age, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, grav);
    }
~~~

### drawWispyMotes overload 2

~~~java
public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) {
        FXGeneric fb = new FXGeneric(getWorld(), d, e, f, vx, vy, vz);
        fb.setMaxAge((int)(age + age / 2 * getWorld().rand.nextFloat()));
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.0f, 0.6f, 0.6f, 0.0f);
        fb.setGridSize(64);
        fb.setParticles(512, 16, 1);
        fb.setScale(1.0f, 0.5f);
        fb.setLoop(true);
        fb.setWind(0.001);
        fb.setGravity(grav);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
~~~

## FXGeneric core methods

### onUpdate

~~~java
public void onUpdate() {
        if (!doneFrames) {
            calculateFrames();
        }
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge) {
            setExpired();
        }
        prevParticleAngle = particleAngle;
        particleAngle += 3.1415927f * rotationSpeed * 2.0f;
        motionY -= 0.04 * particleGravity;
        move(motionX, motionY, motionZ);
        motionX *= slowDown;
        motionY *= slowDown;
        motionZ *= slowDown;
        motionX += world.rand.nextGaussian() * randomX;
        motionY += world.rand.nextGaussian() * randomY;
        motionZ += world.rand.nextGaussian() * randomZ;
        motionX += windX;
        motionZ += windZ;
        if (onGround && slowDown != 1.0) {
            motionX *= 0.699999988079071;
            motionZ *= 0.699999988079071;
        }
    }
~~~

### renderParticle

~~~java
public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        if (loop) {
            setParticleTextureIndex(startParticle + particleAge / particleInc % numParticles);
        }
        else {
            float fs = particleAge / (float) particleMaxAge;
            setParticleTextureIndex((int)(startParticle + Math.min(numParticles * fs, (float)(numParticles - 1))));
        }
        if (finalFrames != null && finalFrames.length > 0 && particleAge > particleMaxAge - finalFrames.length) {
            int frame = particleMaxAge - particleAge;
            if (frame < 0) {
                frame = 0;
            }
            setParticleTextureIndex(finalFrames[frame]);
        }
        particleAlpha = ((alphaFrames.length <= 0) ? 0.0f : alphaFrames[Math.min(particleAge, alphaFrames.length - 1)]);
        particleScale = ((scaleFrames.length <= 0) ? 0.0f : scaleFrames[Math.min(particleAge, scaleFrames.length - 1)]);
        draw(wr, entity, f, f1, f2, f3, f4, f5);
    }
~~~

### draw

~~~java
public void draw(BufferBuilder wr, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float tx1 = particleTextureIndexX / (float) gridSize;
        float tx2 = tx1 + 1.0f / gridSize;
        float ty1 = particleTextureIndexY / (float) gridSize;
        float ty2 = ty1 + 1.0f / gridSize;
        float ts = 0.1f * particleScale;
        if (particleTexture != null) {
            tx1 = particleTexture.getMinU();
            tx2 = particleTexture.getMaxU();
            ty1 = particleTexture.getMinV();
            ty2 = particleTexture.getMaxV();
        }
        if (flipped) {
            float t = tx1;
            tx1 = tx2;
            tx2 = t;
        }
        float fs = MathHelper.clamp((particleAge + partialTicks) / particleMaxAge, 0.0f, 1.0f);
        float pr = particleRed + (dr - particleRed) * fs;
        float pg = particleGreen + (dg - particleGreen) * fs;
        float pb = particleBlue + (db - particleBlue) * fs;
        int i = getBrightnessForRender(partialTicks);
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        float f5 = (float)(prevPosX + (posX - prevPosX) * partialTicks - FXGeneric.interpPosX);
        float f6 = (float)(prevPosY + (posY - prevPosY) * partialTicks - FXGeneric.interpPosY);
        float f7 = (float)(prevPosZ + (posZ - prevPosZ) * partialTicks - FXGeneric.interpPosZ);
        if (angled) {
            Tessellator.getInstance().draw();
            GL11.glPushMatrix();
            GL11.glTranslated(f5, f6, f7);
            GL11.glRotatef(-angleYaw + 90.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(anglePitch + 90.0f, 1.0f, 0.0f, 0.0f);
            if (particleAngle != 0.0f) {
                float f8 = particleAngle + (particleAngle - prevParticleAngle) * partialTicks;
                GL11.glRotated(f8 * 57.29577951308232, 0.0, 0.0, 1.0);
            }
            wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            wr.pos(-ts, -ts, 0.0).tex(tx2, ty2).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(-ts, ts, 0.0).tex(tx2, ty1).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(ts, ts, 0.0).tex(tx1, ty1).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(ts, -ts, 0.0).tex(tx1, ty2).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            Tessellator.getInstance().draw();
            GL11.glPopMatrix();
            wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }
        else {
            Vec3d[] avec3d = { new Vec3d(-rotationX * ts - rotationXY * ts, -rotationZ * ts, -rotationYZ * ts - rotationXZ * ts), new Vec3d(-rotationX * ts + rotationXY * ts, rotationZ * ts, -rotationYZ * ts + rotationXZ * ts), new Vec3d(rotationX * ts + rotationXY * ts, rotationZ * ts, rotationYZ * ts + rotationXZ * ts), new Vec3d(rotationX * ts - rotationXY * ts, -rotationZ * ts, rotationYZ * ts - rotationXZ * ts) };
            if (particleAngle != 0.0f) {
                float f9 = particleAngle + (particleAngle - prevParticleAngle) * partialTicks;
                float f10 = MathHelper.cos(f9 * 0.5f);
                float f11 = MathHelper.sin(f9 * 0.5f) * (float)FXGeneric.cameraViewDir.x;
                float f12 = MathHelper.sin(f9 * 0.5f) * (float)FXGeneric.cameraViewDir.y;
                float f13 = MathHelper.sin(f9 * 0.5f) * (float)FXGeneric.cameraViewDir.z;
                Vec3d vec3d = new Vec3d(f11, f12, f13);
                for (int l = 0; l < 4; ++l) {
                    avec3d[l] = vec3d.scale(2.0 * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale(f10 * f10 - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale(2.0f * f10));
                }
            }
            wr.pos(f5 + avec3d[0].x, f6 + avec3d[0].y, f7 + avec3d[0].z).tex(tx2, ty2).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(f5 + avec3d[1].x, f6 + avec3d[1].y, f7 + avec3d[1].z).tex(tx2, ty1).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(f5 + avec3d[2].x, f6 + avec3d[2].y, f7 + avec3d[2].z).tex(tx1, ty1).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
            wr.pos(f5 + avec3d[3].x, f6 + avec3d[3].y, f7 + avec3d[3].z).tex(tx1, ty2).color(pr, pg, pb, particleAlpha).lightmap(j, k).endVertex();
        }
    }
~~~

### setParticles

~~~java
public void setParticles(int startParticle, int numParticles, int particleInc) {
        this.numParticles = numParticles;
        this.particleInc = particleInc;
        setParticleTextureIndex(this.startParticle = startParticle);
    }
~~~

### setGridSize

~~~java
public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }
~~~

### setScale

~~~java
public void setScale(float... scale) {
        particleScale = scale[0];
        scaleKeys = scale;
    }
~~~

### setAlphaF

~~~java
public void setAlphaF(float... a1) {
        super.setAlphaF(a1[0]);
        alphaKeys = a1;
    }
~~~

~~~java
public void setAlphaF(float a1) {
        super.setAlphaF(a1);
        (alphaKeys = new float[1])[0] = a1;
    }
~~~

## ParticleEngine texture usage

~~~java
public class ParticleEngine
    public ParticleEngine() {
            renderer.bindTexture(ParticleEngine.particleTexture);
                if (ParticleEngine.particles[layer].containsKey(dim)) {
                    ArrayList<Particle> parts = ParticleEngine.particles[layer].get(dim);
        renderer.bindTexture(ParticleEngine.particleTexture);
            if (ParticleEngine.particles[layer].containsKey(dim)) {
                ArrayList<Particle> parts = ParticleEngine.particles[layer].get(dim);
    public static void addEffect(World world, Particle fx) {
        addEffect(world.provider.getDimension(), fx);
    public static void addEffect(int dim, Particle fx) {
        if (!ParticleEngine.particles[fx.getFXLayer()].containsKey(dim)) {
            ParticleEngine.particles[fx.getFXLayer()].put(dim, new ArrayList<Particle>());
        ArrayList<Particle> parts = ParticleEngine.particles[fx.getFXLayer()].get(dim);
        ParticleEngine.particles[fx.getFXLayer()].put(dim, parts);
    public static void addEffectWithDelay(World world, Particle fx, int delay) {
        ParticleEngine.particlesDelayed.add(new ParticleDelay(fx, world.provider.getDimension(), delay));
                Iterator<ParticleDelay> i = ParticleEngine.particlesDelayed.iterator();
                            addEffect(pd.dim, pd.particle);
                if (ParticleEngine.particles[layer].containsKey(dim)) {
                    ArrayList<Particle> parts = ParticleEngine.particles[layer].get(dim);
                            ParticleEngine.particles[layer].put(dim, parts);
        particleTexture = new ResourceLocation("thaumcraft", "textures/misc/particles.png");
        ParticleEngine.particles = new HashMap[] { new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap() };
        ParticleEngine.particlesDelayed = new ArrayList<ParticleDelay>();
~~~

## Pattern counts

| Pattern | Count |
|---|---:|
| drawWispyMotes | 12 |
| new FXGeneric | 45 |
| ParticleEngine.addEffect | 65 |
| ParticleEngine.addEffectWithDelay | 16 |
| particleTextureIndexY | 9 |
| particleTextureIndexX | 10 |
| setAlphaF | 61 |
| setFinalFrames | 7 |
| setGravity | 32 |
| setGridSize | 27 |
| setLayer | 28 |
| setParticles | 40 |
| setParticleTextureIndex | 9 |
| setRandomMovementScale | 21 |
| setRotationSpeed | 25 |
| setScale | 53 |
| setSlowDown | 21 |
| setWind | 10 |
| textures/misc/particles.png | 1 |

## drawWispyMotes call sites

| File | Line | Method guess | Code |
|---|---:|---|---|
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 348 | public void drawWispyMotesOnBlock(BlockPos pp, int age, float grav) { | public void drawWispyMotesOnBlock(BlockPos pp, int age, float grav) { |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 349 | public void drawWispyMotesOnBlock(BlockPos pp, int age, float grav) { | drawWispyMotes(pp.getX() + getWorld().rand.nextFloat(), pp.getY(), pp.getZ() + getWorld().rand.nextFloat(), 0.0, 0.0, 0.0, age, 0.4f + getWorld().rand.nextFloat() * 0.6f, 0.6f + getWorld().rand.nextFloat() * 0.4f, 0.6f + getWorld().rand.nextFloat() * 0.4f, grav); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 352 | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) { | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) { |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 353 | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) { | drawWispyMotes(d, e, f, vx, vy, vz, age, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, grav); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 356 | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 511 | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | drawWispyMotes(x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx, vy, vz, 15 + getWorld().rand.nextInt(10), -0.01f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 568 | public void pechsCurseTick(double posX, double posY, double posZ) { | drawWispyMotes(posX, posY, posZ, 0.0, 0.0, 0.0, 10 + getWorld().rand.nextInt(10), -0.01f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 965 | public void drawWispyMotesEntity(double x, double y, double z, Entity e, float r, float g, float b) { | public void drawWispyMotesEntity(double x, double y, double z, Entity e, float r, float g, float b) { |
| src/main/java/thaumcraft/common/blocks/world/BlockGrassAmbient.java | 43 | public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) { | FXDispatcher.INSTANCE.drawWispyMotesOnBlock(pp.up(), 400, -0.01f); |
| src/main/java/thaumcraft/common/blocks/world/plants/BlockPlantShimmerleaf.java | 44 | public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) { | FXDispatcher.INSTANCE.drawWispyMotes(xr, yr, zr, rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01, 10, 0.3f + world.rand.nextFloat() * 0.3f, 0.7f + world.rand.nextFloat() * 0.3f, 0.7f + world.rand.nextFloat() * 0.3f, 0.0f); |
| src/main/java/thaumcraft/common/blocks/world/plants/BlockPlantVishroom.java | 52 | public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) { | FXDispatcher.INSTANCE.drawWispyMotes(xr, yr, zr, 0.0, 0.0, 0.0, 10, 0.5f, 0.3f, 0.8f, 0.001f); |
| src/main/java/thaumcraft/common/tiles/devices/TileMirror.java | 262 | public boolean receiveClientEvent(int i, int j) { | FXDispatcher.INSTANCE.drawWispyMotes(xx, yy, zz, face.getFrontOffsetX() / 50.0 + world.rand.nextGaussian() * 0.01, face.getFrontOffsetY() / 50.0 + world.rand.nextGaussian() * 0.01, face.getFrontOffsetZ() / 50.0 + world.rand.nextGaussian() * 0.01, MathHelper.getInt(world.rand, 10, 30), world.rand.nextFloat() / 3.0f, 0.0f, world.rand.nextFloat() / 2.0f, (float)(world.rand.nextGaussian() * 0.01)); |

## FXDispatcher methods using FXGeneric or ParticleEngine

Full extracted methods are also written to 06_docs/raw_legacy/fxdispatcher_fxgeneric_methods.txt.

| Method | Contains drawWispyMotes | Contains setGridSize | Contains setParticles | Contains ParticleEngine.addEffect |
|---|---:|---:|---:|---:|
| public void drawFireMote(float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float alpha, float scale) {         boolean bb = getWorld().rand.nextBoolean();         FXFireMote glow = new FXFireMote(getWorld(), x, y, z, vx, vy, vz, r, g, b, bb ? (scale / 3.0f) : scale, bb ? 1 : 0);         glow.setAlphaF(alpha);         ParticleEngine.addEffect(getWorld(), glow);     } | False | False | False | True |
| public void drawAlumentum(float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float alpha, float scale) {         FXFireMote glow = new FXFireMote(getWorld(), x, y, z, vx, vy, vz, r, g, b, scale, 1);         glow.setAlphaF(alpha);         ParticleEngine.addEffect(getWorld(), glow);     } | False | False | False | True |
| public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, vx, vy, vz);         fb.setMaxAge(80 + getWorld().rand.nextInt(20));         fb.setRBGColorF(0.4f + getWorld().rand.nextFloat() * 0.2f, 0.1f + getWorld().rand.nextFloat() * 0.3f, 0.5f + getWorld().rand.nextFloat() * 0.2f);         fb.setAlphaF(0.75f, 0.0f);         fb.setGridSize(16);         fb.setParticles(57 + getWorld().rand.nextInt(3), 1, 1);         fb.setScale(scale, scale / 4.0f);         fb.setLayer(1);         fb.setSlowDown(0.9750000238418579);         fb.setGravity(0.2f);         fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | True | True | True |
| public void drawLightningFlash(double x, double y, double z, float r, float g, float b, float alpha, float scale) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);         fb.setMaxAge(5 + getWorld().rand.nextInt(5));         fb.setGridSize(16);         fb.setRBGColorF(r, g, b);         fb.setAlphaF(alpha, 0.0f);         fb.setParticles(108 + getWorld().rand.nextInt(4), 1, 1);         fb.setScale(scale);         fb.setLayer(0);         fb.setRotationSpeed(getWorld().rand.nextFloat(), 0.0f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | True | True | True |
| public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, mx, my, mz);         fb.setMaxAge(part.age);         fb.setRBGColorF(part.redStart, part.greenStart, part.blueStart, part.redEnd, part.greenEnd, part.blueEnd);         fb.setAlphaF(part.alpha);         fb.setLoop(part.loop);         fb.setParticles(part.partStart, part.partNum, part.partInc);         fb.setScale(part.scale);         fb.setLayer(part.layer);         fb.setRotationSpeed(part.rotstart, part.rot);         fb.setSlowDown(part.slowDown);         fb.setGravity(part.grav);         fb.setGridSize(part.grid);         ParticleEngine.addEffectWithDelay(getWorld(), fb, part.delay);     } | False | True | True | True |
| public void drawGenericParticles(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);         fb.setMaxAge(age);         fb.setRBGColorF(r, g, b);         fb.setAlphaF(alpha);         fb.setLoop(loop);         fb.setParticles(start, num, inc);         fb.setScale(scale);         fb.setLayer(layer);         fb.setRotationSpeed(rot);         ParticleEngine.addEffectWithDelay(getWorld(), fb, delay);     } | False | False | True | True |
| public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);         fb.setGridSize(16);         fb.setMaxAge(age);         fb.setRBGColorF(r, g, b);         fb.setAlphaF(alpha);         fb.setLoop(loop);         fb.setParticles(start, num, inc);         fb.setScale(scale);         fb.setLayer(layer);         fb.setRotationSpeed(rot);         ParticleEngine.addEffectWithDelay(getWorld(), fb, delay);     } | False | True | True | True |
| public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);         fb.setMaxAge(200 + getWorld().rand.nextInt(100));         fb.setRBGColorF(0.5f, 0.5f, 0.2f);         fb.setAlphaF(0.3f, 0.0f);         fb.setGridSize(16);         fb.setParticles(56, 1, 1);         fb.setScale(2.0f, 5.0f);         fb.setLayer(0);         fb.setSlowDown(1.0);         fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | True | True | True |
| public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);         fb.setMaxAge(life + getWorld().rand.nextInt(life));         fb.setRBGColorF(0.5f, 0.2f, 0.5f);         fb.setAlphaF(0.3f, 0.0f);         fb.setGridSize(16);         fb.setParticles(72 + getWorld().rand.nextInt(4), 1, 1);         fb.setScale(1.0f, 10.0f);         fb.setLayer(0);         fb.setSlowDown(1.01);         fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | True | True | True |
| public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) {         try {             FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);             fb.setMaxAge(20 + getWorld().rand.nextInt(5));             fb.setAlphaF(0.3f, 0.0f);             fb.setGridSize(16);             fb.setParticles(56, 1, 1);             fb.setScale(1.5f, 3.0f, 8.0f);             fb.setLayer(0);             fb.setSlowDown(1.0);             fb.setWind(0.001);             fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);             ParticleEngine.addEffect(getWorld(), fb);         }         catch (Exception ex) {}     } | False | True | True | True |
| public void drawPollutionParticles(BlockPos p) {         float x = p.getX() + 0.2f + getWorld().rand.nextFloat() * 0.6f;         float y = p.getY() + 0.2f + getWorld().rand.nextFloat() * 0.6f;         float z = p.getZ() + 0.2f + getWorld().rand.nextFloat() * 0.6f;         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005, 0.02, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005);         fb.setMaxAge(100 + getWorld().rand.nextInt(60));         fb.setRBGColorF(1.0f, 0.3f, 0.9f);         fb.setAlphaF(0.5f, 0.0f);         fb.setGridSize(16);         fb.setParticles(56, 1, 1);         fb.setScale(2.0f, 5.0f);         fb.setLayer(1);         fb.setSlowDown(1.0);         fb.setWind(0.001);         fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | True | True | True |
| public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) {         boolean sp = rand.nextFloat() < 0.2;         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);         int age = baseAge * 4 + getWorld().rand.nextInt(baseAge);         fb.setMaxAge(age);         fb.setRBGColorF(r, g, b);         fb.setAlphaF(0.0f, 1.0f, 0.0f);         fb.setParticles(sp ? 320 : 512, 16, 1);         fb.setLoop(true);         fb.setGravity(grav);         fb.setScale(scale, scale * 2.0f, scale);         fb.setLayer(0);         fb.setSlowDown(decay);         fb.setRandomMovementScale(5.0E-5f, 0.0f, 5.0E-5f);         ParticleEngine.addEffectWithDelay(getWorld(), fb, delay);     } | False | False | True | True |
| public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) {         boolean sp = rand.nextFloat() < 0.2;         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);         int age = baseAge * 4 + getWorld().rand.nextInt(baseAge);         fb.setMaxAge(age);         fb.setRBGColorF(r, g, b);         float[] alphas = new float[6 + rand.nextInt(age / 3)];         for (int a = 1; a < alphas.length - 1; ++a) {             alphas[a] = rand.nextFloat();         }         fb.setAlphaF(alphas);         fb.setParticles(sp ? 320 : 512, 16, 1);         fb.setLoop(true);         fb.setGravity(grav);         fb.setScale(scale, scale * 2.0f);         fb.setLayer(0);         fb.setSlowDown(decay);         fb.setRandomMovementScale(5.0E-4f, 0.001f, 5.0E-4f);         fb.setWind(5.0E-4);         ParticleEngine.addEffectWithDelay(getWorld(), fb, delay);     } | False | False | True | True |
| public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) {         boolean sp = rand.nextFloat() < 0.2;         FXGenericGui fb = new FXGenericGui(getWorld(), x, y, 0.0, x2, y2, 0.0);         fb.setMaxAge(32 + getWorld().rand.nextInt(8));         fb.setRBGColorF(r, g, b);         fb.setAlphaF(0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f);         fb.setParticles(sp ? 320 : 512, 16, 1);         fb.setLoop(true);         fb.setGravity(grav);         fb.setScale(scale, scale * 2.0f);         fb.setNoClip(false);         fb.setLayer(4);         fb.setSlowDown(decay);         fb.setRandomMovementScale(0.025f, 0.025f, 0.0f);         ParticleEngine.addEffectWithDelay(getWorld(), fb, delay);     } | False | False | True | True |
| public void drawBlockMistParticles(BlockPos p, int c) {         AxisAlignedBB bs = getWorld().getBlockState(p).getBoundingBox(getWorld(), p);         Color color = new Color(c);         for (int a = 0; a < 8; ++a) {             double x = p.getX() + bs.minX + getWorld().rand.nextFloat() * (bs.maxX - bs.minX);             double y = p.getY() + bs.minY + getWorld().rand.nextFloat() * (bs.maxY - bs.minY);             double z = p.getZ() + bs.minZ + getWorld().rand.nextFloat() * (bs.maxZ - bs.minZ);             FXGeneric fb = new FXGeneric(getWorld(), x, y, z, getWorld().rand.nextGaussian() * 0.01, getWorld().rand.nextFloat() * 0.075, getWorld().rand.nextGaussian() * 0.01);             fb.setMaxAge(50 + getWorld().rand.nextInt(25));             fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);             fb.setAlphaF(0.0f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.0f);             fb.setGridSize(16);             fb.setParticles(56, 1, 1);             fb.setScale(5.0f, 1.0f);             fb.setLayer(0);             fb.setSlowDown(1.0);             fb.setGravity(0.1f);             fb.setWind(0.001);             fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);             ParticleEngine.addEffect(getWorld(), fb);         }     } | False | True | True | True |
| public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) {         Color color = new Color(c);         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, mx, my, mz);         fb.setMaxAge(20 + getWorld().rand.nextInt(10));         fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);         fb.setAlphaF(0.0f, 0.66f, 0.0f);         fb.setGridSize(16);         fb.setParticles(56 + getWorld().rand.nextInt(4), 1, 1);         fb.setScale(5.0f + getWorld().rand.nextFloat(), 10.0f + getWorld().rand.nextFloat());         fb.setLayer(0);         fb.setSlowDown(0.99);         fb.setWind(0.001);         fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -0.25f : 0.25f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | True | True | True |
| public void drawWispyMotesOnBlock(BlockPos pp, int age, float grav) {         drawWispyMotes(pp.getX() + getWorld().rand.nextFloat(), pp.getY(), pp.getZ() + getWorld().rand.nextFloat(), 0.0, 0.0, 0.0, age, 0.4f + getWorld().rand.nextFloat() * 0.6f, 0.6f + getWorld().rand.nextFloat() * 0.4f, 0.6f + getWorld().rand.nextFloat() * 0.4f, grav);     } | True | False | False | False |
| public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) {         drawWispyMotes(d, e, f, vx, vy, vz, age, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, grav);     } | True | False | False | False |
| public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) {         FXGeneric fb = new FXGeneric(getWorld(), d, e, f, vx, vy, vz);         fb.setMaxAge((int)(age + age / 2 * getWorld().rand.nextFloat()));         fb.setRBGColorF(r, g, b);         fb.setAlphaF(0.0f, 0.6f, 0.6f, 0.0f);         fb.setGridSize(64);         fb.setParticles(512, 16, 1);         fb.setScale(1.0f, 0.5f);         fb.setLoop(true);         fb.setWind(0.001);         fb.setGravity(grav);         fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);         ParticleEngine.addEffect(getWorld(), fb);     } | True | True | True | True |
| public void drawBlockMistParticlesFlat(BlockPos p, int c) {         Block bs = getWorld().getBlockState(p).getBlock();         Color color = new Color(c);         for (int a = 0; a < 6; ++a) {             double x = p.getX() + getWorld().rand.nextFloat();             double y = p.getY() + getWorld().rand.nextFloat() * 0.125f;             double z = p.getZ() + getWorld().rand.nextFloat();             FXGeneric fb = new FXGeneric(getWorld(), x, y, z, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005, 0.005, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005);             fb.setMaxAge(400 + getWorld().rand.nextInt(100));             fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);             fb.setAlphaF(1.0f, 0.0f);             fb.setGridSize(8);             fb.setParticles(24, 1, 1);             fb.setScale(2.0f, 5.0f);             fb.setLayer(0);             fb.setSlowDown(1.0);             fb.setWind(0.001);             fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);             ParticleEngine.addEffect(getWorld(), fb);         }     } | False | True | True | True |
| public void crucibleBubble(float x, float y, float z, float cr, float cg, float cb) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);         fb.setMaxAge(15 + getWorld().rand.nextInt(10));         fb.setScale(getWorld().rand.nextFloat() * 0.3f + 0.3f);         fb.setRBGColorF(cr, cg, cb);         fb.setRandomMovementScale(0.002f, 0.002f, 0.002f);         fb.setGravity(-0.001f);         fb.setParticle(64);         fb.setFinalFrames(65, 66, 66);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void crucibleBoil(BlockPos pos, TileCrucible tile, int j) {         for (int a = 0; a < 2; ++a) {             FXGeneric fb = new FXGeneric(getWorld(), pos.getX() + 0.2f + getWorld().rand.nextFloat() * 0.6f, pos.getY() + 0.1f + tile.getFluidHeight(), pos.getZ() + 0.2f + getWorld().rand.nextFloat() * 0.6f, 0.0, 0.002, 0.0);             fb.setMaxAge((int)(7.0 + 8.0 / (Math.random() * 0.8 + 0.2)));             fb.setScale(getWorld().rand.nextFloat() * 0.3f + 0.2f);             if (tile.aspects.size() == 0) {                 fb.setRBGColorF(1.0f, 1.0f, 1.0f);             }             else {                 Color color = new Color(tile.aspects.getAspects()[getWorld().rand.nextInt(tile.aspects.getAspects().length)].getColor());                 fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);             }             fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);             fb.setGravity(-0.025f * j);             fb.setParticle(64);             fb.setFinalFrames(65, 66);             ParticleEngine.addEffect(getWorld(), fb);         }     } | False | False | False | True |
| public void crucibleFroth(float x, float y, float z) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);         fb.setMaxAge(4 + getWorld().rand.nextInt(3));         fb.setScale(getWorld().rand.nextFloat() * 0.2f + 0.2f);         fb.setRBGColorF(0.5f, 0.5f, 0.7f);         fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);         fb.setGravity(0.1f);         fb.setParticle(64);         fb.setFinalFrames(65, 66);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void crucibleFrothDown(float x, float y, float z) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);         fb.setMaxAge(12 + getWorld().rand.nextInt(12));         fb.setScale(getWorld().rand.nextFloat() * 0.2f + 0.4f);         fb.setRBGColorF(0.25f, 0.0f, 0.75f);         fb.setAlphaF(0.8f);         fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);         fb.setGravity(0.05f);         fb.setNoClip(false);         fb.setParticle(73);         fb.setFinalFrames(65, 66);         fb.setLayer(1);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) {         if (sound) {             getWorld().playSound(x, y, z, SoundsTC.poof, SoundCategory.BLOCKS, 0.4f, 1.0f + (float) getWorld().rand.nextGaussian() * 0.05f, false);         }         for (int a = 0; a < 6 + getWorld().rand.nextInt(3) + 2; ++a) {             double vx = (0.05f + getWorld().rand.nextFloat() * 0.05f) * (getWorld().rand.nextBoolean() ? -1 : 1);             double vy = (0.05f + getWorld().rand.nextFloat() * 0.05f) * (getWorld().rand.nextBoolean() ? -1 : 1);             double vz = (0.05f + getWorld().rand.nextFloat() * 0.05f) * (getWorld().rand.nextBoolean() ? -1 : 1);             if (side != null) {                 vx += side.getFrontOffsetX() * 0.1f;                 vy += side.getFrontOffsetY() * 0.1f;                 vz += side.getFrontOffsetZ() * 0.1f;             }             FXGeneric fb2 = new FXGeneric(getWorld(), x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx / 2.0, vy / 2.0, vz / 2.0);             fb2.setMaxAge(20 + getWorld().rand.nextInt(15));             fb2.setRBGColorF(MathHelper.clamp(r * (1.0f + (float) getWorld().rand.nextGaussian() * 0.1f), 0.0f, 1.0f), MathHelper.clamp(g * (1.0f + (float) getWorld().rand.nextGaussian() * 0.1f), 0.0f, 1.0f), MathHelper.clamp(b * (1.0f + (float) getWorld().rand.nextGaussian() * 0.1f), 0.0f, 1.0f));             fb2.setAlphaF(1.0f, 0.1f);             fb2.setGridSize(16);             fb2.setParticles(123, 5, 1);             fb2.setScale(3.0f, 4.0f + getWorld().rand.nextFloat() * 3.0f);             fb2.setLayer(1);             fb2.setSlowDown(0.7);             fb2.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f);             ParticleEngine.addEffect(getWorld(), fb2);         }         if (flair) {             for (int a = 0; a < 2 + getWorld().rand.nextInt(3); ++a) {                 double vx = (0.025f + getWorld().rand.nextFloat() * 0.025f) * (getWorld().rand.nextBoolean() ? -1 : 1);                 double vy = (0.025f + getWorld().rand.nextFloat() * 0.025f) * (getWorld().rand.nextBoolean() ? -1 : 1);                 double vz = (0.025f + getWorld().rand.nextFloat() * 0.025f) * (getWorld().rand.nextBoolean() ? -1 : 1);                 drawWispyMotes(x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx, vy, vz, 15 + getWorld().rand.nextInt(10), -0.01f);             }             FXGeneric fb3 = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0);             fb3.setMaxAge(10 + getWorld().rand.nextInt(5));             fb3.setRBGColorF(1.0f, 0.9f, 1.0f);             fb3.setAlphaF(1.0f, 0.0f);             fb3.setGridSize(16);             fb3.setParticles(77, 1, 1);             fb3.setScale(10.0f + getWorld().rand.nextFloat() * 2.0f, 0.0f);             fb3.setLayer(0);             fb3.setRotationSpeed(getWorld().rand.nextFloat(), (float) getWorld().rand.nextGaussian());             ParticleEngine.addEffect(getWorld(), fb3);         }         for (int a = 0; a < (flair ? 2 : 0) + getWorld().rand.nextInt(3); ++a) {             drawCurlyWisp(x, y, z, 0.0, 0.0, 0.0, 1.0f, (0.9f + getWorld().rand.nextFloat() * 0.1f + r) / 2.0f, (0.1f + g) / 2.0f, (0.5f + getWorld().rand.nextFloat() * 0.1f + b) / 2.0f, 0.75f, side, a, 0, 0);         }     } | True | True | True | True |
| public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, EnumFacing side, int seed, int layer, int delay) {         if (getWorld() == null) {             return;         }         vx += (0.0025f + getWorld().rand.nextFloat() * 0.005f) * (getWorld().rand.nextBoolean() ? -1 : 1);         vy += (0.0025f + getWorld().rand.nextFloat() * 0.005f) * (getWorld().rand.nextBoolean() ? -1 : 1);         vz += (0.0025f + getWorld().rand.nextFloat() * 0.005f) * (getWorld().rand.nextBoolean() ? -1 : 1);         if (side != null) {             vx += side.getFrontOffsetX() * 0.025f;             vy += side.getFrontOffsetY() * 0.025f;             vz += side.getFrontOffsetZ() * 0.025f;         }         FXGeneric fb2 = new FXGeneric(getWorld(), x + vx * 5.0, y + vy * 5.0, z + vz * 5.0, vx, vy, vz);         if (seed > 0 && getWorld().rand.nextBoolean()) {             fb2.setAngles(90.0f * (float) getWorld().rand.nextGaussian(), 90.0f * (float) getWorld().rand.nextGaussian());         }         fb2.setMaxAge(25 + getWorld().rand.nextInt(20 + 20 * seed));         fb2.setRBGColorF(r, g, b, 0.1f, 0.0f, 0.1f);         fb2.setAlphaF(a, 0.0f);         fb2.setGridSize(16);         fb2.setParticles(60 + getWorld().rand.nextInt(4), 1, 1);         fb2.setScale(5.0f * scale, (10.0f + getWorld().rand.nextFloat() * 4.0f) * scale);         fb2.setLayer(layer);         fb2.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? (-2.0f - getWorld().rand.nextFloat() * 2.0f) : (2.0f + getWorld().rand.nextFloat() * 2.0f));         ParticleEngine.addEffectWithDelay(getWorld(), fb2, delay);     } | False | True | True | True |
| public void pechsCurseTick(double posX, double posY, double posZ) {         FXGeneric fb2 = new FXGeneric(getWorld(), posX, posY, posZ, 0.0, 0.0, 0.0);         fb2.setAngles(90.0f * (float) getWorld().rand.nextGaussian(), 90.0f * (float) getWorld().rand.nextGaussian());         fb2.setMaxAge(50 + getWorld().rand.nextInt(50));         fb2.setRBGColorF(0.9f, 0.1f, 0.5f, 0.1f + getWorld().rand.nextFloat() * 0.1f, 0.0f, 0.5f + getWorld().rand.nextFloat() * 0.1f);         fb2.setAlphaF(0.75f, 0.0f);         fb2.setGridSize(8);         fb2.setParticles(28 + getWorld().rand.nextInt(4), 1, 1);         fb2.setScale(3.0f, 5.0f + getWorld().rand.nextFloat() * 2.0f);         fb2.setLayer(0);         fb2.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? (-3.0f - getWorld().rand.nextFloat() * 3.0f) : (3.0f + getWorld().rand.nextFloat() * 3.0f));         ParticleEngine.addEffect(getWorld(), fb2);         drawWispyMotes(posX, posY, posZ, 0.0, 0.0, 0.0, 10 + getWorld().rand.nextInt(10), -0.01f);     } | True | True | True | True |
| public void visSparkle(int x, int y, int z, int x2, int y2, int z2, int color) {         FXVisSparkle fb = new FXVisSparkle(getWorld(), x + getWorld().rand.nextFloat(), y + getWorld().rand.nextFloat(), z + getWorld().rand.nextFloat(), x2 + 0.4 + getWorld().rand.nextFloat() * 0.2f, y2 + 0.4 + getWorld().rand.nextFloat() * 0.2f, z2 + 0.4 + getWorld().rand.nextFloat() * 0.2f);         Color c = new Color(color);         fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void voidStreak(double x, double y, double z, double x2, double y2, double z2, int seed, float scale) {         FXVoidStream fb = new FXVoidStream(getWorld(), x, y, z, x2, y2, z2, seed, scale);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void drawInfusionParticles3(double x, double y, double z, int x2, int y2, int z2) {         FXBoreSparkle fb = new FXBoreSparkle(getWorld(), x, y, z, x2 + 0.5, y2 - 0.5, z2 + 0.5);         fb.setRBGColorF(0.4f + getWorld().rand.nextFloat() * 0.2f, 0.2f, 0.6f + getWorld().rand.nextFloat() * 0.3f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void drawInfusionParticles4(double x, double y, double z, int x2, int y2, int z2) {         FXBoreSparkle fb = new FXBoreSparkle(getWorld(), x, y, z, x2 + 0.5, y2 - 0.5, z2 + 0.5);         fb.setRBGColorF(0.2f, 0.6f + getWorld().rand.nextFloat() * 0.3f, 0.3f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void drawVentParticles(double x, double y, double z, double x2, double y2, double z2, int color) {         FXVent fb = new FXVent(getWorld(), x, y, z, x2, y2, z2, color);         fb.setAlphaF(0.4f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void drawVentParticles(double x, double y, double z, double x2, double y2, double z2, int color, float scale) {         FXVent fb = new FXVent(getWorld(), x, y, z, x2, y2, z2, color);         fb.setAlphaF(0.4f);         fb.setScale(scale);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void drawVentParticles2(double x, double y, double z, double x2, double y2, double z2, int color, float scale) {         FXVent2 fb = new FXVent2(getWorld(), x, y, z, x2, y2, z2, color);         fb.setAlphaF(0.4f);         fb.setScale(scale);         ParticleEngine.addEffect(getWorld(), fb);         if (getWorld().rand.nextInt(6) < 2) {             drawGenericParticles(x, y, z, x2 / 2.0, y2 / 2.0, z2 / 2.0, 1.0f, 0.7f, 0.2f, 0.9f, true, 320, 16, 1, 10 + getWorld().rand.nextInt(4), 0, 0.25f + getWorld().rand.nextFloat() * 0.1f, 0.0f, 0);         }     } | False | False | False | True |
| public void spark(double d, double e, double f, float size, float r, float g, float b, float a) {         FXGeneric fb = new FXGeneric(getWorld(), d, e, f, 0.0, 0.0, 0.0);         fb.setMaxAge(5 + getWorld().rand.nextInt(5));         fb.setAlphaF(a);         fb.setRBGColorF(r, g, b);         fb.setGridSize(16);         fb.setParticles(8 + getWorld().rand.nextInt(3) * 16, 8, 1);         fb.setScale(size);         fb.setFlipped(getWorld().rand.nextBoolean());         ParticleEngine.addEffect(getWorld(), fb);     } | False | True | True | True |
| public void smokeSpiral(double x, double y, double z, float rad, int start, int miny, int color) {         FXSmokeSpiral fx = new FXSmokeSpiral(getWorld(), x, y, z, rad, start, miny);         Color c = new Color(color);         fx.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);         ParticleEngine.addEffect(getWorld(), fx);     } | False | False | False | True |
| public void wispFXEG(double posX, double posY, double posZ, Entity target) {         for (int a = 0; a < 2; ++a) {             FXWispEG ef = new FXWispEG(getWorld(), posX, posY, posZ, target);             ParticleEngine.addEffect(getWorld(), ef);         }     } | False | False | False | True |
| public void burst(double sx, double sy, double sz, float size) {         FXGeneric fb = new FXGeneric(getWorld(), sx, sy, sz, 0.0, 0.0, 0.0);         fb.setMaxAge(31);         fb.setGridSize(16);         fb.setParticles(208, 31, 1);         fb.setScale(size);         ParticleEngine.addEffect(getWorld(), fb);     } | False | True | True | True |
| public void boreDigFx(int x, int y, int z, Entity e, IBlockState bi, int md, int delay) {         float p = 50.0f;         for (int a = 0; a < p / delay; ++a) {             if (getWorld().rand.nextInt(4) == 0) {                 FXBoreSparkle fb = new FXBoreSparkle(getWorld(), x + getWorld().rand.nextFloat(), y + getWorld().rand.nextFloat(), z + getWorld().rand.nextFloat(), e);                 ParticleEngine.addEffect(getWorld(), fb);             }             else {                 FXBoreParticles fb2 = new FXBoreParticles(getWorld(), x + getWorld().rand.nextFloat(), y + getWorld().rand.nextFloat(), z + getWorld().rand.nextFloat(), e.posX, e.posY, e.posZ, bi, md);                 fb2.setTarget(e);                 FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb2);             }         }     } | False | False | False | True |
| public void essentiaTrailFx(BlockPos p1, BlockPos p2, int count, int color, float scale, int ext) {         FXEssentiaStream fb = new FXEssentiaStream(getWorld(), p1.getX() + 0.5, p1.getY() + 0.5, p1.getZ() + 0.5, p2.getX() + 0.5, p2.getY() + 0.5, p2.getZ() + 0.5, count, color, scale, ext, 0.0);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void boreTrailFx(BlockPos p1, Entity e, int count, int color, float scale, int ext) {         FXBoreStream fb = new FXBoreStream(getWorld(), p1.getX() + 0.5, p1.getY() + 0.5, p1.getZ() + 0.5, e, count, color, scale, ext, 0.0);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void essentiaDropFx(double x, double y, double z, float r, float g, float b, float alpha) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, getWorld().rand.nextGaussian() * 0.004999999888241291, getWorld().rand.nextGaussian() * 0.004999999888241291, getWorld().rand.nextGaussian() * 0.004999999888241291);         fb.setMaxAge(20 + getWorld().rand.nextInt(10));         fb.setRBGColorF(r, g, b);         fb.setAlphaF(alpha);         fb.setLoop(false);         fb.setParticles(25, 1, 1);         fb.setScale(0.4f + getWorld().rand.nextFloat() * 0.2f, 0.2f);         fb.setLayer(1);         fb.setGravity(0.01f);         fb.setRotationSpeed(0.0f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | True | True |
| public void jarSplashFx(double x, double y, double z) {         FXGeneric fb = new FXGeneric(getWorld(), x + getWorld().rand.nextGaussian() * 0.07500000298023224, y, z + getWorld().rand.nextGaussian() * 0.07500000298023224, getWorld().rand.nextGaussian() * 0.014999999664723873, 0.075f + getWorld().rand.nextFloat() * 0.05f, getWorld().rand.nextGaussian() * 0.014999999664723873);         fb.setMaxAge(20 + getWorld().rand.nextInt(10));         Color c = new Color(2650102);         fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);         fb.setAlphaF(0.5f);         fb.setLoop(false);         fb.setParticles(73, 1, 1);         fb.setScale(0.4f + getWorld().rand.nextFloat() * 0.3f, 0.0f);         fb.setLayer(1);         fb.setGravity(0.3f);         fb.setRotationSpeed(0.0f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | True | True |
| public void waterTrailFx(BlockPos p1, BlockPos p2, int count, int color, float scale) {         FXEssentiaStream fb = new FXEssentiaStream(getWorld(), p1.getX() + 0.5, p1.getY() + 0.66, p1.getZ() + 0.5, p2.getX() + 0.5, p2.getY() + 0.5, p2.getZ() + 0.5, count, color, scale, 0, 0.2);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void blockRunes(double x, double y, double z, float r, float g, float b, int dur, float grav) {         FXBlockRunes fb = new FXBlockRunes(getWorld(), x + 0.5, y + 0.5, z + 0.5, r, g, b, dur);         fb.setGravity(grav);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void blockRunes2(double x, double y, double z, float r, float g, float b, int dur, float grav) {         FXBlockRunes fb = new FXBlockRunes(getWorld(), x + 0.5, y + 0.5, z + 0.5, r, g, b, dur);         fb.setGravity(grav);         fb.setScale((float)(0.5 + getWorld().rand.nextGaussian() * 0.10000000149011612));         fb.setOffsetX(0.0);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public void drawSlash(double x, double y, double z, double x2, double y2, double z2, int dur) {         FXPlane fb = new FXPlane(getWorld(), x, y, z, x2, y2, z2, dur);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | False | True |
| public FXSwarm swarmParticleFX(Entity targetedEntity, float f1, float f2, float pg) {         FXSwarm fx = new FXSwarm(getWorld(), targetedEntity.posX + (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 2.0f, targetedEntity.posY + (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 2.0f, targetedEntity.posZ + (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 2.0f, targetedEntity, 0.8f + getWorld().rand.nextFloat() * 0.2f, getWorld().rand.nextFloat() * 0.4f, 1.0f - getWorld().rand.nextFloat() * 0.2f, f1, f2, pg);         ParticleEngine.addEffect(getWorld(), fx);         return fx;     } | False | False | False | True |
| public void cultistSpawn(double x, double y, double z, double a, double b, double c) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, a, b, c);         fb.setMaxAge(10 + getWorld().rand.nextInt(10));         fb.setRBGColorF(1.0f, 1.0f, 1.0f, 0.6f, 0.0f, 0.0f);         fb.setAlphaF(0.8f);         fb.setGridSize(16);         fb.setParticles(160, 6, 1);         fb.setScale(3.0f + getWorld().rand.nextFloat() * 2.0f);         fb.setLayer(1);         ParticleEngine.addEffect(getWorld(), fb);     } | False | True | True | True |
| public void drawWispyMotesEntity(double x, double y, double z, Entity e, float r, float g, float b) {         FXGenericP2E fb = new FXGenericP2E(getWorld(), x, y, z, e);         fb.setRBGColorF(r, g, b);         fb.setAlphaF(0.6f);         fb.setParticles(512, 16, 1);         fb.setLoop(true);         fb.setWind(0.001);         fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);         ParticleEngine.addEffect(getWorld(), fb);     } | True | False | True | True |
| public void drawWispParticles(double x, double y, double z, double x2, double y2, double z2, int color, int a) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);         fb.setMaxAge(10 + getWorld().rand.nextInt(5));         Color c = new Color(color);         fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);         fb.setAlphaF(0.5f);         fb.setLoop(true);         fb.setGridSize(64);         fb.setParticles(264, 8, 1);         fb.setScale(1.0f + getWorld().rand.nextFloat() * 0.25f, 0.05f);         fb.setWind(2.5E-4);         fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);         ParticleEngine.addEffectWithDelay(getWorld(), fb, a);     } | False | True | True | True |
| public void drawNitorCore(double x, double y, double z, double x2, double y2, double z2) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);         fb.setMaxAge(10);         fb.setRBGColorF(1.0f, 1.0f, 1.0f);         fb.setAlphaF(1.0f);         fb.setParticles(457, 1, 1);         fb.setScale(1.0f, 1.0f + (float) getWorld().rand.nextGaussian() * 0.1f, 1.0f);         fb.setLayer(1);         fb.setRandomMovementScale(2.0E-4f, 2.0E-4f, 2.0E-4f);         ParticleEngine.addEffect(getWorld(), fb);     } | False | False | True | True |
| public void drawNitorFlames(double x, double y, double z, double x2, double y2, double z2, int color, int a) {         FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2);         fb.setMaxAge(10 + getWorld().rand.nextInt(5));         Color c = new Color(color);         fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);         fb.setAlphaF(0.66f);         fb.setLoop(true);         fb.setGridSize(64);         fb.setParticles(264, 8, 1);         fb.setScale(3.0f + getWorld().rand.nextFloat(), 0.05f);         fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);         ParticleEngine.addEffectWithDelay(getWorld(), fb, a);     } | False | True | True | True |

## First 300 usage hits

Full CSV is written to 06_docs/raw_legacy/fx_legacy_usage_hits.csv.

| File | Line | Pattern | Method guess | Code |
|---|---:|---|---|---|
| src/main/java/thaumcraft/client/fx/beams/FXArc.java | 71 | new FXGeneric | public FXArc(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, double hg) { | FXGeneric fb = new FXGeneric(par1World, x + vt.x, y + vt.y, z + vt.z, 0.0, 0.0, 0.0); |
| src/main/java/thaumcraft/client/fx/beams/FXArc.java | 80 | setAlphaF | public FXArc(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, double hg) { | fb.setAlphaF(alphas); |
| src/main/java/thaumcraft/client/fx/beams/FXArc.java | 82 | setParticles | public FXArc(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, double hg) { | fb.setParticles(sp ? 320 : 512, 16, 1); |
| src/main/java/thaumcraft/client/fx/beams/FXArc.java | 84 | setGravity | public FXArc(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, double hg) { | fb.setGravity(sp ? 0.0f : 0.125f); |
| src/main/java/thaumcraft/client/fx/beams/FXArc.java | 85 | setScale | public FXArc(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, double hg) { | fb.setScale(0.5f, 0.125f); |
| src/main/java/thaumcraft/client/fx/beams/FXArc.java | 86 | setLayer | public FXArc(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, double hg) { | fb.setLayer(0); |
| src/main/java/thaumcraft/client/fx/beams/FXArc.java | 87 | setSlowDown | public FXArc(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, double hg) { | fb.setSlowDown(0.995); |
| src/main/java/thaumcraft/client/fx/beams/FXArc.java | 88 | setRandomMovementScale | public FXArc(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, double hg) { | fb.setRandomMovementScale(0.0025f, 0.001f, 0.0025f); |
| src/main/java/thaumcraft/client/fx/beams/FXArc.java | 89 | ParticleEngine.addEffectWithDelay | public FXArc(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, double hg) { | ParticleEngine.addEffectWithDelay(par1World, fb, 2 + rand.nextInt(3)); |
| src/main/java/thaumcraft/client/fx/beams/FXArc.java | 89 | ParticleEngine.addEffect | public FXArc(World par1World, double x, double y, double z, double tx, double ty, double tz, float red, float green, float blue, double hg) { | ParticleEngine.addEffectWithDelay(par1World, fb, 2 + rand.nextInt(3)); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 67 | setAlphaF | public void drawFireMote(float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float alpha, float scale) { | glow.setAlphaF(alpha); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 68 | ParticleEngine.addEffect | public void drawFireMote(float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float alpha, float scale) { | ParticleEngine.addEffect(getWorld(), glow); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 73 | setAlphaF | public void drawAlumentum(float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float alpha, float scale) { | glow.setAlphaF(alpha); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 74 | ParticleEngine.addEffect | public void drawAlumentum(float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float alpha, float scale) { | ParticleEngine.addEffect(getWorld(), glow); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 78 | new FXGeneric | public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, vx, vy, vz); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 81 | setAlphaF | public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) { | fb.setAlphaF(0.75f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 82 | setGridSize | public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) { | fb.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 83 | setParticles | public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) { | fb.setParticles(57 + getWorld().rand.nextInt(3), 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 84 | setScale | public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) { | fb.setScale(scale, scale / 4.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 85 | setLayer | public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) { | fb.setLayer(1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 86 | setSlowDown | public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) { | fb.setSlowDown(0.9750000238418579); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 87 | setGravity | public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) { | fb.setGravity(0.2f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 88 | setRotationSpeed | public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) { | fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 89 | ParticleEngine.addEffect | public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 93 | new FXGeneric | public void drawLightningFlash(double x, double y, double z, float r, float g, float b, float alpha, float scale) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 95 | setGridSize | public void drawLightningFlash(double x, double y, double z, float r, float g, float b, float alpha, float scale) { | fb.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 97 | setAlphaF | public void drawLightningFlash(double x, double y, double z, float r, float g, float b, float alpha, float scale) { | fb.setAlphaF(alpha, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 98 | setParticles | public void drawLightningFlash(double x, double y, double z, float r, float g, float b, float alpha, float scale) { | fb.setParticles(108 + getWorld().rand.nextInt(4), 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 99 | setScale | public void drawLightningFlash(double x, double y, double z, float r, float g, float b, float alpha, float scale) { | fb.setScale(scale); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 100 | setLayer | public void drawLightningFlash(double x, double y, double z, float r, float g, float b, float alpha, float scale) { | fb.setLayer(0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 101 | setRotationSpeed | public void drawLightningFlash(double x, double y, double z, float r, float g, float b, float alpha, float scale) { | fb.setRotationSpeed(getWorld().rand.nextFloat(), 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 102 | ParticleEngine.addEffect | public void drawLightningFlash(double x, double y, double z, float r, float g, float b, float alpha, float scale) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 106 | new FXGeneric | public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, mx, my, mz); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 109 | setAlphaF | public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) { | fb.setAlphaF(part.alpha); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 111 | setParticles | public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) { | fb.setParticles(part.partStart, part.partNum, part.partInc); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 112 | setScale | public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) { | fb.setScale(part.scale); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 113 | setLayer | public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) { | fb.setLayer(part.layer); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 114 | setRotationSpeed | public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) { | fb.setRotationSpeed(part.rotstart, part.rot); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 115 | setSlowDown | public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) { | fb.setSlowDown(part.slowDown); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 116 | setGravity | public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) { | fb.setGravity(part.grav); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 117 | setGridSize | public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) { | fb.setGridSize(part.grid); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 118 | ParticleEngine.addEffectWithDelay | public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, part.delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 118 | ParticleEngine.addEffect | public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, part.delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 122 | new FXGeneric | public void drawGenericParticles(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 125 | setAlphaF | public void drawGenericParticles(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | fb.setAlphaF(alpha); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 127 | setParticles | public void drawGenericParticles(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | fb.setParticles(start, num, inc); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 128 | setScale | public void drawGenericParticles(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | fb.setScale(scale); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 129 | setLayer | public void drawGenericParticles(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | fb.setLayer(layer); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 130 | setRotationSpeed | public void drawGenericParticles(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | fb.setRotationSpeed(rot); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 131 | ParticleEngine.addEffectWithDelay | public void drawGenericParticles(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 131 | ParticleEngine.addEffect | public void drawGenericParticles(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 135 | new FXGeneric | public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 136 | setGridSize | public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | fb.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 139 | setAlphaF | public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | fb.setAlphaF(alpha); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 141 | setParticles | public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | fb.setParticles(start, num, inc); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 142 | setScale | public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | fb.setScale(scale); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 143 | setLayer | public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | fb.setLayer(layer); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 144 | setRotationSpeed | public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | fb.setRotationSpeed(rot); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 145 | ParticleEngine.addEffectWithDelay | public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 145 | ParticleEngine.addEffect | public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 149 | new FXGeneric | public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 152 | setAlphaF | public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setAlphaF(0.3f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 153 | setGridSize | public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 154 | setParticles | public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setParticles(56, 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 155 | setScale | public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setScale(2.0f, 5.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 156 | setLayer | public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setLayer(0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 157 | setSlowDown | public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setSlowDown(1.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 158 | setRotationSpeed | public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 159 | ParticleEngine.addEffect | public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 163 | new FXGeneric | public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 166 | setAlphaF | public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) { | fb.setAlphaF(0.3f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 167 | setGridSize | public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) { | fb.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 168 | setParticles | public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) { | fb.setParticles(72 + getWorld().rand.nextInt(4), 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 169 | setScale | public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) { | fb.setScale(1.0f, 10.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 170 | setLayer | public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) { | fb.setLayer(0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 171 | setSlowDown | public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) { | fb.setSlowDown(1.01); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 172 | setRotationSpeed | public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) { | fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 173 | ParticleEngine.addEffect | public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 178 | new FXGeneric | public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 180 | setAlphaF | public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setAlphaF(0.3f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 181 | setGridSize | public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 182 | setParticles | public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setParticles(56, 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 183 | setScale | public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setScale(1.5f, 3.0f, 8.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 184 | setLayer | public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setLayer(0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 185 | setSlowDown | public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setSlowDown(1.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 186 | setWind | public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setWind(0.001); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 187 | setRotationSpeed | public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) { | fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 188 | ParticleEngine.addEffect | public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 197 | new FXGeneric | public void drawPollutionParticles(BlockPos p) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005, 0.02, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 200 | setAlphaF | public void drawPollutionParticles(BlockPos p) { | fb.setAlphaF(0.5f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 201 | setGridSize | public void drawPollutionParticles(BlockPos p) { | fb.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 202 | setParticles | public void drawPollutionParticles(BlockPos p) { | fb.setParticles(56, 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 203 | setScale | public void drawPollutionParticles(BlockPos p) { | fb.setScale(2.0f, 5.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 204 | setLayer | public void drawPollutionParticles(BlockPos p) { | fb.setLayer(1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 205 | setSlowDown | public void drawPollutionParticles(BlockPos p) { | fb.setSlowDown(1.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 206 | setWind | public void drawPollutionParticles(BlockPos p) { | fb.setWind(0.001); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 207 | setRotationSpeed | public void drawPollutionParticles(BlockPos p) { | fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 208 | ParticleEngine.addEffect | public void drawPollutionParticles(BlockPos p) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 255 | new FXGeneric | public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 259 | setAlphaF | public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setAlphaF(0.0f, 1.0f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 260 | setParticles | public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setParticles(sp ? 320 : 512, 16, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 262 | setGravity | public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setGravity(grav); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 263 | setScale | public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setScale(scale, scale * 2.0f, scale); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 264 | setLayer | public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setLayer(0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 265 | setSlowDown | public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setSlowDown(decay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 266 | setRandomMovementScale | public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setRandomMovementScale(5.0E-5f, 0.0f, 5.0E-5f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 267 | ParticleEngine.addEffect | public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 267 | ParticleEngine.addEffectWithDelay | public void drawLineSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 272 | new FXGeneric | public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, x2, y2, z2); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 280 | setAlphaF | public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setAlphaF(alphas); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 281 | setParticles | public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setParticles(sp ? 320 : 512, 16, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 283 | setGravity | public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setGravity(grav); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 284 | setScale | public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setScale(scale, scale * 2.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 285 | setLayer | public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setLayer(0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 286 | setSlowDown | public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setSlowDown(decay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 287 | setRandomMovementScale | public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setRandomMovementScale(5.0E-4f, 0.001f, 5.0E-4f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 288 | setWind | public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | fb.setWind(5.0E-4); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 289 | ParticleEngine.addEffectWithDelay | public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 289 | ParticleEngine.addEffect | public void drawSimpleSparkle(Random rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 294 | new FXGeneric | public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) { | FXGenericGui fb = new FXGenericGui(getWorld(), x, y, 0.0, x2, y2, 0.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 297 | setAlphaF | public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) { | fb.setAlphaF(0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 298 | setParticles | public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) { | fb.setParticles(sp ? 320 : 512, 16, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 300 | setGravity | public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) { | fb.setGravity(grav); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 301 | setScale | public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) { | fb.setScale(scale, scale * 2.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 303 | setLayer | public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) { | fb.setLayer(4); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 304 | setSlowDown | public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) { | fb.setSlowDown(decay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 305 | setRandomMovementScale | public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) { | fb.setRandomMovementScale(0.025f, 0.025f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 306 | ParticleEngine.addEffect | public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 306 | ParticleEngine.addEffectWithDelay | public void drawSimpleSparkleGui(Random rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) { | ParticleEngine.addEffectWithDelay(getWorld(), fb, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 316 | new FXGeneric | public void drawBlockMistParticles(BlockPos p, int c) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, getWorld().rand.nextGaussian() * 0.01, getWorld().rand.nextFloat() * 0.075, getWorld().rand.nextGaussian() * 0.01); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 319 | setAlphaF | public void drawBlockMistParticles(BlockPos p, int c) { | fb.setAlphaF(0.0f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 320 | setGridSize | public void drawBlockMistParticles(BlockPos p, int c) { | fb.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 321 | setParticles | public void drawBlockMistParticles(BlockPos p, int c) { | fb.setParticles(56, 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 322 | setScale | public void drawBlockMistParticles(BlockPos p, int c) { | fb.setScale(5.0f, 1.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 323 | setLayer | public void drawBlockMistParticles(BlockPos p, int c) { | fb.setLayer(0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 324 | setSlowDown | public void drawBlockMistParticles(BlockPos p, int c) { | fb.setSlowDown(1.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 325 | setGravity | public void drawBlockMistParticles(BlockPos p, int c) { | fb.setGravity(0.1f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 326 | setWind | public void drawBlockMistParticles(BlockPos p, int c) { | fb.setWind(0.001); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 327 | setRotationSpeed | public void drawBlockMistParticles(BlockPos p, int c) { | fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 328 | ParticleEngine.addEffect | public void drawBlockMistParticles(BlockPos p, int c) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 334 | new FXGeneric | public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, mx, my, mz); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 337 | setAlphaF | public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) { | fb.setAlphaF(0.0f, 0.66f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 338 | setGridSize | public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) { | fb.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 339 | setParticles | public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) { | fb.setParticles(56 + getWorld().rand.nextInt(4), 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 340 | setScale | public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) { | fb.setScale(5.0f + getWorld().rand.nextFloat(), 10.0f + getWorld().rand.nextFloat()); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 341 | setLayer | public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) { | fb.setLayer(0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 342 | setSlowDown | public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) { | fb.setSlowDown(0.99); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 343 | setWind | public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) { | fb.setWind(0.001); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 344 | setRotationSpeed | public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) { | fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -0.25f : 0.25f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 345 | ParticleEngine.addEffect | public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 348 | drawWispyMotes | public void drawWispyMotesOnBlock(BlockPos pp, int age, float grav) { | public void drawWispyMotesOnBlock(BlockPos pp, int age, float grav) { |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 349 | drawWispyMotes | public void drawWispyMotesOnBlock(BlockPos pp, int age, float grav) { | drawWispyMotes(pp.getX() + getWorld().rand.nextFloat(), pp.getY(), pp.getZ() + getWorld().rand.nextFloat(), 0.0, 0.0, 0.0, age, 0.4f + getWorld().rand.nextFloat() * 0.6f, 0.6f + getWorld().rand.nextFloat() * 0.4f, 0.6f + getWorld().rand.nextFloat() * 0.4f, grav); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 352 | drawWispyMotes | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) { | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) { |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 353 | drawWispyMotes | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) { | drawWispyMotes(d, e, f, vx, vy, vz, age, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, grav); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 356 | drawWispyMotes | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 357 | new FXGeneric | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { | FXGeneric fb = new FXGeneric(getWorld(), d, e, f, vx, vy, vz); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 360 | setAlphaF | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { | fb.setAlphaF(0.0f, 0.6f, 0.6f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 361 | setGridSize | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { | fb.setGridSize(64); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 362 | setParticles | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { | fb.setParticles(512, 16, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 363 | setScale | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { | fb.setScale(1.0f, 0.5f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 365 | setWind | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { | fb.setWind(0.001); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 366 | setGravity | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { | fb.setGravity(grav); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 367 | setRandomMovementScale | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { | fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 368 | ParticleEngine.addEffect | public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 378 | new FXGeneric | public void drawBlockMistParticlesFlat(BlockPos p, int c) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005, 0.005, (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.005); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 381 | setAlphaF | public void drawBlockMistParticlesFlat(BlockPos p, int c) { | fb.setAlphaF(1.0f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 382 | setGridSize | public void drawBlockMistParticlesFlat(BlockPos p, int c) { | fb.setGridSize(8); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 383 | setParticles | public void drawBlockMistParticlesFlat(BlockPos p, int c) { | fb.setParticles(24, 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 384 | setScale | public void drawBlockMistParticlesFlat(BlockPos p, int c) { | fb.setScale(2.0f, 5.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 385 | setLayer | public void drawBlockMistParticlesFlat(BlockPos p, int c) { | fb.setLayer(0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 386 | setSlowDown | public void drawBlockMistParticlesFlat(BlockPos p, int c) { | fb.setSlowDown(1.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 387 | setWind | public void drawBlockMistParticlesFlat(BlockPos p, int c) { | fb.setWind(0.001); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 388 | setRotationSpeed | public void drawBlockMistParticlesFlat(BlockPos p, int c) { | fb.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 389 | ParticleEngine.addEffect | public void drawBlockMistParticlesFlat(BlockPos p, int c) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 394 | new FXGeneric | public void crucibleBubble(float x, float y, float z, float cr, float cg, float cb) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 396 | setScale | public void crucibleBubble(float x, float y, float z, float cr, float cg, float cb) { | fb.setScale(getWorld().rand.nextFloat() * 0.3f + 0.3f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 398 | setRandomMovementScale | public void crucibleBubble(float x, float y, float z, float cr, float cg, float cb) { | fb.setRandomMovementScale(0.002f, 0.002f, 0.002f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 399 | setGravity | public void crucibleBubble(float x, float y, float z, float cr, float cg, float cb) { | fb.setGravity(-0.001f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 401 | setFinalFrames | public void crucibleBubble(float x, float y, float z, float cr, float cg, float cb) { | fb.setFinalFrames(65, 66, 66); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 402 | ParticleEngine.addEffect | public void crucibleBubble(float x, float y, float z, float cr, float cg, float cb) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 407 | new FXGeneric | public void crucibleBoil(BlockPos pos, TileCrucible tile, int j) { | FXGeneric fb = new FXGeneric(getWorld(), pos.getX() + 0.2f + getWorld().rand.nextFloat() * 0.6f, pos.getY() + 0.1f + tile.getFluidHeight(), pos.getZ() + 0.2f + getWorld().rand.nextFloat() * 0.6f, 0.0, 0.002, 0.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 409 | setScale | public void crucibleBoil(BlockPos pos, TileCrucible tile, int j) { | fb.setScale(getWorld().rand.nextFloat() * 0.3f + 0.2f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 417 | setRandomMovementScale | public void crucibleBoil(BlockPos pos, TileCrucible tile, int j) { | fb.setRandomMovementScale(0.001f, 0.001f, 0.001f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 418 | setGravity | public void crucibleBoil(BlockPos pos, TileCrucible tile, int j) { | fb.setGravity(-0.025f * j); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 420 | setFinalFrames | public void crucibleBoil(BlockPos pos, TileCrucible tile, int j) { | fb.setFinalFrames(65, 66); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 421 | ParticleEngine.addEffect | public void crucibleBoil(BlockPos pos, TileCrucible tile, int j) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 426 | new FXGeneric | public void crucibleFroth(float x, float y, float z) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 428 | setScale | public void crucibleFroth(float x, float y, float z) { | fb.setScale(getWorld().rand.nextFloat() * 0.2f + 0.2f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 430 | setRandomMovementScale | public void crucibleFroth(float x, float y, float z) { | fb.setRandomMovementScale(0.001f, 0.001f, 0.001f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 431 | setGravity | public void crucibleFroth(float x, float y, float z) { | fb.setGravity(0.1f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 433 | setFinalFrames | public void crucibleFroth(float x, float y, float z) { | fb.setFinalFrames(65, 66); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 434 | ParticleEngine.addEffect | public void crucibleFroth(float x, float y, float z) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 438 | new FXGeneric | public void crucibleFrothDown(float x, float y, float z) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 440 | setScale | public void crucibleFrothDown(float x, float y, float z) { | fb.setScale(getWorld().rand.nextFloat() * 0.2f + 0.4f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 442 | setAlphaF | public void crucibleFrothDown(float x, float y, float z) { | fb.setAlphaF(0.8f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 443 | setRandomMovementScale | public void crucibleFrothDown(float x, float y, float z) { | fb.setRandomMovementScale(0.001f, 0.001f, 0.001f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 444 | setGravity | public void crucibleFrothDown(float x, float y, float z) { | fb.setGravity(0.05f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 447 | setFinalFrames | public void crucibleFrothDown(float x, float y, float z) { | fb.setFinalFrames(65, 66); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 448 | setLayer | public void crucibleFrothDown(float x, float y, float z) { | fb.setLayer(1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 449 | ParticleEngine.addEffect | public void crucibleFrothDown(float x, float y, float z) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 494 | new FXGeneric | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | FXGeneric fb2 = new FXGeneric(getWorld(), x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx / 2.0, vy / 2.0, vz / 2.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 497 | setAlphaF | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb2.setAlphaF(1.0f, 0.1f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 498 | setGridSize | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb2.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 499 | setParticles | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb2.setParticles(123, 5, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 500 | setScale | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb2.setScale(3.0f, 4.0f + getWorld().rand.nextFloat() * 3.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 501 | setLayer | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb2.setLayer(1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 502 | setSlowDown | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb2.setSlowDown(0.7); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 503 | setRotationSpeed | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb2.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? -1.0f : 1.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 504 | ParticleEngine.addEffect | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | ParticleEngine.addEffect(getWorld(), fb2); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 511 | drawWispyMotes | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | drawWispyMotes(x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx, vy, vz, 15 + getWorld().rand.nextInt(10), -0.01f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 513 | new FXGeneric | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | FXGeneric fb3 = new FXGeneric(getWorld(), x, y, z, 0.0, 0.0, 0.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 516 | setAlphaF | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb3.setAlphaF(1.0f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 517 | setGridSize | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb3.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 518 | setParticles | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb3.setParticles(77, 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 519 | setScale | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb3.setScale(10.0f + getWorld().rand.nextFloat() * 2.0f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 520 | setLayer | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb3.setLayer(0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 521 | setRotationSpeed | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | fb3.setRotationSpeed(getWorld().rand.nextFloat(), (float) getWorld().rand.nextGaussian()); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 522 | ParticleEngine.addEffect | public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, EnumFacing side) { | ParticleEngine.addEffect(getWorld(), fb3); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 541 | new FXGeneric | public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, EnumFacing side, int seed, int layer, int delay) { | FXGeneric fb2 = new FXGeneric(getWorld(), x + vx * 5.0, y + vy * 5.0, z + vz * 5.0, vx, vy, vz); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 547 | setAlphaF | public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, EnumFacing side, int seed, int layer, int delay) { | fb2.setAlphaF(a, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 548 | setGridSize | public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, EnumFacing side, int seed, int layer, int delay) { | fb2.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 549 | setParticles | public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, EnumFacing side, int seed, int layer, int delay) { | fb2.setParticles(60 + getWorld().rand.nextInt(4), 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 550 | setScale | public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, EnumFacing side, int seed, int layer, int delay) { | fb2.setScale(5.0f * scale, (10.0f + getWorld().rand.nextFloat() * 4.0f) * scale); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 551 | setLayer | public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, EnumFacing side, int seed, int layer, int delay) { | fb2.setLayer(layer); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 552 | setRotationSpeed | public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, EnumFacing side, int seed, int layer, int delay) { | fb2.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? (-2.0f - getWorld().rand.nextFloat() * 2.0f) : (2.0f + getWorld().rand.nextFloat() * 2.0f)); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 553 | ParticleEngine.addEffectWithDelay | public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, EnumFacing side, int seed, int layer, int delay) { | ParticleEngine.addEffectWithDelay(getWorld(), fb2, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 553 | ParticleEngine.addEffect | public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, EnumFacing side, int seed, int layer, int delay) { | ParticleEngine.addEffectWithDelay(getWorld(), fb2, delay); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 557 | new FXGeneric | public void pechsCurseTick(double posX, double posY, double posZ) { | FXGeneric fb2 = new FXGeneric(getWorld(), posX, posY, posZ, 0.0, 0.0, 0.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 561 | setAlphaF | public void pechsCurseTick(double posX, double posY, double posZ) { | fb2.setAlphaF(0.75f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 562 | setGridSize | public void pechsCurseTick(double posX, double posY, double posZ) { | fb2.setGridSize(8); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 563 | setParticles | public void pechsCurseTick(double posX, double posY, double posZ) { | fb2.setParticles(28 + getWorld().rand.nextInt(4), 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 564 | setScale | public void pechsCurseTick(double posX, double posY, double posZ) { | fb2.setScale(3.0f, 5.0f + getWorld().rand.nextFloat() * 2.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 565 | setLayer | public void pechsCurseTick(double posX, double posY, double posZ) { | fb2.setLayer(0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 566 | setRotationSpeed | public void pechsCurseTick(double posX, double posY, double posZ) { | fb2.setRotationSpeed(getWorld().rand.nextFloat(), getWorld().rand.nextBoolean() ? (-3.0f - getWorld().rand.nextFloat() * 3.0f) : (3.0f + getWorld().rand.nextFloat() * 3.0f)); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 567 | ParticleEngine.addEffect | public void pechsCurseTick(double posX, double posY, double posZ) { | ParticleEngine.addEffect(getWorld(), fb2); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 568 | drawWispyMotes | public void pechsCurseTick(double posX, double posY, double posZ) { | drawWispyMotes(posX, posY, posZ, 0.0, 0.0, 0.0, 10 + getWorld().rand.nextInt(10), -0.01f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 619 | ParticleEngine.addEffect | public void visSparkle(int x, int y, int z, int x2, int y2, int z2, int color) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 624 | ParticleEngine.addEffect | public void voidStreak(double x, double y, double z, double x2, double y2, double z2, int seed, float scale) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 635 | setAlphaF | public void splooshFX(Entity e) { | fx.setAlphaF(0.4f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 639 | setAlphaF | public void splooshFX(Entity e) { | fx.setAlphaF(0.6f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 649 | setAlphaF | public void taintsplosionFX(Entity e) { | fx.setAlphaF(0.4f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 653 | setAlphaF | public void taintsplosionFX(Entity e) { | fx.setAlphaF(0.6f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 669 | setAlphaF | public void tentacleAriseFX(Entity e) { | fx.setAlphaF(0.5f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 689 | setAlphaF | public void slimeJumpFX(Entity e, int i) { | fx.setAlphaF(0.4f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 702 | setAlphaF | public void taintLandFX(Entity e) { | fx.setAlphaF(0.4f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 710 | setAlphaF | public void drawInfusionParticles1(double x, double y, double z, BlockPos pos, ItemStack stack) { | fb.setAlphaF(0.3f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 716 | setAlphaF | public void drawInfusionParticles2(double x, double y, double z, BlockPos pos, IBlockState id, int md) { | fb.setAlphaF(0.3f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 723 | ParticleEngine.addEffect | public void drawInfusionParticles3(double x, double y, double z, int x2, int y2, int z2) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 729 | ParticleEngine.addEffect | public void drawInfusionParticles4(double x, double y, double z, int x2, int y2, int z2) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 734 | setAlphaF | public void drawVentParticles(double x, double y, double z, double x2, double y2, double z2, int color) { | fb.setAlphaF(0.4f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 735 | ParticleEngine.addEffect | public void drawVentParticles(double x, double y, double z, double x2, double y2, double z2, int color) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 740 | setAlphaF | public void drawVentParticles(double x, double y, double z, double x2, double y2, double z2, int color, float scale) { | fb.setAlphaF(0.4f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 741 | setScale | public void drawVentParticles(double x, double y, double z, double x2, double y2, double z2, int color, float scale) { | fb.setScale(scale); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 742 | ParticleEngine.addEffect | public void drawVentParticles(double x, double y, double z, double x2, double y2, double z2, int color, float scale) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 747 | setAlphaF | public void drawVentParticles2(double x, double y, double z, double x2, double y2, double z2, int color, float scale) { | fb.setAlphaF(0.4f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 748 | setScale | public void drawVentParticles2(double x, double y, double z, double x2, double y2, double z2, int color, float scale) { | fb.setScale(scale); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 749 | ParticleEngine.addEffect | public void drawVentParticles2(double x, double y, double z, double x2, double y2, double z2, int color, float scale) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 756 | new FXGeneric | public void spark(double d, double e, double f, float size, float r, float g, float b, float a) { | FXGeneric fb = new FXGeneric(getWorld(), d, e, f, 0.0, 0.0, 0.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 758 | setAlphaF | public void spark(double d, double e, double f, float size, float r, float g, float b, float a) { | fb.setAlphaF(a); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 760 | setGridSize | public void spark(double d, double e, double f, float size, float r, float g, float b, float a) { | fb.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 761 | setParticles | public void spark(double d, double e, double f, float size, float r, float g, float b, float a) { | fb.setParticles(8 + getWorld().rand.nextInt(3) * 16, 8, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 762 | setScale | public void spark(double d, double e, double f, float size, float r, float g, float b, float a) { | fb.setScale(size); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 764 | ParticleEngine.addEffect | public void spark(double d, double e, double f, float size, float r, float g, float b, float a) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 771 | ParticleEngine.addEffect | public void smokeSpiral(double x, double y, double z, float rad, int start, int miny, int color) { | ParticleEngine.addEffect(getWorld(), fx); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 777 | ParticleEngine.addEffect | public void wispFXEG(double posX, double posY, double posZ, Entity target) { | ParticleEngine.addEffect(getWorld(), ef); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 782 | new FXGeneric | public void burst(double sx, double sy, double sz, float size) { | FXGeneric fb = new FXGeneric(getWorld(), sx, sy, sz, 0.0, 0.0, 0.0); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 784 | setGridSize | public void burst(double sx, double sy, double sz, float size) { | fb.setGridSize(16); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 785 | setParticles | public void burst(double sx, double sy, double sz, float size) { | fb.setParticles(208, 31, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 786 | setScale | public void burst(double sx, double sy, double sz, float size) { | fb.setScale(size); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 787 | ParticleEngine.addEffect | public void burst(double sx, double sy, double sz, float size) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 842 | ParticleEngine.addEffect | public void boreDigFx(int x, int y, int z, Entity e, IBlockState bi, int md, int delay) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 854 | ParticleEngine.addEffect | public void essentiaTrailFx(BlockPos p1, BlockPos p2, int count, int color, float scale, int ext) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 859 | ParticleEngine.addEffect | public void boreTrailFx(BlockPos p1, Entity e, int count, int color, float scale, int ext) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 863 | new FXGeneric | public void essentiaDropFx(double x, double y, double z, float r, float g, float b, float alpha) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, getWorld().rand.nextGaussian() * 0.004999999888241291, getWorld().rand.nextGaussian() * 0.004999999888241291, getWorld().rand.nextGaussian() * 0.004999999888241291); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 866 | setAlphaF | public void essentiaDropFx(double x, double y, double z, float r, float g, float b, float alpha) { | fb.setAlphaF(alpha); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 868 | setParticles | public void essentiaDropFx(double x, double y, double z, float r, float g, float b, float alpha) { | fb.setParticles(25, 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 869 | setScale | public void essentiaDropFx(double x, double y, double z, float r, float g, float b, float alpha) { | fb.setScale(0.4f + getWorld().rand.nextFloat() * 0.2f, 0.2f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 870 | setLayer | public void essentiaDropFx(double x, double y, double z, float r, float g, float b, float alpha) { | fb.setLayer(1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 871 | setGravity | public void essentiaDropFx(double x, double y, double z, float r, float g, float b, float alpha) { | fb.setGravity(0.01f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 872 | setRotationSpeed | public void essentiaDropFx(double x, double y, double z, float r, float g, float b, float alpha) { | fb.setRotationSpeed(0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 873 | ParticleEngine.addEffect | public void essentiaDropFx(double x, double y, double z, float r, float g, float b, float alpha) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 877 | new FXGeneric | public void jarSplashFx(double x, double y, double z) { | FXGeneric fb = new FXGeneric(getWorld(), x + getWorld().rand.nextGaussian() * 0.07500000298023224, y, z + getWorld().rand.nextGaussian() * 0.07500000298023224, getWorld().rand.nextGaussian() * 0.014999999664723873, 0.075f + getWorld().rand.nextFloat() * 0.05f, getWorld().rand.nextGaussian() * 0.014999999664723873); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 881 | setAlphaF | public void jarSplashFx(double x, double y, double z) { | fb.setAlphaF(0.5f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 883 | setParticles | public void jarSplashFx(double x, double y, double z) { | fb.setParticles(73, 1, 1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 884 | setScale | public void jarSplashFx(double x, double y, double z) { | fb.setScale(0.4f + getWorld().rand.nextFloat() * 0.3f, 0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 885 | setLayer | public void jarSplashFx(double x, double y, double z) { | fb.setLayer(1); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 886 | setGravity | public void jarSplashFx(double x, double y, double z) { | fb.setGravity(0.3f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 887 | setRotationSpeed | public void jarSplashFx(double x, double y, double z) { | fb.setRotationSpeed(0.0f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 888 | ParticleEngine.addEffect | public void jarSplashFx(double x, double y, double z) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 893 | ParticleEngine.addEffect | public void waterTrailFx(BlockPos p1, BlockPos p2, int count, int color, float scale) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 905 | setGravity | public void blockRunes(double x, double y, double z, float r, float g, float b, int dur, float grav) { | fb.setGravity(grav); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 906 | ParticleEngine.addEffect | public void blockRunes(double x, double y, double z, float r, float g, float b, int dur, float grav) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 911 | setGravity | public void blockRunes2(double x, double y, double z, float r, float g, float b, int dur, float grav) { | fb.setGravity(grav); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 912 | setScale | public void blockRunes2(double x, double y, double z, float r, float g, float b, int dur, float grav) { | fb.setScale((float)(0.5 + getWorld().rand.nextGaussian() * 0.10000000149011612)); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 914 | ParticleEngine.addEffect | public void blockRunes2(double x, double y, double z, float r, float g, float b, int dur, float grav) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 919 | ParticleEngine.addEffect | public void drawSlash(double x, double y, double z, double x2, double y2, double z2, int dur) { | ParticleEngine.addEffect(getWorld(), fb); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 929 | ParticleEngine.addEffect | public FXSwarm swarmParticleFX(Entity targetedEntity, float f1, float f2, float pg) { | ParticleEngine.addEffect(getWorld(), fx); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 954 | new FXGeneric | public void cultistSpawn(double x, double y, double z, double a, double b, double c) { | FXGeneric fb = new FXGeneric(getWorld(), x, y, z, a, b, c); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 957 | setAlphaF | public void cultistSpawn(double x, double y, double z, double a, double b, double c) { | fb.setAlphaF(0.8f); |
| src/main/java/thaumcraft/client/fx/FXDispatcher.java | 958 | setGridSize | public void cultistSpawn(double x, double y, double z, double a, double b, double c) { | fb.setGridSize(16); |