package com.antbasi.bedrockfireworks.mixin;

import com.antbasi.bedrockfireworks.BedrockFireworks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin {

    /**
     * Vanilla pulls the player's velocity toward the look direction only 50% per tick,
     * so after a turn the old direction keeps sliding for several ticks (the "drift").
     * With snapDirection on, the velocity points exactly where you look, every tick,
     * at a constant boostSpeed (no ramp-up).
     */
    @Redirect(
        method = "tick",
        at = @At(value = "INVOKE",
                 target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V")
    )
    private void bedrockfireworks$boost(LivingEntity shooter, Vec3d vanillaResult) {
        if (BedrockFireworks.enabled && BedrockFireworks.snapDirection) {
            shooter.setVelocity(shooter.getRotationVector().multiply(BedrockFireworks.boostSpeed));
        } else {
            shooter.setVelocity(vanillaResult);
        }
    }

    // Rocket entity's own motion (defaults = vanilla values)
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 1.15))
    private double bedrockfireworks$horizontalMultiplier(double vanilla) {
        return BedrockFireworks.enabled ? BedrockFireworks.horizontalMultiplier : vanilla;
    }

    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 0.04))
    private double bedrockfireworks$verticalAcceleration(double vanilla) {
        return BedrockFireworks.enabled ? BedrockFireworks.verticalAcceleration : vanilla;
    }
}
