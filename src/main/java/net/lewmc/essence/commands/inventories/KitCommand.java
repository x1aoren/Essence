package net.lewmc.essence.commands.inventories;

import net.lewmc.essence.Essence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class KitCommand implements CommandExecutor {

    private final Essence plugin;

    /**
     * Constructor for the KitCommand class.
     * @param plugin References to the main plugin class.
     */
    public KitCommand (Essence plugin) {
        this.plugin = plugin;
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
            @NotNull String[] args)
    {
        return false;
    }
}
