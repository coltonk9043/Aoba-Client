package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.misc.Render3D;
import net.aoba.misc.bowaimbot.BowAimbotTargets;
import net.aoba.misc.bowaimbot.BowAimbotUtils;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import java.util.Comparator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BowAimbot extends Module implements TickListener, Render3DListener {

    private Entity temp = null;

    private BooleanSetting targetAnimals;
    private BooleanSetting targetPlayers;
    private FloatSetting frequency;
    private FloatSetting predictmovement;

    private int currentTick = 0;
    private boolean skip;
    private float velocity;
    private double posX;
    private double posY;
    private double posZ;
    private float neededPitch;
    private double d;
    private float neededYaw;



    public BowAimbot() {
        super(new KeybindSetting("key.bowaimbot", "BowAimbot Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("BowAimbot");
        this.setCategory(Category.Combat);
        this.setDescription("BowAimbot");

        targetAnimals = new BooleanSetting("bowaimbot_target_mobs", "Target Mobs", "Target mobs.", false);
        targetPlayers = new BooleanSetting("bowaimbot_target_players", "Target Players", "Target players.", true);
        frequency = new FloatSetting("bowaimbot_frequency", "Ticks", "How frequent the aimbot updates (Lower = Laggier)", 1.0f, 1.0f, 20.0f, 1.0f);
        predictmovement = new FloatSetting("bowaimbot_prediction", "Prediction", "sets the strength of BowAimbot's movement prediction", 2f, 0f, 10f, 1f);

        this.addSetting(targetAnimals);
        this.addSetting(targetPlayers);
        this.addSetting(frequency);
        this.addSetting(predictmovement);
    }

    @Override
    public void onDisable() {
        if(Aoba.getInstance().moduleManager.trajectory.getState())
            Aoba.getInstance().moduleManager.trajectory.toggle();
        Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
        Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
    }

    @Override
    public void onEnable() {
        if(!Aoba.getInstance().moduleManager.trajectory.getState())
            Aoba.getInstance().moduleManager.trajectory.toggle();
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
        Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
    }

    @Override
    public void onToggle() {

    }

    @Override
    public void OnUpdate(TickEvent event) {
        skip = false;
        currentTick++;

        ItemStack stack = MC.player.getInventory().getMainHandStack();
        Item item = stack.getItem();
        if(!(item instanceof BowItem || item instanceof CrossbowItem))
        {
            temp = null;
            return;
        }

        if(item instanceof BowItem && !MC.options.useKey.isPressed()
                && !MC.player.isUsingItem())
        {
            temp = null;
            return;
        }

        if(item instanceof CrossbowItem && !CrossbowItem.isCharged(stack))
        {
            temp = null;
            return;
        }

        velocity = (72000 - MC.player.getItemUseTimeLeft()) / 20F;
        velocity = (velocity * velocity + velocity * 2) / 3;
        if(velocity > 1)
            velocity = 1;

        if (currentTick >= frequency.getValue()) {

            if(targetAnimals.getValue() && targetPlayers.getValue()) {
                if(filterEntities(Stream.of(temp)) == null) temp = filterEntities(StreamSupport.stream(MC.world.getEntities().spliterator(), true));
            }

            if(!targetAnimals.getValue() && targetPlayers.getValue()) {
                if(filterPlayers(Stream.of((AbstractClientPlayerEntity) temp)) == null) temp = filterPlayers(StreamSupport.stream(MC.world.getPlayers().spliterator(), true));
            }

            if(targetAnimals.getValue() && !targetPlayers.getValue()) {
                if(filterEntities(Stream.of(temp)) == null) temp = filterEntities(StreamSupport.stream(MC.world.getEntities().spliterator(), true));
                if(temp instanceof PlayerEntity) temp = null;
            }

            if(temp == null)
                return;

            double hDistance = Math.sqrt(posX * posX + posZ * posZ);
            double hDistanceSq = hDistance * hDistance;
            float g = 0.006F;
            float velocitySq = velocity * velocity;
            float velocityPow4 = velocitySq * velocitySq;

            d = temp.squaredDistanceTo(MC.player.getEyePos()) * (predictmovement.getValue() / 100);
            posY = temp.getY() + (temp.getY() - temp.lastRenderY) * d + temp.getHeight() * 0.5 - MC.player.getY() - MC.player.getEyeHeight(MC.player.getPose());
            neededPitch = (float)-Math.toDegrees(Math.atan((velocitySq - Math.sqrt(velocityPow4 - g * (g * hDistanceSq + 2 * posY * velocitySq))) / (g * hDistance)));
            posZ = temp.getZ() + (temp.getZ() - temp.lastRenderZ) * d - MC.player.getZ();
            posX = temp.getX() + (temp.getX() - temp.lastRenderX) * d - MC.player.getX();
            neededYaw = (float)Math.toDegrees(Math.atan2(posZ, posX)) - 90;

            currentTick = 0;
        }
    }

    private Entity filterEntities(Stream<Entity> s)
    {
        Stream<Entity> stream = s.filter(BowAimbotTargets.IS_ATTACKABLE);

        return stream.min(Priority.ANGLE_DIST.comparator).orElse(null);
    }

    private Entity filterPlayers(Stream<AbstractClientPlayerEntity> s)
    {
        Stream<AbstractClientPlayerEntity> stream = s.filter(BowAimbotTargets.IS_ATTACKABLE);

        return stream.min(Priority.ANGLE_DIST.comparator).orElse(null);
    }

    static MinecraftClient MC = AobaClient.MC;

    private enum Priority
    {
        ANGLE_DIST("", e -> Math.pow(BowAimbotUtils.getAngleToLookVec(e.getBoundingBox().getCenter()), 2) + MC.player.squaredDistanceTo(e));

        private final String name;
        private final Comparator<Entity> comparator;

        private Priority(String name, ToDoubleFunction<Entity> keyExtractor)
        {
            this.name = name;
            comparator = Comparator.comparingDouble(keyExtractor);
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    @Override
    public void OnRender(Render3DEvent event) {
        if(skip) return;
        if (temp != null) {
            Vec3d offset = Render3D.getEntityPositionOffsetInterpolated(temp, event.GetPartialTicks());
            MC.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, temp.getEyePos().add(offset).add(posX, posY, posZ));
            MC.player.setYaw(neededYaw);
            MC.player.setPitch(neededPitch);
        }
    }
}
