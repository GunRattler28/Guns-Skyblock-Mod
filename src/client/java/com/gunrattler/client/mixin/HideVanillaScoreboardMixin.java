package com.gunrattler.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.DisplaySlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Scoreboard.class)
public class HideVanillaScoreboardMixin {

    @Inject(method = "getDisplayObjective", at = @At("HEAD"), cancellable = true)
    private void hideSidebar(DisplaySlot slot, CallbackInfoReturnable<Objective> cir) {
        if (slot == DisplaySlot.SIDEBAR) {
            Minecraft mc = Minecraft.getInstance();
            
            if (mc.getCurrentServer() != null && mc.level != null) {
                String ip = mc.getCurrentServer().ip.toLowerCase();
                
                if (ip.contains("hypixel.net")) {
                    Scoreboard scoreboard = (Scoreboard) (Object) this;
                    
                    Objective skyblockObjective = scoreboard.getObjective("SBScoreboard");
                    
                    if (skyblockObjective != null) {
                        String title = skyblockObjective.getDisplayName().getString().toLowerCase();
                        
                        if (title.contains("skyblock")) {
                            cir.setReturnValue(null); 
                        }
                    }
                }
            }
        }
    }
}