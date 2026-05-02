package net.poob22.normaldm.common.server.blocks.properties;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RoomDefinition {
    String name;

    List<RoomVolume> volumes;

    RoomType type;

    public RoomDefinition(@NotNull String name, @NotNull RoomType type, RoomVolume... volumes) {
        this.name = name;
        this.type = type;
        this.volumes = List.of(volumes);
    }

    public List<RoomVolume> getVolumes() {
        return this.volumes;
    }

    public String toString() {
        return name.toUpperCase();
    }
}
