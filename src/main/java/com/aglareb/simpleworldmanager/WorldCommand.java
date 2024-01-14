package com.aglareb.simpleworldmanager;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author henryjmo
 */
public class WorldCommand implements CommandExecutor {
    private SimpleWorldManager plugin;
    
    public WorldCommand(SimpleWorldManager plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) return false;
        
        if (args[0].equals("create")) {
            if (!handleWorldCreate(sender, command, label, args)) {
                sender.sendMessage(ChatColor.RED + "Useage: /world create <name> [empty|flat]" + ChatColor.RESET);
            }
            return true;
        } else if (args[0].equals("delete")) {
            return handleWorldDelete(sender, command, label, args);
        } else if (args[0].equals("confirm")) {
            return handleWorldConfirm(sender, command, label, args);
        } else if (args[0].equals("list")) {
            if (!handleWorldList(sender, command, label, args)) {
                sender.sendMessage(ChatColor.RED + "Useage: /world list" + ChatColor.RESET);
            }
            return true;
        } else if (args[0].equals("tp")) {
            if (!handleWorldTP(sender, command, label, args)) {
                sender.sendMessage(ChatColor.RED + "Useage: /world tp <player> <world>" + ChatColor.RESET);
                sender.sendMessage(ChatColor.RED + "        /world tp <player> <world> <x> <y> <z>" + ChatColor.RESET);
                sender.sendMessage(ChatColor.RED + "        /world tp <world> <x> <y> <z>" + ChatColor.RESET);
                sender.sendMessage(ChatColor.RED + "        /world tp <world>" + ChatColor.RESET);
            }
            return true;
        } 
        
        sender.sendMessage(ChatColor.RED + "Valid subcommands: create, delete, confirm, list, tp." + ChatColor.RESET);
        return true;        
    }
    
    public boolean handleWorldCreate(CommandSender sender, Command command, String label, String[] args) {
        // /world create <name> [empty|flat]
        boolean isEmpty = true;
        if (args.length == 3) {
            args[2] = args[2].strip();
            if (args[2].equalsIgnoreCase("empty")) {
                isEmpty = true;
            } else if (args[2].equalsIgnoreCase("flat")) {
                isEmpty = false;
            } else {
                return false;
            }
        }
        
        if (sender.getServer().getWorld(args[1]) != null) {
            sender.sendMessage(ChatColor.RED + "World " + args[1] + "already exists." + ChatColor.RESET);
            return true;
        }
        
        sender.sendMessage(ChatColor.GREEN + "Generating world..." + ChatColor.RESET);
        
        WorldCreator wc = new WorldCreator(args[1]);
        if (isEmpty) {
            wc.generator(new EmptyWorldGenerator());
        } else {
            wc.generator(new FlatWorldGenerator());
        }
        wc.biomeProvider(new ConstBiomeProvider(Biome.PLAINS));
        wc.environment(World.Environment.NORMAL);
        wc.generateStructures(false);
        wc.hardcore(false);
        
        sender.getServer().createWorld(wc);
        
        sender.sendMessage(ChatColor.GREEN + "Created world: " + args[1] + ChatColor.RESET);
        
        return true;
    }
    
    public boolean handleWorldDelete(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
    
    public boolean handleWorldConfirm(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
    
    public boolean handleWorldList(CommandSender sender, Command command, String label, String[] args) {
        List<World> worlds = sender.getServer().getWorlds();
        
        String msg = "Worlds: ";
        for (World w : worlds) {
            msg += w.getName() + " ";
        }
        sender.sendMessage(msg);
        
        return true;
    }
    
    public boolean handleWorldTP(CommandSender sender, Command command, String label, String[] args) {
        double x = 0;
        double y = 0;
        double z = 0;
        World w;
        Player p;
        
        if (args.length == 3) {
            // /w tp <player> <world>
            p = sender.getServer().getPlayer(args[1]);
            w = sender.getServer().getWorld(args[2]);
            if (w != null) {
                Location l = w.getSpawnLocation();
                x = l.getX();
                y = l.getY();
                z = l.getZ();
            }
        } else if (args.length == 5) {
            // /w tp name x y z
            if (sender instanceof Player) {
                p = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "You must be a player to do this" + ChatColor.RESET);
                return true;
            }
            w = sender.getServer().getWorld(args[1]);
            try {
                x = Double.parseDouble(args[2]);
                y = Double.parseDouble(args[3]);
                z = Double.parseDouble(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "x, y, and z must be numbers." + ChatColor.RESET);
                return true;
            }
        } else if (args.length == 6) {
            // /w tp <player> <name> x y x
            p = sender.getServer().getPlayer(args[1]);
            w = sender.getServer().getWorld(args[2]);
            try {
                x = Double.parseDouble(args[3]);
                y = Double.parseDouble(args[4]);
                z = Double.parseDouble(args[5]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "x, y, and z must be numbers." + ChatColor.RESET);
                return true;
            }
        } else if (args.length == 2) {
            // /w tp name
            if (sender instanceof Player) {
                p = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "You must be a player to do this" + ChatColor.RESET);
                return true;
            }
            w = sender.getServer().getWorld(args[1]);
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
