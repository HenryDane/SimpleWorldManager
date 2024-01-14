package com.aglareb.simpleworldmanager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.FileUtils;

/**
 *
 * @author henryjmo
 */
public class WorldCommand implements CommandExecutor {
    private SimpleWorldManager plugin;
    private Map<UUID, String> deleteTable;
    private String consoleDelete;
    
    public WorldCommand(SimpleWorldManager plugin) {
        this.plugin = plugin;
        deleteTable = new HashMap<UUID, String>();
        consoleDelete = null;
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
            if (!handleWorldDelete(sender, command, label, args)) {
                sender.sendMessage(ChatColor.RED + "Useage: /world delete <name>" + ChatColor.RESET);
            }
            return true;
        } else if (args[0].equals("confirm")) {
            if (!handleWorldConfirm(sender, command, label, args)) {
                sender.sendMessage(ChatColor.RED + "Useage: /world confirm <name>" + ChatColor.RESET);
            }
            return true;
        } else if (args[0].equals("cancel")) {
            if (!handleWorldCancel(sender, command, label, args)) {
                sender.sendMessage(ChatColor.RED + "Useage: /world cancel" + ChatColor.RESET);
            }
            return true;
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
        
        sender.sendMessage(ChatColor.RED + "Valid subcommands: create, delete, confirm, cancel, list, tp." + ChatColor.RESET);
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
            sender.sendMessage(ChatColor.RED + "World " + args[1] + " already exists." + ChatColor.RESET);
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
        
        this.plugin.addWorld(args[1]);
        
        sender.sendMessage(ChatColor.GREEN + "Created world: " + args[1] + ChatColor.RESET);
        
        return true;
    }
    
    public boolean handleWorldDelete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) return false;
        
        // world delete <name>
        World world = this.plugin.getServer().getWorld(args[1]);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "World " + args[1] + " does not exist." + ChatColor.RESET);
            return true;
        }
        
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (deleteTable.containsKey(p.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You were already trying to delete something. Previous delete cancelled." + ChatColor.RESET);
                deleteTable.remove(p.getUniqueId());
            } else {
                deleteTable.put(p.getUniqueId(), args[1]);
                sender.sendMessage(ChatColor.GREEN + "Type " + ChatColor.YELLOW + "/world confirm " + args[1] + ChatColor.GREEN + 
                        " to confirm this operation or " + ChatColor.YELLOW + "/world cancel" + ChatColor.GREEN + " to cancel." + ChatColor.RESET);
            }
        } else {
            if (consoleDelete != null) {
                sender.sendMessage(ChatColor.RED + "You were already trying to delete something. Previous delete cancelled." + ChatColor.RESET);
                consoleDelete = null;
            } else {
                consoleDelete = args[1];
                sender.sendMessage(ChatColor.GREEN + "Type " + ChatColor.YELLOW + "/world confirm " + args[1] + ChatColor.GREEN + 
                        " to confirm this operation or " + ChatColor.YELLOW + "/world cancel" + ChatColor.GREEN + " to cancel." + ChatColor.RESET);
            }
        }
        
        return true;
    }
    
    public boolean handleWorldConfirm(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) return false;
        
        // world confirm <name>
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (deleteTable.containsKey(p.getUniqueId())) {
                if (args[1].strip().equals(deleteTable.get(p.getUniqueId()))) {
                    sender.sendMessage(ChatColor.GREEN + "Deleting world " + args[1] + ChatColor.RESET);
                    
                    if (deleteWorldByName(args[1])) {
                        sender.sendMessage(ChatColor.GREEN + "Successfully deleted world " + args[1] + ChatColor.RESET);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Failed to delete world " + args[1] + ChatColor.RESET);
                    }
                    
                    deleteTable.remove(p.getUniqueId());
                } else {
                    sender.sendMessage(ChatColor.RED + args[1] + " does not match world name: " + deleteTable.get(p.getUniqueId()) + 
                            ". Cancelling operation." + ChatColor.RESET);
                    deleteTable.remove(p.getUniqueId());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "No operation available to confirm." + ChatColor.RESET);
            }
        } else {
            if (consoleDelete == null) {
                sender.sendMessage(ChatColor.RED + "No operation available to confirm." + ChatColor.RESET);
            } else {
                if (consoleDelete.equals(args[1].strip())) {
                    sender.sendMessage(ChatColor.GREEN + "Deleting world " + args[1] + ChatColor.RESET);
                    
                    if (deleteWorldByName(args[1])) {
                        sender.sendMessage(ChatColor.GREEN + "Successfully deleted world " + args[1] + ChatColor.RESET);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Failed to delete world " + args[1] + ChatColor.RESET);
                    }
                    
                    consoleDelete = null;
                } else {
                    sender.sendMessage(ChatColor.RED + args[1] + " does not match world name: " + consoleDelete + 
                            ". Cancelling operation." + ChatColor.RESET);
                    consoleDelete = null;
                }
            }
        }
        
        return true;
    }
    
    public boolean handleWorldCancel(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return false;
        
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (deleteTable.containsKey(p.getUniqueId())) {
                deleteTable.remove(p.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "Successfully cancelled operation." + ChatColor.RESET);
            } else {
                sender.sendMessage(ChatColor.RED + "No pending operation." + ChatColor.RESET);
            }
        } else {
            if (consoleDelete != null) {
                consoleDelete = null;
                sender.sendMessage(ChatColor.GREEN + "Successfully cancelled operation." + ChatColor.RESET);
            } else {
                sender.sendMessage(ChatColor.RED + "No pending operation." + ChatColor.RESET);
            }
        }
        
        return true;
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
        
    public boolean deleteWorldByName(String name) {
        File dir = this.plugin.getServer().getWorld(name).getWorldFolder();
        this.plugin.getServer().unloadWorld(name, true);
        
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to delete world directory for world " + name);
            return false;
        }
        
        return true;
    }
}
