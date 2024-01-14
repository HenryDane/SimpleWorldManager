package com.aglareb.simpleworldmanager;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

/**
 *
 * @author henryjmo
 */
public class ConstBiomeProvider extends BiomeProvider {
    private Biome biome;
    
    public ConstBiomeProvider(Biome biome) {
        this.biome = biome;
    }
    
    @Override
    public Biome getBiome(WorldInfo wi, int i, int i1, int i2) {
        return this.biome;
    }

    @Override
    public List<Biome> getBiomes(WorldInfo wi) {
        ArrayList<Biome> biomes = new ArrayList<>();
        biomes.add(this.biome);
        return biomes;
    }
    
}
