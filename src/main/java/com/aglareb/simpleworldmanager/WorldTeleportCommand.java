package com.aglareb.simpleworldmanager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author henryjmo
 */
public class WorldTeleportCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!handleWorldTP(sender, command, label, args)) {
            sender.sendMessage(ChatColor.RED + "Useage: /world tp <player> <world>" + ChatColor.RESET);
            sender.sendMessage(ChatColor.RED + "        /world tp <player> <world> <x> <y> <z>" + ChatColor.RESET);
            sender.sendMessage(ChatColor.RED + "        /world tp <world> <x> <y> <z>" + ChatColor.RESET);
            sender.sendMessage(ChatColor.RED + "        /world tp <world>" + ChatColor.RESET);
        }   
        
        return true;
    }
    
    public boolean handleWorldTP(CommandSender sender, Command command, String label, String[] args) {
        double x = 0;
        double y = 0;
        double z = 0;
        World w;
        Player p;
        
        if (args.length == 2) {
            // /wtp <player> <world>
            p = sender.getServer().getPlayer(args[0]);
            w = sender.getServer().getWorld(args[1]);
            if (w != null) {
                Location l = w.getSpawnLocation();
                x = l.getX();
                y = l.getY();
                z = l.getZ();
            }
        } else if (args.length == 4) {
            // /wtp name x y z
            if (sender instanceof Player) {
                p = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "You must be a player to do this" + ChatColor.RESET);
                return true;
            }
            w = sender.getServer().getWorld(args[0]);
            try {
                x = Double.parseDouble(args[1]);
                y = Double.parseDouble(args[2]);
                z = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "x, y, and z must be numbers." + ChatColor.RESET);
                return true;
            }
        } else if (args.length == 5) {
            // /wtp <player> <name> x y x
            p = sender.getServer().getPlayer(args[0]);
            w = sender.getServer().getWorld(args[1]);
            try {
                x = Double.parseDouble(args[2]);
                y = Double.parseDouble(args[3]);
                z = Double.parseDouble(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "x, y, and z must be numbers." + ChatColor.RESET);
                return true;
            }
        } else if (args.length == 1) {
            // /wtp name
            if (sender instanceof Player) {
                p = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "You must be a player to do this" + ChatColor.RESET);
                return true;
            }
            w = sender.getServer().getWorld(args[0]);
            if (w != null) {
                Location l = w.getSpawnLocation();
                x = l.getX();
                y = l.getY();
                z = l.getZ();
            }
        } else {
            return false;
        }
        
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "No such player." + ChatColor.RESET);
            return true;
        }
        
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "No such world." + ChatColor.RESET);
            return true;
        }
        
        Location l = new Location(w, x, y, z);
        p.teleport(l);
        
        sender.sendMessage(ChatColor.GREEN + "Teleported " + p.getName() + " to world " + w.getName() + ChatColor.RESET);
        
        return true;
    }
}
