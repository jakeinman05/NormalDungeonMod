package net.poob22.normaldm.common.server.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.common.server.entity.definition.LaserType;
import net.poob22.normaldm.common.server.entity.projectile.BioluminescentBeamEntity;
import org.jetbrains.annotations.NotNull;

public class LaserStick extends Item {

    public LaserStick(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand pUsedHand) {
        if(!level.isClientSide) {
            BioluminescentBeamEntity beam = new BioluminescentBeamEntity(level, player, player, 20, 21, false, LaserType.STRAIGHT);

            beam.setPos(player.getX(), player.getEyeY() - 1F, player.getZ());
            level.addFreshEntity(beam);

            player.getCooldowns().addCooldown(this, 20);

            return InteractionResultHolder.success(player.getItemInHand(pUsedHand));
        }


        return super.use(level, player, pUsedHand);
    }
}
