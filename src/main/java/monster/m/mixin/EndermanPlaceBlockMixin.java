package monster.m.mixin;

import monster.m.Mgriefpreventr;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.entity.mob.EndermanEntity$PlaceBlockGoal")
public abstract class EndermanPlaceBlockMixin {
    @Shadow @Final private EndermanEntity enderman;

    @Inject(method = "canStart()Z", at = @At("HEAD"), cancellable = true)
    private void preventPlacement(CallbackInfoReturnable<Boolean> cir) {
        World world = this.enderman.getWorld();
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            boolean doMonsterGriefing = serverWorld.getGameRules().getBoolean(Mgriefpreventr.DO_MONSTER_GRIEFING);
            boolean doEndermanGriefing = serverWorld.getGameRules().getBoolean(Mgriefpreventr.DO_ENDERMAN_GRIEFING);
            if (!doMonsterGriefing || !doEndermanGriefing) {
                cir.setReturnValue(false); // Prevent placing blocks
            }
        }
    }
}