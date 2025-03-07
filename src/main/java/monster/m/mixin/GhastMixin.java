package monster.m.mixin;

import monster.m.Mgriefpreventr;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
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

@Mixin(FireballEntity.class)
public abstract class GhastMixin extends AbstractFireballEntity {
    @Shadow private int explosionPower;

    protected GhastMixin(EntityType<? extends AbstractFireballEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void customExplosion(HitResult hitResult, CallbackInfo ci) {
        World world = this.getWorld();
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            boolean doMonsterGriefing = serverWorld.getGameRules().getBoolean(Mgriefpreventr.DO_MONSTER_GRIEFING);
            Vec3d pos = new Vec3d(this.getX(), this.getY(), this.getZ());
            Explosion.DestructionType destructionType = doMonsterGriefing ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.KEEP;
            ExplosionImpl explosion = new ExplosionImpl(serverWorld, this, null, null, pos, (float)this.explosionPower, false, destructionType);
            explosion.explode();

            // Send explosion packet to clients
            RegistryEntry.Reference<SoundEvent> explosionSound = RegistryEntry.of(SoundEvents.ENTITY_GENERIC_EXPLODE).value();
            serverWorld.getServer().getPlayerManager().getPlayerList().forEach(player -> {
                if (player.squaredDistanceTo(pos) < 4096.0F) {
                    Vec3d knockback = explosion.getKnockbackByPlayer().get(player);
                    player.networkHandler.sendPacket(new ExplosionS2CPacket(
                            pos,
                            knockback != null ? Optional.of(knockback) : Optional.empty(),
                            this.explosionPower >= 2.0F ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION,
                            explosionSound
                    ));
                }
            });

            this.discard();
            ci.cancel();
        }
    }
}