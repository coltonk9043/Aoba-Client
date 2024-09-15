/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.mixin.interfaces;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

@Mixin(MinecraftClient.class)
public interface IMinecraftClient {
    @Accessor("itemUseCooldown")
    int getItemUseCooldown();

    @Accessor("itemUseCooldown")
    void setItemUseCooldown(int value);

    @Invoker("doItemUse")
    void useItem();

    @Mutable
    @Accessor("session")
    void setSession(Session session);
    
    @Accessor("authenticationService")
    YggdrasilAuthenticationService getAuthenticationService();
    
    @Mutable
    @Accessor("socialInteractionsManager")
    void setSocialInteractionsManager(SocialInteractionsManager socialInteractionsManager);
    
    @Mutable
    @Accessor("abuseReportContext")
    void setAbuseReportContext(AbuseReportContext abuseReportContext);
    
    @Mutable
    @Accessor("gameProfileFuture")
    void setGameProfileFuture(CompletableFuture<ProfileResult> future);
    
    @Mutable
    @Accessor("profileKeys")
    void setProfileKeys(ProfileKeys keys);
    
    @Mutable
    @Accessor
    void setUserApiService(UserApiService apiService);
}
