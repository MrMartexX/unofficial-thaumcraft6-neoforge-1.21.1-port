# Remaining Alchemy Legacy Recipe Blocks

Generated: 2026-06-18 16:02:26 +03:00

## Purpose

This document extracts exact legacy recipe source blocks for the remaining non-HEDGE alchemy recipe-page gaps after the first HEDGE_ALCHEMY crucible boundary batch.

## Summary

| Metric | Count |
|---|---:|
| Unique remaining alchemy references | 3 |
| Extracted legacy blocks | 15 |
| References without extracted block | 1 |

## Remaining family distribution

| Family | Count |
|---|---:|
| ALCHEMY_OTHER | 3 |

## Extracted API kind distribution

| API kind | Count |
|---|---:|
| CRUCIBLE | 15 |

## Extracted recipe overview

| Family | Reference | API kind | Resource id | Research | Aspects | File | Line |
|---|---|---|---|---|---|---|---:|
| ALCHEMY_OTHER | thaumcraft:EverfullUrn | CRUCIBLE | thaumcraft:EverfullUrn | EVERFULLURN | water=30, craft=10, earth=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 180 |
| ALCHEMY_OTHER | thaumcraft:JarLabelEssence | UNRESOLVED |  |  |  |  | 0 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 1 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 1 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 30 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 32 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 33 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 312 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 1 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 1 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 1 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 1 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 59 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 65 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 66 |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | CRUCIBLE |  |  |  | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 49 |

## Extracted legacy source blocks

### thaumcraft:EverfullUrn

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/common/config/ConfigRecipes.java
- Start line: 180
- Research: EVERFULLURN
- Aspects: water=30, craft=10, earth=10

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:EverfullUrn"), new CrucibleRecipe("EVERFULLURN", new ItemStack(BlocksTC.everfullUrn), new ItemStack(Items.FLOWER_POT), new AspectList().add(Aspect.WATER, 30).add(Aspect.CRAFT, 10).add(Aspect.EARTH, 10)));
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/client/gui/GuiThaumatorium.java
- Start line: 1
- Research: 
- Aspects: 

```java
package thaumcraft.client.gui;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.container.ContainerThaumatorium;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketSelectThaumotoriumRecipeToServer;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


@SideOnly(Side.CLIENT)
public class GuiThaumatorium extends GuiContainer
{
    private TileThaumatorium inventory;
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/client/gui/GuiThaumatorium.java
- Start line: 1
- Research: 
- Aspects: 

```java
package thaumcraft.client.gui;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.container.ContainerThaumatorium;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketSelectThaumotoriumRecipeToServer;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


@SideOnly(Side.CLIENT)
public class GuiThaumatorium extends GuiContainer
{
    private TileThaumatorium inventory;
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/client/gui/GuiThaumatorium.java
- Start line: 30
- Research: 
- Aspects: 

```java
public class GuiThaumatorium extends GuiContainer
{
    private TileThaumatorium inventory;
    private ContainerThaumatorium container;
    private int index;
    private int lastSize;
    private EntityPlayer player;
    ResourceLocation tex;
    ArrayList<Integer> hashList;
    long lastHLUpdate;
    static HashMap<Integer, CrucibleRecipe> recipeCache;

    public GuiThaumatorium(InventoryPlayer par1InventoryPlayer, TileThaumatorium par2TileEntityFurnace) {
        super(new ContainerThaumatorium(par1InventoryPlayer, par2TileEntityFurnace));
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/client/gui/GuiThaumatorium.java
- Start line: 32
- Research: 
- Aspects: 

```java
    private TileThaumatorium inventory;
    private ContainerThaumatorium container;
    private int index;
    private int lastSize;
    private EntityPlayer player;
    ResourceLocation tex;
    ArrayList<Integer> hashList;
    long lastHLUpdate;
    static HashMap<Integer, CrucibleRecipe> recipeCache;

    public GuiThaumatorium(InventoryPlayer par1InventoryPlayer, TileThaumatorium par2TileEntityFurnace) {
        super(new ContainerThaumatorium(par1InventoryPlayer, par2TileEntityFurnace));
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/client/gui/GuiThaumatorium.java
- Start line: 33
- Research: 
- Aspects: 

```java
    private ContainerThaumatorium container;
    private int index;
    private int lastSize;
    private EntityPlayer player;
    ResourceLocation tex;
    ArrayList<Integer> hashList;
    long lastHLUpdate;
    static HashMap<Integer, CrucibleRecipe> recipeCache;

    public GuiThaumatorium(InventoryPlayer par1InventoryPlayer, TileThaumatorium par2TileEntityFurnace) {
        super(new ContainerThaumatorium(par1InventoryPlayer, par2TileEntityFurnace));
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/client/gui/GuiThaumatorium.java
- Start line: 312
- Research: 
- Aspects: 

```java
        GuiThaumatorium.recipeCache = new HashMap<Integer, CrucibleRecipe>();
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java
- Start line: 1
- Research: 
- Aspects: 

```java
package thaumcraft.client.renderers.tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


@SideOnly(Side.CLIENT)
public class TileThaumatoriumRenderer extends TileEntitySpecialRenderer
{
    EntityItem entityitem;
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java
- Start line: 1
- Research: 
- Aspects: 

```java
package thaumcraft.client.renderers.tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


@SideOnly(Side.CLIENT)
public class TileThaumatoriumRenderer extends TileEntitySpecialRenderer
{
    EntityItem entityitem;
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java
- Start line: 1
- Research: 
- Aspects: 

```java
package thaumcraft.client.renderers.tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


@SideOnly(Side.CLIENT)
public class TileThaumatoriumRenderer extends TileEntitySpecialRenderer
{
    EntityItem entityitem;
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java
- Start line: 1
- Research: 
- Aspects: 

```java
package thaumcraft.common.lib.network.misc;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


public class PacketSelectThaumotoriumRecipeToServer implements IMessage, IMessageHandler<PacketSelectThaumotoriumRecipeToServer, IMessage>
{
    private long pos;
    private int hash;

    public PacketSelectThaumotoriumRecipeToServer() {
    }

    public PacketSelectThaumotoriumRecipeToServer(EntityPlayer player, BlockPos pos, int recipeHash) {
        this.pos = pos.toLong();
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java
- Start line: 59
- Research: 
- Aspects: 

```java
                                thaumatorium.currentCraft = -1;
                                flag = true;
                                break;
                            }
                            ++i;
                        }
                        if (!flag && thaumatorium.recipeHash.size() < thaumatorium.maxRecipes) {
                            for (CrucibleRecipe cr : thaumatorium.recipes) {
                                if (cr.hash == message.hash) {
                                    thaumatorium.recipeEssentia.add(cr.getAspects().copy());
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java
- Start line: 65
- Research: 
- Aspects: 

```java
                        if (!flag && thaumatorium.recipeHash.size() < thaumatorium.maxRecipes) {
                            for (CrucibleRecipe cr : thaumatorium.recipes) {
                                if (cr.hash == message.hash) {
                                    thaumatorium.recipeEssentia.add(cr.getAspects().copy());
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java
- Start line: 66
- Research: 
- Aspects: 

```java
                            for (CrucibleRecipe cr : thaumatorium.recipes) {
                                if (cr.hash == message.hash) {
                                    thaumatorium.recipeEssentia.add(cr.getAspects().copy());
```

### thaumcraft:Thaumatorium

- Family: ALCHEMY_OTHER
- API kind: CRUCIBLE
- File: src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java
- Start line: 49
- Research: 
- Aspects: 

```java
public class TileThaumatorium extends TileThaumcraftInventory implements IAspectContainer, IEssentiaTransport, ITickable
{
    public AspectList essentia;
    public ArrayList<Integer> recipeHash;
    public ArrayList<AspectList> recipeEssentia;
    public ArrayList<String> recipePlayer;
    public int currentCraft;
    public int maxRecipes;
    public Aspect currentSuction;
    int venting;
    int counter;
    boolean heated;
    CrucibleRecipe currentRecipe;
    public Container eventHandler;
    public ArrayList<CrucibleRecipe> recipes;

    public TileThaumatorium() {
        super(1);
```

## Unresolved remaining alchemy references

| Family | Reference |
|---|---|
| ALCHEMY_OTHER | thaumcraft:JarLabelEssence |

## Next implementation guidance

1. Prefer the largest resolved family whose API kind is CRUCIBLE and whose output/catalyst ids already exist in the modern registry.
2. Keep infusion, entity/block behavior, and machine behavior out of alchemy recipe-page batches unless the audit shows the family is not a crucible recipe.
3. Re-run research recipe page gap audit after each family-level batch and do not patch individual ids unless a family has mixed API kind or missing registry identities.
