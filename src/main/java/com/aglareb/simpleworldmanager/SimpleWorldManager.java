package com.aglareb.simpleworldmanager;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author henryjmo
 */
public class SimpleWorldManager extends JavaPlugin implements Listener {
    private List<String> worlds;
    private boolean hasLoadedWorlds;
    
    @Override
    public void onEnable() {
        hasLoadedWorlds = false;
        worlds = new ArrayList<String>();
        
        this.getCommand("world").setExecutor(new WorldCommand(this));
        this.getCommand("wtp").setExecutor(new WorldTeleportCommand());
        
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    
    @Override
    public void onDisable() {
        File worldFile = new File(this.getDataFolder(), "worlds.yml");
        YamlConfiguration worldyaml = new YamlConfiguration();
        worldyaml.set("worlds", worlds);
        try {
            worldyaml.save(worldFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.WARNING, "Failed writing world file (IOException).");
        }
    }
    
    @EventHandler
    public void onServerLoad(ServerLoadEvent ev) {
        if (hasLoadedWorlds) return;
        hasLoadedWorlds = true;
        
        worlds.clear();
        
        // read worlds from file
        File worldFile = new File(this.getDataFolder(), "worlds.yml");
        YamlConfiguration worldyaml = new YamlConfiguration();
        if (worldFile.exists()) {
            try {
                worldyaml.load(worldFile);
            } catch (FileNotFoundException ex) {
                this.getLogger().log(Level.WARNING, "Failed reading world file (FileNotFoundException).");
            } catch (IOException ex) {
                this.getLogger().log(Level.WARNING, "Failed reading world file (IOException).");
            } catch (InvalidConfigurationException ex) {
                this.getLogger().log(Level.WARNING, "Failed reading world file (InvalidConfigurationException).");
            }
        } else {
            try {
                worldyaml.save(worldFile);
            } catch (IOException ex) {
                this.getLogger().log(Level.WARNING, "Failed writing world file (IOException).");
            }
        }
        worlds = worldyaml.getStringList("worlds");
        
        // load worlds
        this.getLogger().log(Level.INFO, "Loading " + worlds.size() + " worlds.");
        for (String name : worlds) {
            this.getServer().createWorld(new WorldCreator(name));
        }
        this.getLogger().log(Level.INFO, "Done loading worlds.");
    }
    
    public void addWorld(String name) {
        worlds.add(name);
    }
}
