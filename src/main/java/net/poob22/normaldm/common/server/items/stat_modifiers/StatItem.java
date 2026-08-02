package net.poob22.normaldm.common.server.items.stat_modifiers;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class StatItem extends Item implements IStatModifier{
    protected String name;
    protected Component title;
    protected Component subtitle;

    public StatItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand useHand) {
        if(level.isClientSide) {
            return InteractionResultHolder.pass(player.getItemInHand(useHand));
        }

        if(player.getItemInHand(useHand).getItem() == this){
            applyStats(player);
            player.getItemInHand(useHand).shrink(1);
        }

        return super.use(level, player, useHand);
    }

    @Override
    public abstract void applyStats(Player player);

    public Component getTitle() {
        return title;
    }

    public Component getSubtitle() {
        return subtitle;
    }

    protected void setTitle(String title) {
        this.title = Component.literal(title);
    }

    protected void setSubtitle(String subtitle) {
        this.subtitle = Component.literal(subtitle);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
