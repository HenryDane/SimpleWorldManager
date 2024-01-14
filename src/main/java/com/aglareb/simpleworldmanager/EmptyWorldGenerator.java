package com.aglareb.simpleworldmanager;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

/**
 *
 * @author henryjmo
 */
public class EmptyWorldGenerator extends ChunkGenerator {
    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        if (chunkX == 0 && chunkZ == 0) {
            chunkData.setBlock(0, 64, 0, Material.STONE);
        }
    }
}
