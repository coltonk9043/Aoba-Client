package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.PreTickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.PreTickListener;
import net.aoba.gui.colors.Color;
import net.aoba.utils.FindItemResult;
import net.aoba.utils.render.Render3D;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.*;
import net.aoba.utils.entity.DamageUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.glfw.GLFW;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CrystalAura extends Module implements PreTickListener, Render3DListener {

    private final FloatSetting radius;
    private final FloatSetting placeRadius;
    private final BooleanSetting targetFriends;
    private final FloatSetting attackDelay;
    private final BooleanSetting autoSwitch;
    private final FloatSetting placeDelay;
    private final EnumSetting<TargetMode> targetMode;

    public enum TargetMode {
        NEAREST, MOST_HEALTH
    }

    public enum HandSetting {
        MAIN_HAND, OFF_HAND
    }

    private final EnumSetting<HandSetting> handSetting;

    // MultiPlace
    private final BooleanSetting multiPlace;

    // AntiSuicide
    private final BooleanSetting antiSuicide;

    // MinDamage
    private final FloatSetting minDamage;

    // MaxSelfDamage
    private final FloatSetting maxSelfDamage;

    // Enemy Range
    private final FloatSetting enemyRange;

    // Wall Range
    private final FloatSetting wallRange;

    // EndCrystal Priority
    public enum CrystalPriority {
        CLOSEST, HIGHEST_DAMAGE, LOWEST_HEALTH
    }

    private final EnumSetting<CrystalPriority> crystalPriority;

    // Rotation Mode
    public enum RotationMode {
        NONE, INSTANT, SMOOTH
    }

    private final EnumSetting<RotationMode> rotationMode;
    private final BooleanSetting swingHand;
    private final BooleanSetting ignoreWalls;
    private ColorSetting color = new ColorSetting("tilebreaker_color", "Color", "Color", new Color(0, 1f, 1f));

    private long lastAttackTime;
    private long lastPlaceTime;
    private BlockPos placePos;
    private final long BOX_DISPLAY_TIME_MS = 3000;
    private final Map<BlockPos, Long> displayedBoxes = new HashMap<>();

    public CrystalAura() {
        super(new KeybindSetting("key.crystalaura", "Crystal Aura Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("CrystalAura");
        this.setCategory(Category.of("Combat"));
        this.setDescription("Attacks anything within your personal space with a End Crystal.");

        radius = new FloatSetting("crystalaura_radius", "Radius", "Radius, in blocks, that you can place/attack a crystal.", 5f, 1f, 15f, 1f);
        placeRadius = new FloatSetting("crystalaura_place_radius", "Place Radius", "Radius, in blocks, that you can place/attack a crystal.", 5f, 1f, 15f, 1f);
        targetFriends = new BooleanSetting("crystalaura_target_friends", "Target Friends", "Target friends.", false);
        attackDelay = new FloatSetting("crystalaura_attack_delay", "Attack Delay", "Delay between attacks in milliseconds.", 500, 0, 2000, 50);
        autoSwitch = new BooleanSetting("crystalaura_auto_switch", "Auto Switch", "Automatically switch to End Crystal.", true);
        placeDelay = new FloatSetting("crystalaura_place_delay", "Place Delay", "Delay between placing crystals in milliseconds.", 500, 0, 2000, 50);
        targetMode = new EnumSetting<>("crystalaura_target_mode", "Target Mode", "Mode to target players.", TargetMode.NEAREST);
        multiPlace = new BooleanSetting("crystalaura_multi_place", "MultiPlace", "Allows placing multiple crystals simultaneously.", false);
        antiSuicide = new BooleanSetting("crystalaura_anti_suicide", "AntiSuicide", "Prevents attacking crystals if it would result in player's death.", true);
        minDamage = new FloatSetting("crystalaura_min_damage", "Min Damage", "Minimum damage a crystal must deal to be placed or attacked.", 6f, 0f, 36f, 0.5f);
        maxSelfDamage = new FloatSetting("crystalaura_max_self_damage", "Max Self Damage", "Maximum self-damage the player can take from a single crystal.", 4f, 0f, 20f, 0.5f);
        enemyRange = new FloatSetting("crystalaura_enemy_range", "Enemy Range", "Maximum distance an enemy can be to be considered a target.", 12f, 0f, 32f, 1f);
        wallRange = new FloatSetting("crystalaura_wall_range", "Wall Range", "Distance an enemy must be to a wall for crystals to be placed or attacked through it.", 3f, 0f, 8f, 0.5f);
        crystalPriority = new EnumSetting<>("crystalaura_crystal_priority", "Crystal Priority", "Prioritize which crystals to attack first.", CrystalPriority.CLOSEST);
        rotationMode = new EnumSetting<>("crystalaura_rotation_mode", "Rotation Mode", "Controls how the player's view rotates.", RotationMode.NONE);
        handSetting = new EnumSetting<>("crystalaura_hand_setting", "Hand Setting", "The hand to use for interactions.", HandSetting.MAIN_HAND);
        swingHand = new BooleanSetting("crystalaura_swing_hand", "Swing Hand", "Swing hand after interacting.", true);
        ignoreWalls = new BooleanSetting("crystalaura_ignore_walls", "Ignore Walls", "Ignore walls when targeting enemies.", false);

        this.addSetting(ignoreWalls);
        this.addSetting(swingHand);
        this.addSetting(handSetting);
        this.addSetting(radius);
        this.addSetting(placeRadius);
        this.addSetting(targetFriends);
        this.addSetting(attackDelay);
        this.addSetting(autoSwitch);
        this.addSetting(placeDelay);
        this.addSetting(targetMode);
        this.addSetting(multiPlace);
        this.addSetting(antiSuicide);
        this.addSetting(minDamage);
        this.addSetting(maxSelfDamage);
        this.addSetting(enemyRange);
        this.addSetting(wallRange);
        this.addSetting(crystalPriority);
        this.addSetting(rotationMode);
        this.addSetting(color);

        lastAttackTime = 0;
        lastPlaceTime = 0;
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(PreTickListener.class, this);
        Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(PreTickListener.class, this);
        Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
    }

    @Override
    public void onToggle() {

    }

    @Override
    public void onPreTick(PreTickEvent event) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPlaceTime >= placeDelay.getValue()) {
            placeCrystal();
            lastPlaceTime = currentTime;
        }

        if (currentTime - lastAttackTime >= attackDelay.getValue()) {
            attackCrystal();
            lastAttackTime = currentTime;
        }
    }


    private void placeCrystal() {
        List<AbstractClientPlayerEntity> players = MC.world.getPlayers();

        Hand hand = handSetting.getValue() == HandSetting.MAIN_HAND ? Hand.MAIN_HAND : Hand.OFF_HAND;

        FindItemResult result = Module.find(Items.END_CRYSTAL);

        if (!result.found() && !result.isHotbar()) return;

        for (PlayerEntity player : players) {
            if (player == MC.player || MC.player.squaredDistanceTo(player) > radius.getValueSqr()) continue;
            if (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(player)) continue;

            Optional<BlockPos> bestPos = findBestCrystalPlacement(player);
            if (bestPos.isPresent()) {
                placePos = bestPos.get();

                double damage = DamageUtils.crystalDamage(player, Vec3d.of(placePos.add(0, 1, 0)));
                if (damage < minDamage.getValue()) continue;

                if (autoSwitch.getValue()) {
                    Module.swap(result.slot(), false);
                }

                if (multiPlace.getValue()) {
                    performMultiPlace(placePos);
                } else {
                    BlockHitResult hitResult = new BlockHitResult(placePos.toCenterPos(), Direction.UP, placePos, false);
                    MC.interactionManager.interactBlock(MC.player, hand, hitResult);
                    MC.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(hand, 0, MC.player.getYaw(), MC.player.getPitch()));
                }

                displayedBoxes.put(placePos, System.currentTimeMillis()); // Store rendering time
                lastPlaceTime = System.currentTimeMillis();
                break;
            }
        }
    }

    private void performMultiPlace(BlockPos initialPos) {
        Hand hand = handSetting.getValue() == HandSetting.MAIN_HAND ? Hand.MAIN_HAND : Hand.OFF_HAND;

        BlockPos[] positions = {initialPos, initialPos.up(), initialPos.east(), initialPos.west(), initialPos.north(), initialPos.south()};

        for (BlockPos pos : positions) {
            BlockHitResult hitResult = new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, false);
            MC.interactionManager.interactBlock(MC.player, hand, hitResult);
            MC.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(hand, 0, MC.player.getYaw(), MC.player.getPitch()));
        }
    }

    private Optional<BlockPos> findBestCrystalPlacement(PlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        double maxDamage = 0;
        double minDistance = Double.MAX_VALUE;
        BlockPos bestPos = null;

        int radius = placeRadius.getValue().intValue();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState blockState = MC.world.getBlockState(pos);
                    Block block = blockState.getBlock();
                    if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) continue;

                    // Ensure the block above is air
                    BlockPos abovePos = pos.up();
                    BlockState aboveBlockState = MC.world.getBlockState(abovePos);
                    if (!aboveBlockState.isAir()) continue;

                    // Ensure there is air two blocks above the potential placement
                    BlockPos abovePos2 = pos.up(2);
                    BlockState aboveBlockState2 = MC.world.getBlockState(abovePos2);
                    if (!aboveBlockState2.isAir()) continue;

                    if (pos.getY() > playerPos.getY() + 1) continue;

                    double damage = DamageUtils.crystalDamage(player, Vec3d.of(pos));
                    double distance = playerPos.getSquaredDistance(pos);

                    if (pos.getY() == playerPos.getY()) {
                        damage *= 1.5;
                    }

                    if (damage > maxDamage || (damage == maxDamage && distance < minDistance)) {
                        maxDamage = damage;
                        minDistance = distance;
                        bestPos = pos;
                    }
                }
            }
        }

        return Optional.ofNullable(bestPos);
    }

    private PlayerEntity getTargetPlayer() {
        List<AbstractClientPlayerEntity> players = MC.world.getPlayers();
        PlayerEntity targetPlayer = null;
        double targetValue = (targetMode.getValue() == TargetMode.NEAREST) ? Double.MAX_VALUE : 0;

        for (PlayerEntity player : players) {
            if (player == MC.player || !targetFriends.getValue() && Aoba.getInstance().friendsList.contains(player))
                continue;
            double distance = MC.player.squaredDistanceTo(player);
            double health = player.getHealth();

            if (targetMode.getValue() == TargetMode.NEAREST && distance < targetValue) {
                targetValue = distance;
                targetPlayer = player;
            } else if (targetMode.getValue() == TargetMode.MOST_HEALTH && health > targetValue) {
                targetValue = health;
                targetPlayer = player;
            }
        }

        return targetPlayer;
    }

    private void attackCrystal() {
        PlayerEntity targetPlayer = getTargetPlayer();

        Hand hand = handSetting.getValue() == HandSetting.MAIN_HAND ? Hand.MAIN_HAND : Hand.OFF_HAND;

        if (targetPlayer == null) return;

        EndCrystalEntity bestCrystal = null;
        double maxDamage = 0;

        double maxSelfDamageAllowed = maxSelfDamage.getValue();
        double enemyRangeSquared = enemyRange.getValue() * enemyRange.getValue();
        double wallRangeSquared = wallRange.getValue() * wallRange.getValue();

        Iterable<Entity> entities = MC.world.getEntities();
        for (Entity entity : entities) {
            if (entity instanceof EndCrystalEntity) {
                double distanceSquared = MC.player.squaredDistanceTo(entity);
                if (distanceSquared < enemyRangeSquared) {
                    double damage = DamageUtils.crystalDamage(targetPlayer, entity.getPos());
                    double selfDamage = DamageUtils.crystalDamage(MC.player, entity.getPos());

                    if (damage < minDamage.getValue() || (antiSuicide.getValue() && selfDamage > maxSelfDamageAllowed)) {
                        continue;
                    }

                    boolean isBehindWall = distanceSquared > wallRangeSquared && isCrystalBehindWall(entity);

                    if (!ignoreWalls.getValue() && isBehindWall) {
                        continue;
                    }

                    switch (crystalPriority.getValue()) {
                        case CLOSEST:
                            if (bestCrystal == null || distanceSquared < MC.player.squaredDistanceTo(bestCrystal)) {
                                bestCrystal = (EndCrystalEntity) entity;
                                maxDamage = damage;
                            }
                            break;
                        case HIGHEST_DAMAGE:
                            if (damage > maxDamage) {
                                bestCrystal = (EndCrystalEntity) entity;
                                maxDamage = damage;
                            }
                            break;
                        case LOWEST_HEALTH:
                            double targetHealth = targetPlayer.getHealth();
                            if (targetHealth > 0 && targetHealth < MC.player.getHealth()) {
                                bestCrystal = (EndCrystalEntity) entity;
                                maxDamage = damage;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        if (bestCrystal != null) {
            Vec3d targetPos = bestCrystal.getPos().add(0, bestCrystal.getBoundingBox().getLengthY() / 2.0, 0);

            switch (rotationMode.getValue()) {
                case NONE:
                    MC.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(bestCrystal, MC.player.isSneaking()));
                    if (swingHand.getValue()) MC.player.swingHand(hand);
                    break;
                case INSTANT:
                    MC.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, targetPos);
                    MC.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(bestCrystal, MC.player.isSneaking()));
                    break;
                case SMOOTH:
                    // Instant rotation for now because im too dumb to figure out smooth rotation
                    MC.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, targetPos);

                    MC.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(bestCrystal, MC.player.isSneaking()));
                    if (swingHand.getValue()) MC.player.swingHand(hand);

                    break;
                default:
                    break;
            }
        }
    }

    private boolean isCrystalBehindWall(Entity crystal) {
        BlockPos crystalPos = crystal.getBlockPos();
        Vec3d playerEyePos = MC.player.getCameraPosVec(1.0f);

        Vec3d toCrystalVec = crystal.getPos().subtract(playerEyePos);

        Vec3d extendedVec = playerEyePos.add(toCrystalVec.multiply(1.1));
        VoxelShape crystalShape = VoxelShapes.cuboid(crystal.getBoundingBox());

        BlockHitResult result = MC.world.raycastBlock(playerEyePos, extendedVec, crystalPos, crystalShape, crystal.getBlockStateAtPos());

        if (result != null && result.getType() == BlockHitResult.Type.BLOCK) {
            BlockPos blockPos = result.getBlockPos();
            BlockState blockState = MC.world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            return block != Blocks.AIR && blockState.isSolidBlock(MC.world, blockPos);
        }

        return false;
    }

    @Override
    public void OnRender(Render3DEvent event) {
        long currentTime = System.currentTimeMillis();

        displayedBoxes.entrySet().removeIf(entry -> {
            long renderTime = entry.getValue();
            if (currentTime - renderTime >= BOX_DISPLAY_TIME_MS) {
                return true;
            }
            return false;
        });

        for (Map.Entry<BlockPos, Long> entry : displayedBoxes.entrySet()) {
            BlockPos pos = entry.getKey();
            Render3D.draw3DBox(event.GetMatrix(), new Box(pos), color.getValue(), 1.0f);
        }
    }
}