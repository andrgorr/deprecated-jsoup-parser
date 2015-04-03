package me.javipepe.authenticator.listeners;

import me.javipepe.authenticator.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AsyncPreLoginListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        String playername = e.getName();
        //whitelist
        for (String team : Main.wlteams) {
            if (Main.authenticateteam(playername, team)) {
                Bukkit.getPlayer(playername).setWhitelisted(true);
                //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:whitelist add " + playername);
                e.allow();

            }
        }

        //blacklist
        for (String team : Main.blteams) {
            if (Main.authenticateteam(playername, team)) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.GRAY + "" + ChatColor.BOLD + "Your team (" + team + ") is blacklisted from the server.");


            }
        }
    }

}
