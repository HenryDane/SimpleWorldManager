package com.aglareb.simpleworldmanager;


import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author henryjmo
 */
public class SimpleWorldManager extends JavaPlugin implements Listener {
    private List<String> worlds;
    
    @Override
    public void onEnable() {
        worlds = new ArrayList<String>();
        // TODO: load worlds
        // TODO: add registerWorld() function
        // TODO: save worlds on disable
        
        this.getCommand("world").setExecutor(new WorldCommand(this));
        this.getCommand("wtp").setExecutor(new WorldTeleportCommand());
    }
}
