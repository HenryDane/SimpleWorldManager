package com.aglareb.simpleworldmanager;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

/**
 *
 * @author henryjmo
 */
public class FlatWorldGenerator extends ChunkGenerator {
    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkGenerator.ChunkData chunkData) {
        final int chunkExtents = 4;
        if (Math.abs(chunkX) > chunkExtents || Math.abs(chunkZ) > chunkExtents) return;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                boolean odd = false;
                odd = (x % 2) == (z % 2);
                if (odd) {
                    chunkData.setBlock(x, 64, z, Material.BLACK_CONCRETE);
                } else {
                    chunkData.setBlock(x, 64, z, Material.WHITE_CONCRETE);
                }
                for (int y = worldInfo.getMinHeight(); y < 63; y++) {
                    chunkData.setBlock(x, y, z, Material.STONE);
                }
            }
        }
    }
}
