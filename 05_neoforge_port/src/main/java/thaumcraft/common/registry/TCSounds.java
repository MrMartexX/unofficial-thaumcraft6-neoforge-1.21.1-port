package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, Thaumcraft.MODID);

    public static final Supplier<SoundEvent> SCAN = SOUND_EVENTS.register("scan", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "scan")));
    public static final Supplier<SoundEvent> PAGE = SOUND_EVENTS.register("page", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "page")));
    public static final Supplier<SoundEvent> JAR = SOUND_EVENTS.register("jar", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "jar")));
    public static final Supplier<SoundEvent> ZAP = SOUND_EVENTS.register("zap", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "zap")));
    public static final Supplier<SoundEvent> KEY = SOUND_EVENTS.register("key", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "key")));
    public static final Supplier<SoundEvent> TOOL = SOUND_EVENTS.register("tool", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "tool")));
    public static final Supplier<SoundEvent> SQUEEK = SOUND_EVENTS.register("squeek", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "squeek")));
    public static final Supplier<SoundEvent> CREAK = SOUND_EVENTS.register("creak", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "creak")));
    public static final Supplier<SoundEvent> PAGETURN = SOUND_EVENTS.register("pageturn", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "pageturn")));
    public static final Supplier<SoundEvent> LEARN = SOUND_EVENTS.register("learn", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "learn")));
    public static final Supplier<SoundEvent> WHISPERS = SOUND_EVENTS.register("whispers", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "whispers")));
    public static final Supplier<SoundEvent> WRITE = SOUND_EVENTS.register("write", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "write")));
    public static final Supplier<SoundEvent> BUBBLE = SOUND_EVENTS.register("bubble", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "bubble")));
    public static final Supplier<SoundEvent> SPILL = SOUND_EVENTS.register("spill", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "spill")));
    public static final Supplier<SoundEvent> POOF = SOUND_EVENTS.register("poof", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "poof")));
    public static final Supplier<SoundEvent> WAND = SOUND_EVENTS.register("wand", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "wand")));
    public static final Supplier<SoundEvent> WANDFAIL = SOUND_EVENTS.register("wandfail", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "wandfail")));
    public static final Supplier<SoundEvent> TICKS = SOUND_EVENTS.register("ticks", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "ticks")));
    public static final Supplier<SoundEvent> CRAFTSTART = SOUND_EVENTS.register("craftstart", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "craftstart")));
    public static final Supplier<SoundEvent> CRAFTFAIL = SOUND_EVENTS.register("craftfail", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "craftfail")));
    public static final Supplier<SoundEvent> MONOLITH = SOUND_EVENTS.register("monolith", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "monolith")));
    public static final Supplier<SoundEvent> INFUSER = SOUND_EVENTS.register("infuser", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "infuser")));
    public static final Supplier<SoundEvent> INFUSERSTART = SOUND_EVENTS.register("infuserstart", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "infuserstart")));
    public static final Supplier<SoundEvent> RUMBLE = SOUND_EVENTS.register("rumble", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "rumble")));
    public static final Supplier<SoundEvent> EVILPORTAL = SOUND_EVENTS.register("evilportal", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "evilportal")));
    public static final Supplier<SoundEvent> GORE = SOUND_EVENTS.register("gore", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "gore")));
    public static final Supplier<SoundEvent> SHOCK = SOUND_EVENTS.register("shock", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "shock")));
    public static final Supplier<SoundEvent> EGIDLE = SOUND_EVENTS.register("egidle", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "egidle")));
    public static final Supplier<SoundEvent> EGATTACK = SOUND_EVENTS.register("egattack", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "egattack")));
    public static final Supplier<SoundEvent> EGDEATH = SOUND_EVENTS.register("egdeath", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "egdeath")));
    public static final Supplier<SoundEvent> EGSCREECH = SOUND_EVENTS.register("egscreech", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "egscreech")));
    public static final Supplier<SoundEvent> CRAB_CLAW = SOUND_EVENTS.register("crabclaw", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "crabclaw")));
    public static final Supplier<SoundEvent> CRAB_DEATH = SOUND_EVENTS.register("crabdeath", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "crabdeath")));
    public static final Supplier<SoundEvent> CRAB_TALK = SOUND_EVENTS.register("crabtalk", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "crabtalk")));
    public static final Supplier<SoundEvent> WISPLIVE = SOUND_EVENTS.register("wisplive", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "wisplive")));
    public static final Supplier<SoundEvent> WISPDEAD = SOUND_EVENTS.register("wispdead", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "wispdead")));
    public static final Supplier<SoundEvent> CHANT = SOUND_EVENTS.register("chant", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "chant")));
    public static final Supplier<SoundEvent> PECH_IDLE = SOUND_EVENTS.register("pech_idle", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "pech_idle")));
    public static final Supplier<SoundEvent> PECH_TRADE = SOUND_EVENTS.register("pech_trade", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "pech_trade")));
    public static final Supplier<SoundEvent> PECH_DICE = SOUND_EVENTS.register("pech_dice", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "pech_dice")));
    public static final Supplier<SoundEvent> PECH_HIT = SOUND_EVENTS.register("pech_hit", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "pech_hit")));
    public static final Supplier<SoundEvent> PECH_DEATH = SOUND_EVENTS.register("pech_death", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "pech_death")));
    public static final Supplier<SoundEvent> PECH_CHARGE = SOUND_EVENTS.register("pech_charge", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "pech_charge")));

    private TCSounds() {
    }
}
