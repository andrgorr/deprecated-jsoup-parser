package me.javipepe.authenticator;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by javipepe on 2/04/15.
 */
public class Main extends JavaPlugin implements Listener{

    public void onEnable(){
        getConfig().options().copyDefaults(true);
        saveConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(authenticate(e.getPlayer())) {
            e.getPlayer().sendMessage(ChatColor.GREEN + "You were granted op permissions as you are on " + getConfig().getString("team"));
            if(!e.getPlayer().isOp()){
                e.getPlayer().setOp(true);
            }
        }else{
            if(getConfig().getBoolean("deopothers")){
                if(e.getPlayer().isOp()) {
                    e.getPlayer().setOp(false);
                    e.getPlayer().sendMessage(ChatColor.DARK_RED + "Your op perms were removed because you are not on " + getConfig().getString("team"));
                }
            }
        }
    }

    public boolean authenticate(Player p){
        Document doc;
        try{
            doc = Jsoup.connect("http://oc.tc/teams/" + getConfig().getString("team")).get();

            if(doc.toString().contains(p.getName())){
                return true;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){

            if (cmd.getName().equalsIgnoreCase("setteam")) {
                if(sender.isOp()){


                    if (args.length == 0 || args.length > 1) {
                        sender.sendMessage(ChatColor.RED + "Please specify the team represented as yourteamname here: http://oc.tc/teams/" + ChatColor.DARK_RED + "yourteamname");
                        return true;
                    }
                    if (args.length == 1) {
                        String teamname = args[0];

                        getConfig().set("team", teamname);
                        saveConfig();
                        sender.sendMessage(ChatColor.AQUA + "Your team (" + ChatColor.GRAY + args[0] + ChatColor.AQUA + ") is now the team to authenticate. Make sure this URL works, as otherwise this will not work:" );
                        sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "https://oc.tc/teams/" + args[0]);
                        return true;
                    }
                }
            }
        return true;
    }
}
