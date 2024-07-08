package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.aoba.utils.entity.DamageUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

public class CrystalAura extends Module implements TickListener {

    private final FloatSetting radius;
    private final BooleanSetting targetFriends;
    private final FloatSetting attackDelay;
    private final BooleanSetting autoSwitch;
    private final FloatSetting placeDelay;
    private final EnumSetting<TargetMode> targetMode;

    public enum TargetMode {
        NEAREST,
        MOST_HEALTH
    }

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


    private long lastAttackTime;
    private long lastPlaceTime;
    private BlockPos placePos;
    private final int explosionRadius = 6;

    public CrystalAura() {
        super(new KeybindSetting("key.crystalaura", "Crystal Aura Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("CrystalAura");
        this.setCategory(Category.Combat);
        this.setDescription("Attacks anything within your personal space.");

        radius = new FloatSetting("crystalaura_radius", "Radius", "Radius, in blocks, that you can place/attack a crystal.", 5f, 1f, 15f, 1f);
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

        this.addSetting(radius);
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

        lastAttackTime = 0;
        lastPlaceTime = 0;
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onToggle() {

    }

    @Override
    public void OnUpdate(TickEvent event) {
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
        ItemStack[] inventory = MC.player.getInventory().main.toArray(new ItemStack[0]);
        boolean switchSlot = autoSwitch.getValue();

        int crystalSlot = -1;
        if (switchSlot) {
            for (int slot = 0; slot < 9; slot++) {
                if (inventory[slot].getItem() == Items.END_CRYSTAL) {
                    crystalSlot = slot;
                    break;
                }
            }
            if (crystalSlot == -1) return;
        }

        double radiusSquared = radius.getValue() * radius.getValue();
        for (PlayerEntity player : players) {
            if (player == MC.player || MC.player.squaredDistanceTo(player) > radiusSquared) continue;
            if (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(player)) continue;

            Optional<BlockPos> bestPos = findBestCrystalPlacement(player);
            if (bestPos.isPresent()) {
                placePos = bestPos.get();

                double damage = DamageUtils.crystalDamage(player, Vec3d.of(placePos));
                if (damage < minDamage.getValue()) continue;

                if (switchSlot) {
                    MC.player.getInventory().selectedSlot = crystalSlot;
                }

                if (multiPlace.getValue()) {
                    performMultiPlace(placePos);
                } else {
                    BlockHitResult hitResult = new BlockHitResult(placePos.toCenterPos(), Direction.UP, placePos, false);
                    MC.interactionManager.interactBlock(MC.player, Hand.MAIN_HAND, hitResult);
                    MC.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, MC.player.getYaw(), MC.player.getPitch()));
                }

                lastPlaceTime = System.currentTimeMillis();
                break;
            }
        }
    }

    private void performMultiPlace(BlockPos initialPos) {
        BlockPos[] positions = {
                initialPos,
                initialPos.up(),
                initialPos.east(),
                initialPos.west(),
                initialPos.north(),
                initialPos.south()
        };

        for (BlockPos pos : positions) {
            BlockHitResult hitResult = new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, false);
            MC.interactionManager.interactBlock(MC.player, Hand.MAIN_HAND, hitResult);
            MC.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, MC.player.getYaw(), MC.player.getPitch()));
        }
    }


    private Optional<BlockPos> findBestCrystalPlacement(PlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        BlockPos[] positionsToCheck = {playerPos.down(), playerPos.north(), playerPos.south(), playerPos.east(), playerPos.west(), playerPos.north().down(), playerPos.south().down(), playerPos.east().down(), playerPos.west().down()};

        double maxDamage = 0;
        BlockPos bestPos = null;

        for (BlockPos pos : positionsToCheck) {
            BlockState blockState = MC.world.getBlockState(pos);
            Block block = blockState.getBlock();
            if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) continue;

            double damage = DamageUtils.crystalDamage(player, Vec3d.of(pos));
            if (damage > maxDamage) {
                maxDamage = damage;
                bestPos = pos;
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

                    switch (crystalPriority.getValue()) {
                        case CLOSEST:
                            if (bestCrystal == null || distanceSquared < MC.player.squaredDistanceTo(bestCrystal)) {
                                if (distanceSquared > wallRangeSquared && isCrystalBehindWall(entity)) {
                                    continue;
                                }
                                bestCrystal = (EndCrystalEntity) entity;
                                maxDamage = damage;
                            }
                            break;
                        case HIGHEST_DAMAGE:
                            if (damage > maxDamage) {
                                if (distanceSquared > wallRangeSquared && isCrystalBehindWall(entity)) {
                                    continue;
                                }
                                bestCrystal = (EndCrystalEntity) entity;
                                maxDamage = damage;
                            }
                            break;
                        case LOWEST_HEALTH:
                            double targetHealth = targetPlayer.getHealth();
                            if (targetHealth > 0 && targetHealth < MC.player.getHealth()) {
                                if (distanceSquared > wallRangeSquared && isCrystalBehindWall(entity)) {
                                    continue;
                                }
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
            // Handling rotation mode
            switch (rotationMode.getValue()) {
                case NONE:
                    MC.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(bestCrystal, MC.player.isSneaking()));
                    MC.player.swingHand(Hand.MAIN_HAND);
                    break;
                case INSTANT:
                    Vec3d lookVec = bestCrystal.getPos().subtract(MC.player.getPos()).normalize();
                    MC.player.setYaw((float) Math.toDegrees(Math.atan2(lookVec.z, lookVec.x)) - 90);
                    MC.player.setPitch((float) -Math.toDegrees(Math.atan2(lookVec.y, Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z))));
                    MC.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(bestCrystal, MC.player.isSneaking()));
                    break;
                case SMOOTH:
                    Vec3d targetPos = bestCrystal.getPos().add(0, bestCrystal.getBoundingBox().getLengthY() / 2.0, 0);
                    smoothLookAt(targetPos.x, targetPos.y, targetPos.z);

                    MC.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(bestCrystal, MC.player.isSneaking()));
                    MC.player.swingHand(Hand.MAIN_HAND);

                    break;
                default:
                    break;
            }
        }
    }

    private void smoothLookAt(double x, double y, double z) {
        double diffX = x - MC.player.getX();
        double diffY = y - (MC.player.getY() + MC.player.getEyeHeight(MC.player.getPose()));
        double diffZ = z - MC.player.getZ();

        double yaw = Math.atan2(diffZ, diffX) * (180.0 / Math.PI) - 90.0;
        double pitch = -Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)) * (180.0 / Math.PI);

        if (rotationMode.getValue() == RotationMode.SMOOTH) {
            float prevYaw = MC.player.getYaw();
            float prevPitch = MC.player.getPitch();

            float yawDiff = (float) (yaw - prevYaw);
            float pitchDiff = (float) (pitch - prevPitch);

            float step = 5.0f;
            MC.player.setYaw(prevYaw + clampAngle(yawDiff, -step, step));
            MC.player.setPitch(prevPitch + clampAngle(pitchDiff, -step, step));
        } else {
            MC.player.setYaw((float) yaw);
            MC.player.setPitch((float) pitch);
        }
    }

    private float clampAngle(float angle, float min, float max) {
        if (angle < min) {
            return min;
        }
        if (angle > max) {
            return max;
        }
        return angle;
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
}