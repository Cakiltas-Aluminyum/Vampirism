package de.teamlapen.vampirism.world.gen.structures.huntercamp;

import com.mojang.serialization.Codec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HunterCampFeature extends StructureFeature<NoneFeatureConfiguration> {

    public HunterCampFeature(Codec<NoneFeatureConfiguration> deserializer) {
        super(deserializer, PieceGeneratorSupplier.simple(HunterCampFeature::checkLocation, HunterCampFeature::generatePieces));
    }

    private static <C extends FeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.Context<C> cContext) { //TODO 1.18 check
        if (!cContext.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
            return false;
        } else {
            return cContext.getLowestY(12, 15) >= cContext.chunkGenerator().getSeaLevel();
        }
    }

    private static <C extends FeatureConfiguration> void generatePieces(StructurePiecesBuilder structurePiecesBuilder, PieceGenerator.Context<C> cContext) {
        HunterCampPieces.addStartPieces(structurePiecesBuilder, cContext);

    }
}