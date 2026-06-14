/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.entity;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

public class EntityUtils {
	protected static final AobaClient AOBA_CLIENT = Aoba.getInstance();
	protected static final Minecraft MC = AobaClient.MC;

	public static boolean isInFOV(Entity entity, float fov) {
		return isInFOV(entity.getEyePosition(), fov);
	}

	public static boolean isInFOV(Entity entity, BodyPart part, float fov) {
		return isInFOV(getBodyPartPosition(entity, part, 1.0f), fov);
	}

	public static boolean isInFOV(Entity entity, BodyPart part, float fov, float frameDelta) {
		return isInFOV(getBodyPartPosition(entity, part, frameDelta), fov);
	}

	public static boolean isInFOV(Vec3 position, float fov) {
		if (fov >= 360.0f)
			return true;
		Vec3 viewVector = MC.player.getViewVector(1.0f);
		Vec3 vecToTarget = position.subtract(MC.player.getEyePosition()).normalize();
		double cosAngle = viewVector.dot(vecToTarget);
		return cosAngle >= Math.cos(Math.toRadians(fov / 2.0f));
	}

	public static Vec3 getBodyPartPosition(Entity entity, BodyPart part, float frameDelta) {
		Vec3 entityPos = entity.getPosition(frameDelta);
		double entityEyeHeight = entity.getEyeHeight();
		double bbWidth = entity.getBbWidth();

		double yaw;
		if(entity instanceof LivingEntity living)
			yaw = Math.toRadians(living.yBodyRot);
		else
			yaw = Math.toRadians(entity.getYRot());
			
		double rightX = -Math.cos(yaw);
		double rightZ = -Math.sin(yaw);

		switch (part) {
			case HEAD:
				return entityPos.add(0, entityEyeHeight * 0.95, 0);
			case CHEST:
				return entityPos.add(0, entityEyeHeight * 0.65, 0);
			case LEFT_ARM:
				return entityPos.add(-rightX * bbWidth * 0.4, entityEyeHeight * 0.6, -rightZ * bbWidth * 0.4);
			case RIGHT_ARM:
				return entityPos.add(rightX * bbWidth * 0.4, entityEyeHeight * 0.6, rightZ * bbWidth * 0.4);
			case LEFT_LEG:
				return entityPos.add(-rightX * bbWidth * 0.2, entityEyeHeight * 0.3, -rightZ * bbWidth * 0.2);
			case RIGHT_LEG:
				return entityPos.add(rightX * bbWidth * 0.2, entityEyeHeight * 0.3, rightZ * bbWidth * 0.2);
			default:
				return entityPos.add(0, entityEyeHeight * 0.65, 0);
		}
	}

	public static GameType getGameMode(Player player) {
		if (player == null)
			return null;
		PlayerInfo playerListEntry = MC.getConnection().getPlayerInfo(player.getUUID());
		if (playerListEntry == null)
			return null;
		return playerListEntry.getGameMode();
	}
	
	/**
	 * Gets whether the player is possibly a bot.
	 * @param player Player to check.
	 * @return True if the player could be a bot, false otherwise.
	 */
	public static boolean isNPC(Player player) {
		if (player == null)
			return false;
		
		ClientPacketListener connection = MC.getConnection();
		if (connection == null)
			return false;

		// Check if the player is on the tab-list
		PlayerInfo info = connection.getPlayerInfo(player.getUUID());
		if (info == null)
			return true;

		// Assume that the server is in OFFLINE MODE if the 
		// current player's UUID version is NOT 4. 
		// Skip the remaining checks as every player will be flagged.
		boolean serverIsOffline = MC.player != null && MC.player.getUUID().version() != 4;
		if (serverIsOffline)
			return false;

		UUID uuid = player.getUUID();
		GameProfile profile = info.getProfile();

		// Mojang official UUIDs use V4, not V3 or V5.
		if (uuid.version() != 4)
			return true;
	
		// Ensure that the player has signed textures.
		// In an online server- every player SHOULD have a valid signature
		// on their textures.
		Collection<Property> textures = profile.properties().get("textures");
		if (textures.isEmpty())
			return true;
		
		boolean hasSignedTexture = false;
		for (Property prop : textures) {
			String signature = prop.signature();
			if (signature != null && !signature.isEmpty()) {
				hasSignedTexture = true;
				break;
			}
		}
	
		if (!hasSignedTexture)
			return true;
		return false;
	}

	public static boolean isFriend(Player player) {
		return Aoba.getInstance().friendsList.contains(player);
	}
}
