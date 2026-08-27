package net.poob22.normaldm.common.server.items.attack_modifiers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.coremod.transformer.CoreModBaseTransformer;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class AcidVial extends AttackModItem {
    public AcidVial(Properties pProperties) {
        super(pProperties);
        setTitle("Acid Vial");
        setSubtitle("MY HANDS ARE MELTING AAAAAAAAAH!!!");
    }

    @Override
    public void doEffectOn(Entity entity) {
        NormalDungeonMod.LOGGER.info("Yay did an effect yippee!");
        // apply custom acid effect on entity
    }

    @Override
    public void applyStats(Player player) {
        player.getCapability(COMBAT).ifPresent(c -> {
            c.getStats().addToStat(StatType.REACH, -0.1F);
        });
    }
}
