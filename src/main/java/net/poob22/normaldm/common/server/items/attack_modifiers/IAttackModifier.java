package net.poob22.normaldm.common.server.items.attack_modifiers;

import net.minecraft.world.entity.Entity;

public interface IAttackModifier {
    void doEffectOn(Entity entity);
}
