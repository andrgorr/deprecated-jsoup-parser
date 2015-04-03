package me.javipepe.authenticator;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.ArrayList;

/**
 * Created by javipepe on 2/04/15.
 */
public class Main extends JavaPlugin implements Listener{

    public static ArrayList<String> wlteams = new ArrayList<>();
    public static ArrayList<String> blteams = new ArrayList<>();

    public void onEnable(){
        getConfig().options().copyDefaults(true);
        saveConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public void onDisable(){
        getConfig().set("blacklistedteams", blteams);
        saveConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){

        String permission = getConfig().getString("oplevel");

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

    public static boolean authenticateteam(String p, String team){
        try{
            Document doc = Jsoup.connect("http://oc.tc/teams/" + team).get();

            if(doc.toString().contains(p)){
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

                        if(args[0].equals(getConfig().getString("team"))){
                            sender.sendMessage(ChatColor.RED + "Your team (" + ChatColor.DARK_RED + args[0] + ChatColor.RED + ") is already the one to authenticate");
                            return true;
                        }

                        getConfig().set("team", teamname.toLowerCase());
                        saveConfig();
                        sender.sendMessage(ChatColor.AQUA + "Your team (" + ChatColor.GRAY + args[0] + ChatColor.AQUA + ") is now the team to authenticate. Make sure this URL works, as otherwise this will not work:" );
                        sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "https://oc.tc/teams/" + args[0]);
                        return true;
                    }
                }
            }
            if(cmd.getName().equalsIgnoreCase("getteam")) {
                if(args.length == 0){
                    sender.sendMessage(ChatColor.AQUA + "The team to authenticate is " + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + getConfig().getString("team"));
                    return true;
                }else{
                    sender.sendMessage(ChatColor.RED + "Wrong syntax:");
                    sender.sendMessage(ChatColor.RED + "/getteam");
                    return true;
                }
            }
        if(cmd.getName().equalsIgnoreCase("wlteam")){
            if(sender.isOp()){
                if(args.length == 0){
                    sender.sendMessage(org.bukkit.ChatColor.RED + "Wrong syntax.");
                    sender.sendMessage(org.bukkit.ChatColor.RED + "/wlteam add/remove <team>");
                    return true;

                }else{
                    if(args[0].equalsIgnoreCase("add")) {

                        String team = args[1];
                        if(!wlteams.contains(team)){
                            wlteams.add(team);
                            sender.sendMessage(org.bukkit.ChatColor.GREEN + "" + org.bukkit.ChatColor.BOLD + team + org.bukkit.ChatColor.RESET + org.bukkit.ChatColor.DARK_GREEN + " is now whitelisted.");
                            return true;
                        }else{
                            sender.sendMessage(org.bukkit.ChatColor.RED + team + " is already whitelisted.");
                            return true;
                        }


                    }else if(args[0].equalsIgnoreCase("remove")){
                        String team = args[1];
                        if(wlteams.contains(team)){

                            wlteams.remove(team);
                            for(Player p : Bukkit.getOnlinePlayers()){
                                if(authenticateteam(p.getName(), team)){
                                    p.setWhitelisted(false);
                                }
                            }
                            sender.sendMessage(org.bukkit.ChatColor.RED + "" + org.bukkit.ChatColor.BOLD + team + org.bukkit.ChatColor.RESET + org.bukkit.ChatColor.DARK_RED + " is no longer in the whitelist.");
                            return true;
                        }else{
                            sender.sendMessage(org.bukkit.ChatColor.RED + team + " is not whitelisted!");
                            return true;
                        }
                    }else if(args[0].equalsIgnoreCase("list")){
                        if(wlteams.isEmpty()){
                            sender.sendMessage(org.bukkit.ChatColor.RED + "There are no teams whitelisted.");
                            return true;
                        }
                        sender.sendMessage(org.bukkit.ChatColor.GREEN + "" + org.bukkit.ChatColor.STRIKETHROUGH + "------- " + org.bukkit.ChatColor.RESET + org.bukkit.ChatColor.RED + "Whitelisted Teams (" + wlteams.size() + ")" + org.bukkit.ChatColor.GREEN + "" + org.bukkit.ChatColor.STRIKETHROUGH + " -------");
                        for(String team: wlteams){
                            sender.sendMessage(org.bukkit.ChatColor.WHITE + " ○ " + org.bukkit.ChatColor.DARK_AQUA + team);

                        }
                        return true;
                    }
                }
            }else{
                sender.sendMessage(org.bukkit.ChatColor.RED + "You don't have permission to execute this command.");
                return true;
            }
        }
        if(cmd.getName().equalsIgnoreCase("blacklist")){
            if(sender.isOp()){
                if(args.length == 0){
                    sender.sendMessage(org.bukkit.ChatColor.RED + "Wrong syntax.");
                    sender.sendMessage(org.bukkit.ChatColor.RED + "/blacklist add/remove <team>");
                    return true;

                }else{
                    if(args[0].equalsIgnoreCase("add")) {

                        String team = args[1];
                        if(!blteams.contains(team)){
                            if(team.equalsIgnoreCase(getConfig().getString("team"))){
                                sender.sendMessage(ChatColor.RED + "You can't blacklist this server's team (" + team + ").");
                                return true;
                            }
                            for(Player p : Bukkit.getOnlinePlayers()){
                                if(authenticateteam(p.getName(), team)){

                                    p.kickPlayer(ChatColor.GRAY + "" + "You have been blacklisted from this server.");
                                }
                            }
                            blteams.add(team);
                            sender.sendMessage(org.bukkit.ChatColor.DARK_GRAY + "" + org.bukkit.ChatColor.BOLD + team + org.bukkit.ChatColor.RESET + org.bukkit.ChatColor.GRAY + " is now blacklisted.");
                            return true;
                        }else{
                            sender.sendMessage(org.bukkit.ChatColor.RED + team + " is already blacklisted.");
                            return true;
                        }


                    }else if(args[0].equalsIgnoreCase("remove")){
                        String team = args[1];
                        if(blteams.contains(team)){

                            blteams.remove(team);

                            sender.sendMessage(org.bukkit.ChatColor.RED + "" + org.bukkit.ChatColor.BOLD + team + org.bukkit.ChatColor.RESET + org.bukkit.ChatColor.DARK_RED + " is no longer blacklisted");
                            return true;
                        }else{
                            sender.sendMessage(org.bukkit.ChatColor.RED + team + " is not blacklisted!");
                            return true;
                        }
                    }else if(args[0].equalsIgnoreCase("list")){
                        if(blteams.isEmpty()){
                            sender.sendMessage(org.bukkit.ChatColor.RED + "There are no teams blacklisted.");
                            return true;
                        }
                        sender.sendMessage(ChatColor.GRAY + "" + org.bukkit.ChatColor.STRIKETHROUGH + "------- " + org.bukkit.ChatColor.RESET + org.bukkit.ChatColor.RED + "Blacklisted Teams (" + blteams.size() + ")" + ChatColor.GRAY + "" + org.bukkit.ChatColor.STRIKETHROUGH + " -------");
                        for(String team: blteams){
                            sender.sendMessage(org.bukkit.ChatColor.WHITE + " ○ " + org.bukkit.ChatColor.DARK_AQUA + team);

                        }

                        return true;
                    }
                }
            }else{
                sender.sendMessage(org.bukkit.ChatColor.RED + "You don't have permission to execute this command.");
                return true;
            }
        }

        return true;
    }


}
