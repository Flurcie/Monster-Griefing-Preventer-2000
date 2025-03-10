package monster.m.mixin;

import monster.m.Mgriefpreventr;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Blocks silverfish from destroying, but not entering, blocks.
//This limits griefing while still leaving infested block variants accessible, should any mod or content ever want to make use of them.
//Also stops me having to troubleshoot why the "please don't enter block" mixins won't work.

@Mixin(targets = "net.minecraft.entity.mob.SilverfishEntity$CallForHelpGoal")
public abstract class SilverfishMixin {
    @Shadow @Final private SilverfishEntity silverfish;

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    private void preventCallingAllies(CallbackInfo ci) {
        if (this.silverfish.getWorld() instanceof ServerWorld serverWorld && !serverWorld.getGameRules().getBoolean(Mgriefpreventr.DO_SILVERFISH_GRIEFING)) {
            ci.cancel();
        }
    }
}