package net.poob22.normaldm.common.server.entity.definition;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public class DungeonMobDefinition<T extends Mob> {
    public final String id;
    public final EntityType.EntityFactory<T> factory;
    public final MobCategory mobCategory;
    public final float width;
    public final float height;

    public final Supplier<AttributeSupplier.Builder> attributes;

    public final ModelLayerLocation layerLocation;
    public final Supplier<LayerDefinition> layerDefinition;

    public final EntityRendererProvider<T> renderer;

    public DungeonMobDefinition(String id, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height, Supplier<AttributeSupplier.Builder> attributes, ModelLayerLocation layerLocation, Supplier<LayerDefinition> layerDefinition, EntityRendererProvider<T> renderer) {
        this.id = id;
        this.factory = factory;
        this.mobCategory = category;
        this.width = width;
        this.height = height;
        this.attributes = attributes;
        this.layerLocation = layerLocation;
        this.layerDefinition = layerDefinition;
        this.renderer = renderer;
    }
}
