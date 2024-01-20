package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.utils.LocationUtil;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TprandomCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the TprandomCommand class.
     * @param plugin References to the main plugin class.
     */
    public TprandomCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * @param commandSender Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            String[] args
    ) {
        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("tprandom")) {
            if (permission.has("essence.teleport.random")) {
                message.PrivateMessage("Finding somewhere to go...", false);
                WorldBorder wb;
                try {
                    wb = Objects.requireNonNull(Bukkit.getWorld(player.getWorld().getUID())).getWorldBorder();
                } catch (NullPointerException e) {
                    this.log.warn("NullPointerException randomly teleporting: " + e);
                    message.PrivateMessage("Unable to teleport due to an error, please see the console for more information.", true);
                    return true;
                }

                LocationUtil loc = new LocationUtil(this.plugin, message);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location teleportLocation = loc.GetRandomLocation(player, wb);
                        if (teleportLocation.getY() != -64) {
                            message.PrivateMessage("Teleporting...", false);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Chunk chunk = teleportLocation.getChunk();
                                    if (!chunk.isLoaded()) {
                                        chunk.load(true);
                                    }
                                    teleportPlayer(player, teleportLocation);
                                }
                            }.runTask(plugin);
                        } else {
                            message.PrivateMessage("Couldn't find suitable location, please try again.", true);
                        }
                    }
                }.runTaskAsynchronously(this.plugin);
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }

    public void teleportPlayer(Player player, Location loc) {
        player.teleport(loc);
    }
}