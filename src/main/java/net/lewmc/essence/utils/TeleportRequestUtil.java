package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class TeleportRequestUtil {
    private final Essence plugin;

    public TeleportRequestUtil(Essence plugin) {
        this.plugin = plugin;
    }

    public void createRequest(String requester, String requested, boolean teleportToRequester) {
        this.deleteFromRequester(requester);

        if (teleportToRequester) {
            this.plugin.teleportRequests.put(requested, new String[]{requester, "true"});
        } else {
            this.plugin.teleportRequests.put(requested, new String[]{requester, "false"});
        }
    }

    public boolean deleteFromRequester(String requester) {
        boolean found = false;
        if (this.plugin.teleportRequests == null) {
            return false;
        }
        for (Map.Entry<String, String[]> entry : this.plugin.teleportRequests.entrySet()) {
            String[] values = entry.getValue();
            String key = entry.getKey();
            if (values.length > 0 && values[0].equalsIgnoreCase(requester)) {
                this.plugin.teleportRequests.remove(key);
                found = true;
            }
        }
        return found;
    }

    public void deleteFromRequested(String requested) {
        if (this.plugin.teleportRequests == null) {
            return;
        }
        this.plugin.teleportRequests.remove(requested);
    }

    public boolean acceptRequest(String requested) {
        LogUtil log = new LogUtil(this.plugin);
        String[] tpaRequest = this.plugin.teleportRequests.get(requested);
        log.info(this.plugin.teleportRequests.toString());
        if (tpaRequest == null) {
            log.severe("Unable to teleport, request is null. Please re-request and try again.");
            log.severe("If this problem persists try restarting your server.");
            return false;
        }

        TeleportUtil tpu = new TeleportUtil(this.plugin);
        log.info(Arrays.toString(tpaRequest));
        if (Objects.equals(tpaRequest[1], "true")) {
            tpu.doTeleport(
                    this.plugin.getServer().getPlayer(requested),
                    this.plugin.getServer().getPlayer(tpaRequest[0]).getLocation(),
                    0
            );
        } else {
            tpu.doTeleport(
                    this.plugin.getServer().getPlayer(tpaRequest[0]),
                    this.plugin.getServer().getPlayer(requested).getLocation(),
                    0
            );
        }

        this.deleteFromRequested(requested);
        return true;
    }
}
