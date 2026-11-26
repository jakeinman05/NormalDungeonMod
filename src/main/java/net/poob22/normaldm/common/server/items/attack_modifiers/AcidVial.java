package net.poob22.normaldm.common.server.items.attack_modifiers;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;
import net.poob22.normaldm.common.server.mobeffects.NDMEffects;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class AcidVial extends AttackModItem {
    public AcidVial(Properties pProperties) {
        super(pProperties);
        setTitle("Acid Vial");
        setSubtitle("Acid Hands");
    }

    @Override
    public void doEffectOn(Entity entity) {
        if(entity instanceof LivingEntity livingEntity) {
            if(livingEntity.addEffect(new MobEffectInstance(NDMEffects.ACID.get(), 200, 0, false, false))) {

            }
        }
    }

    @Override
    public void applyStats(Player player) {
        player.getCapability(COMBAT).ifPresent(c -> {
            c.getStats().addToStat(StatType.REACH, -0.1F);
        });
    }
}
