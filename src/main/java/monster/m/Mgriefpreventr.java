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

	//MonsterGriefing = Turns off creeper, ghast, and enderman griefing. These are the worst offenders.
	//EndermanGriefing = Allows me to only turn off enderman griefing, which is how I prefer to play.
	//Silverfish Griefing, does not fully turn off silverfish griefing as they may still enter blocks, but they will not leave without player interference.
	//separated from MonsterGriefing as this is technically not a full shutdown on their ability to grief, just a limitation.

	public static final GameRules.Key<GameRules.BooleanRule> DO_MONSTER_GRIEFING =
			GameRuleRegistry.register("doMonsterGriefing", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));

	public static final GameRules.Key<GameRules.BooleanRule> DO_ENDERMAN_GRIEFING =
			GameRuleRegistry.register("doEndermanGriefing", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));

	public static final GameRules.Key<GameRules.BooleanRule> DO_SILVERFISH_GRIEFING =
			GameRuleRegistry.register("doSilverfishGriefing", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));

	@Override
	public void onInitialize() {

		LOGGER.info("Hello Fabric world!");
	}
}