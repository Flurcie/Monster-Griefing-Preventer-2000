package monster.m;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mgriefpreventr implements ModInitializer {
	public static final String MOD_ID = "mgriefpreventr";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final GameRules.Key<GameRules.BooleanRule> DO_MONSTER_GRIEFING =
			GameRuleRegistry.register("doMonsterGriefing", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> DO_ENDERMAN_GRIEFING =
			GameRuleRegistry.register("doEndermanGriefing", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));

	@Override
	public void onInitialize() {

		LOGGER.info("Hello Fabric world!");
	}
}