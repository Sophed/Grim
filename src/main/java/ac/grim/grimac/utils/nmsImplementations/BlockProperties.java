package ac.grim.grimac.utils.nmsImplementations;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.collisions.Materials;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;

public class BlockProperties {
    private final static Material ice = XMaterial.ICE.parseMaterial();
    private final static Material slime = XMaterial.SLIME_BLOCK.parseMaterial();
    private final static Material packedIce = XMaterial.PACKED_ICE.parseMaterial();
    private final static Material frostedIce = XMaterial.FROSTED_ICE.parseMaterial();
    private final static Material blueIce = XMaterial.BLUE_ICE.parseMaterial();

    private final static Material soulSand = XMaterial.SOUL_SAND.parseMaterial();
    private final static Material honeyBlock = XMaterial.HONEY_BLOCK.parseMaterial();

    // WATER and STATIONARY_WATER on 1.12
    // WATER and BUBBLE_COLUMN on 1.13
    private final static Material water;
    private final static Material alsoWater;

    static {
        if (XMaterial.isNewVersion()) {
            water = Material.WATER;
            alsoWater = Material.BUBBLE_COLUMN;
        } else {
            water = Material.WATER;
            alsoWater = Material.LEGACY_STATIONARY_WATER;
        }
    }

    public static float getBlockFrictionUnderPlayer(GrimPlayer player) {
        if (player.bukkitPlayer.isGliding() || player.specialFlying) return 1.0f;

        Material material = player.compensatedWorld.getBukkitBlockDataAt(player.lastX, player.lastY - 0.5000001, player.lastZ).getMaterial();

        return getMaterialFriction(player, material);
    }

    public static float getMaterialFriction(GrimPlayer player, Material material) {
        float friction = 0.6f;

        if (material == ice) friction = 0.98f;
        if (material == slime) friction = 0.8f;
        if (material == packedIce) friction = 0.98f;
        if (material == frostedIce) friction = 0.98f;
        if (material == blueIce) {
            friction = 0.98f;
            if (player.clientVersion >= 13) friction = 0.989f;
        }

        return friction;
    }

    public static float getFrictionInfluencedSpeed(float f, GrimPlayer player) {
        //Player bukkitPlayer = player.bukkitPlayer;

        // Use base value because otherwise it isn't async safe.
        // Well, more async safe, still isn't 100% safe.
        if (player.lastOnGround) {
            return (float) (player.movementSpeed * (0.21600002f / (f * f * f)));
        }

        if (player.specialFlying) {
            return player.flySpeed * 20 * (player.isSprinting ? 0.1f : 0.05f);

        } else {
            if (player.isSprinting) {
                return 0.026f;
            } else {
                return 0.02f;
            }
        }
    }

    // Entity line 617
    public static BlockData getOnBlock(GrimPlayer player, Location getBlockLocation) {
        BlockData block1 = player.compensatedWorld.getBukkitBlockDataAt(getBlockLocation.getBlockX(), (int) Math.floor(getBlockLocation.getY() - 0.2F), getBlockLocation.getBlockZ());

        if (Materials.checkFlag(block1.getMaterial(), Materials.AIR)) {
            BlockData block2 = player.compensatedWorld.getBukkitBlockDataAt(getBlockLocation.getBlockX(), (int) Math.floor(getBlockLocation.getY() - 1.2F), getBlockLocation.getBlockZ());

            if (Materials.checkFlag(block2.getMaterial(), Materials.FENCE) || Materials.checkFlag(block2.getMaterial(), Materials.WALL) || Materials.checkFlag(block2.getMaterial(), Materials.GATE)) {
                return block2;
            }
        }

        return block1;
    }

    // Entity line 637
    public static float getBlockSpeedFactor(GrimPlayer player) {
        if (player.bukkitPlayer.isGliding() || player.specialFlying) return 1.0f;

        Material block = player.compensatedWorld.getBukkitBlockDataAt(player.x, player.y, player.z).getMaterial();

        if (block == soulSand) {
            // Soul speed is a 1.16+ enchantment
            if (player.bukkitPlayer.getInventory().getBoots() != null && XMaterial.getVersion() > 15 && player.bukkitPlayer.getInventory().getBoots().getEnchantmentLevel(Enchantment.SOUL_SPEED) > 0)
                return 1.0f;
            return 0.4f;
        }

        float f = 1.0f;

        if (block == honeyBlock) f = 0.4F;

        if (block == water || block == alsoWater) {
            return f;
        }

        if (f == 1.0) {
            Material block2 = player.compensatedWorld.getBukkitBlockDataAt(player.x, player.y - 0.5000001, player.z).getMaterial();
            if (block2 == honeyBlock) return 0.4F;
            if (block2 == soulSand) return 0.4F;
            return 1.0f;
        }

        return f;
    }
}
