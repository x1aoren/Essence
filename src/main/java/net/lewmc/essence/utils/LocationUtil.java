package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Random;

public class LocationUtil {
    private final Essence plugin;
    private final MessageUtil message;

    public LocationUtil(Essence plugin, MessageUtil message) {
        this.plugin = plugin;
        this.message = message;
    }

    public void UpdateLastLocation(Player player) {

        DataUtil data = new DataUtil(this.plugin, this.message);
        data.load(data.playerDataFile(player));

        if (!data.sectionExists("last-location")) {
            data.createSection("last-location");
        }
        ConfigurationSection cs = data.getSection("last-location");
        cs.set("world", player.getLocation().getWorld().getName());
        cs.set("X", player.getLocation().getX());
        cs.set("Y", player.getLocation().getY());
        cs.set("Z", player.getLocation().getZ());
        cs.set("yaw", player.getLocation().getYaw());
        cs.set("pitch", player.getLocation().getPitch());

        data.save();
    }

    public Location GetRandomLocation(Player player, WorldBorder wb) {
        World world = player.getWorld();
        double maxCoords = (wb.getSize() / 2) - 1;

        Random rand = new Random();
        int x = rand.nextInt((int) maxCoords);
        int z = rand.nextInt((int) maxCoords);
        int y = GetRandomY(world, x, z);
        return new Location(world, (float) x, (float) y, (float) z);
    }

    private int GetRandomY(World world, int x, int z) {
        int y = 319;

        Material block = world.getBlockAt(x, y, z).getType();
        while ((block == Material.AIR)) {
            y--;
            block = world.getBlockAt(x, y, z).getType();
            if (y == -64) { break; }
        }

        if (!LocationIsSafe(block) || y == -64) { y = GetRandomY(world, x, z); }

        return y + 1;
    }

    private boolean LocationIsSafe(Material block) {
        return block != Material.LAVA && block != Material.WATER && block != Material.MAGMA_BLOCK && block != Material.POWDER_SNOW && block != Material.CACTUS && block != Material.VOID_AIR;
    }
}
