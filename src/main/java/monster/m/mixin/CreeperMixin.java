package monster.m.mixin;

import monster.m.Mgriefpreventr;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CreeperEntity.class)
public abstract class CreeperMixin extends HostileEntity {
    protected CreeperMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void customExplode(CallbackInfo ci) {
        World world = this.getWorld();
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            boolean doMonsterGriefing = serverWorld.getGameRules().getBoolean(Mgriefpreventr.DO_MONSTER_GRIEFING);
            float power = this.isCharged() ? 6.0F : 3.0F;
            this.dead = true;
            Vec3d pos = new Vec3d(this.getX(), this.getY(), this.getZ());
            Explosion.DestructionType destructionType = doMonsterGriefing ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.KEEP;
            ExplosionImpl explosion = new ExplosionImpl(serverWorld, this, null, null, pos, power, false, destructionType);
            explosion.explode();

            // Send explosion packet to clients for sound and particles
            serverWorld.getServer().getPlayerManager().getPlayerList().forEach(player -> {
                if (player.squaredDistanceTo(pos) < 4096.0F) { // 64 blocks squared
                    Vec3d knockback = explosion.getKnockbackByPlayer().get(player);
                    player.networkHandler.sendPacket(new ExplosionS2CPacket(
                            pos,
                            knockback != null ? Optional.of(knockback) : Optional.empty(),
                            power >= 2.0F ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION, // Match vanilla size logic
                            SoundEvents.ENTITY_GENERIC_EXPLODE // Vanilla explosion sound
                    ));
                }
            });

            this.spawnEffectsCloud();
            this.discard();
            ci.cancel();
        }
    }

    @Shadow
    public abstract boolean isCharged();

    @Shadow
    private void spawnEffectsCloud() {}
}