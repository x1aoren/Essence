package net.lewmc.essence.commands.teleportation.tp;

import net.lewmc.essence.utils.*;
import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TeleportCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;
    private MessageUtil message;

    /**
     * Constructor for the TeleportCommand class.
     * @param plugin References to the main plugin class.
     */
    public TeleportCommand(Essence plugin) {
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
        this.message = new MessageUtil(commandSender, plugin);

        Player player = null;

        if (!console(commandSender)) {
            player = (Player) commandSender;
        }

        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("tp")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("tp")) {
                return cmd.disabled(message);
            }

            if (args.length == 0) {
                message.send("teleport", "usage");
                return true;
            }

            // /tp <selector> <x> <y> <z>
            if (args.length >= 4) {
                // 权限检查
                boolean isSelf = false;
                if (commandSender instanceof Player && (args[0].equalsIgnoreCase("@s") || (args[0].equalsIgnoreCase(((Player)commandSender).getName())))) {
                    isSelf = true;
                }
                if (isSelf) {
                    if (!permission.has("essence.teleport.coord")) {
                        return permission.not();
                    }
                } else {
                    if (!(permission.has("essence.teleport.other") && permission.has("essence.teleport.coord"))) {
                        return permission.not();
                    }
                }
                String selector = args[0];
                List<Player> targets = parseSelector(selector, commandSender);
                if (targets.isEmpty()) {
                    message.send("generic", "playernotfound");
                    return true;
                }
                double x, y, z;
                try {
                    Player ref = (commandSender instanceof Player) ? (Player) commandSender : targets.get(0);
                    x = args[1].equals("~") ? ref.getLocation().getX() : Double.parseDouble(args[1]);
                    y = args[2].equals("~") ? ref.getLocation().getY() : Double.parseDouble(args[2]);
                    z = args[3].equals("~") ? ref.getLocation().getZ() : Double.parseDouble(args[3]);
                } catch (Exception e) {
                    message.send("generic", "numberformaterror");
                    return true;
                }
                for (Player t : targets) {
                    Location loc = new Location(t.getWorld(), x, y, z);
                    TeleportUtil tp = new TeleportUtil(this.plugin);
                    tp.doTeleport(t, loc, 0); // Folia兼容
                }
                message.send("teleport", "tocoord", new String[] { x+", "+y+", "+z });
                return true;
            }

            // /tp <selector1> <selector2>  或 /tp <selector1> <player2>
            if (args.length == 2) {
                if (!(permission.has("essence.teleport.other") && permission.has("essence.teleport.player"))) {
                    return permission.not();
                }
                List<Player> fromList = parseSelector(args[0], commandSender);
                List<Player> toList = parseSelector(args[1], commandSender);
                if (fromList.isEmpty() || toList.isEmpty()) {
                    message.send("generic", "playernotfound");
                    return true;
                }
                Player to = toList.get(0);
                for (Player from : fromList) {
                    TeleportUtil tp = new TeleportUtil(this.plugin);
                    tp.doTeleport(from, to.getLocation(), 0); // Folia兼容
                }
                message.send("teleport", "toplayer", new String[] { fromList.stream().map(Player::getName).collect(Collectors.joining(", ")), to.getName() });
                return true;
            }

            // /tp <x> <y> <z>  (自己传送到坐标)
            if (args.length == 3 && player != null) {
                if (!permission.has("essence.teleport.coord")) {
                    return permission.not();
                }
                double x, y, z;
                try {
                    x = args[0].equals("~") ? player.getLocation().getX() : Double.parseDouble(args[0]);
                    y = args[1].equals("~") ? player.getLocation().getY() : Double.parseDouble(args[1]);
                    z = args[2].equals("~") ? player.getLocation().getZ() : Double.parseDouble(args[2]);
                } catch (Exception e) {
                    message.send("generic", "numberformaterror");
                    return true;
                }
                Location loc = new Location(player.getWorld(), x, y, z);
                TeleportUtil tp = new TeleportUtil(this.plugin);
                tp.doTeleport(player, loc, 0); // Folia兼容
                message.send("teleport", "tocoord", new String[] { x+", "+y+", "+z });
                return true;
            }

            // /tp <player> 仅玩家传送到玩家
            if (args.length == 1 && player != null) {
                if (!permission.has("essence.teleport.player")) {
                    return permission.not();
                }
                List<Player> toList = parseSelector(args[0], commandSender);
                if (toList.isEmpty()) {
                    message.send("generic", "playernotfound");
                    return true;
                }
                Player to = toList.get(0);
                TeleportUtil tp = new TeleportUtil(this.plugin);
                tp.doTeleport(player, to.getLocation(), 0);
                message.send("teleport", "to", new String[] { to.getName() });
                return true;
            }

            message.send("teleport", "usage");
            return true;
        }

        return false;
    }

    /**
     * Checks if the command sender is the console.
     * @param commandSender CommandSender - The command sender.
     * @return boolean - If the command sender is the console.
     */
    public boolean console(CommandSender commandSender) {
        return !(commandSender instanceof Player);
    }

    /**
     * Checks if a player is null.
     * @param player Player - The player to check.
     * @return boolean - If the player is null.
     */
    private boolean isNull(Player player) {
        if (player == null) {
            this.message.send("generic", "exception");
            this.log.severe("Unable to complete teleportation, player is null.");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 解析目标选择器，返回匹配的玩家列表
     */
    private List<Player> parseSelector(String selector, CommandSender sender) {
        if (selector.equalsIgnoreCase("@s")) {
            if (sender instanceof Player) {
                List<Player> list = new ArrayList<>();
                list.add((Player) sender);
                return list;
            }
            return new ArrayList<>();
        } else if (selector.equalsIgnoreCase("@a")) {
            return new ArrayList<>(Bukkit.getOnlinePlayers());
        } else if (selector.equalsIgnoreCase("@p")) {
            if (sender instanceof Player) {
                Player self = (Player) sender;
                Player nearest = null;
                double minDist = Double.MAX_VALUE;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.equals(self)) continue;
                    double dist = p.getLocation().distance(self.getLocation());
                    if (dist < minDist) {
                        minDist = dist;
                        nearest = p;
                    }
                }
                if (nearest != null) {
                    List<Player> list = new ArrayList<>();
                    list.add(nearest);
                    return list;
                }
            }
            return new ArrayList<>();
        } else {
            Player p = plugin.getServer().getPlayer(selector);
            if (p != null) {
                List<Player> list = new ArrayList<>();
                list.add(p);
                return list;
            }
            return new ArrayList<>();
        }
    }
}