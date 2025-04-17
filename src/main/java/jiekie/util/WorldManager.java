package jiekie.util;

import jiekie.MultiWorldPlugin;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WorldManager {
    private final MultiWorldPlugin plugin;
    private final List<WorldData> worldDataList = new ArrayList<>();
    private final String[] DEFAULT_WORLD_NAMES = {"world", "world_nether", "world_the_end"};

    public WorldManager(MultiWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        worldDataList.clear();
        FileConfiguration config = plugin.getConfig();

        ConfigurationSection worldSection = config.getConfigurationSection("world");
        if(worldSection == null) return;

        for(String worldName : worldSection.getKeys(false)) {
            String path = "world." + worldName;

            // 기본 정보 설정
            String environment = config.getString(path + ".environment");
            String type = config.getString(path + ".type");
            boolean generateStructures = config.getBoolean(path + ".generateStructures");
            Long seed = config.contains(path + ".seed") ? config.getLong(path + ".seed") : null;

            WorldData worldData = new WorldData(worldName, environment, type, generateStructures, seed);

            // 규칙 설정
            worldData.setSpawnAnimal(config.getBoolean(path + ".spawnAnimal"));
            worldData.setSpawnMonster(config.getBoolean(path + ".spawnMonster"));
            worldData.setPvp(config.getBoolean(path + ".pvp"));

            worldDataList.add(worldData);
            createWorldByWorldName(worldData);
        }
    }

    public void createWorld(Player player, String worldName, String environmentName, String typeName, boolean generateStructures, Long seed) {
        if(isDefaultWorldName(worldName)) {
            ChatUtil.isDefaultWorldName(player);
            return;
        }

        if(worldExists(worldName)) {
            ChatUtil.worldExists(player);
            return;
        }

        WorldData worldData = new WorldData(worldName, environmentName, typeName, generateStructures, seed);
        worldDataList.add(worldData);

        if(!createWorldByWorldName(worldData)) {
            ChatUtil.couldNotCreateWorld(player);
            return;
        }

        ChatUtil.createWorld(player);
        SoundUtil.playNoteBlockBell(player);
    }

    public void removeWorld(Player player, String worldName) {
        if(isDefaultWorldName(worldName)) {
            ChatUtil.isDefaultWorldName(player);
            return;
        }

        if(!worldExists(worldName)) {
            ChatUtil.worldDoesNotExist(player);
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            ChatUtil.worldDoesNotExist(player);
            return;
        }

        Bukkit.unloadWorld(worldName, false);
        WorldData worldData = getWorldDataByWorldName(worldName);
        worldDataList.remove(worldData);

        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        try {
            deleteDirectory(worldFolder);
            ChatUtil.removeWorld(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (IOException e) {
            ChatUtil.failToDeleteFolder(player);
            e.printStackTrace();
        }
    }

    public void setWorldRule(Player player, String worldName, String rule, boolean value) {
        if(isDefaultWorldName(worldName)) {
            ChatUtil.isDefaultWorldName(player);
            return;
        }

        if(!worldExists(worldName)) {
            ChatUtil.worldDoesNotExist(player);
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            ChatUtil.worldDoesNotExist(player);
            return;
        }

        WorldData worldData = getWorldDataByWorldName(worldName);
        if(rule.equals("동물스폰")) {
            setAnimalAndMonsterSpawn(world, worldData, value, worldData.isSpawnMonster());

        } else if(rule.equals("몬스터스폰")) {
            setAnimalAndMonsterSpawn(world, worldData, worldData.isSpawnAnimal(), value);

        } else if(rule.equals("PVP")) {
            setPvp(world, worldData, value);

        } else {
            ChatUtil.ruleDoesNotExist(player);
            return;
        }

        ChatUtil.setWorldRule(player);
        SoundUtil.playNoteBlockBell(player);
    }

    public void backupWorld(Player player, String worldName) {
        if(isDefaultWorldName(worldName)) {
            ChatUtil.isDefaultWorldName(player);
            return;
        }

        if(!worldExists(worldName)) {
            ChatUtil.worldDoesNotExist(player);
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            ChatUtil.worldDoesNotExist(player);
            return;
        }

        Bukkit.unloadWorld(worldName, true);

        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        File backupFolder = new File(Bukkit.getWorldContainer(), "world_backups");
        if(!backupFolder.exists()) backupFolder.mkdirs();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String formattedTime = now.format(formatter);

        File zipFile = new File(backupFolder, worldName + "_" + formattedTime + ".zip");
        try(FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos)) {
            zipFolder(worldFolder, worldName, zos);

            WorldData worldData = getWorldDataByWorldName(worldName);
            createWorldByWorldName(worldData);

            ChatUtil.backupWorld(player, zipFile.getParent(), zipFile.getName());
            SoundUtil.playNoteBlockBell(player);

        } catch (IOException e) {
            e.printStackTrace();
            ChatUtil.failToBackupFolder(player);
        }
    }

    public void moveToWorld(Player player, String worldName) {
        if(!worldExistsIncludeDefaultWorld(worldName)) {
            ChatUtil.worldDoesNotExist(player);
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            ChatUtil.worldDoesNotExist(player);
            return;
        }

        player.teleport(world.getSpawnLocation());
        SoundUtil.playTeleport(player);
    }

    public void movePlayerToWorld(Player player, String worldName, String targetPlayerName) {
        if(!worldExistsIncludeDefaultWorld(worldName)) {
            ChatUtil.worldDoesNotExist(player);
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            ChatUtil.worldDoesNotExist(player);
            return;
        }

        Player targetPlayer = Bukkit.getPlayerExact(targetPlayerName);
        if(targetPlayer == null) {
            ChatUtil.playerDoesNotExist(player);
            return;
        }

        targetPlayer.teleport(world.getSpawnLocation());
        SoundUtil.playTeleport(targetPlayer);

        ChatUtil.movePlayerToWorld(player);
        SoundUtil.playNoteBlockBell(player);
    }

    private boolean createWorldByWorldName(WorldData worldData) {
        WorldCreator worldCreator = new WorldCreator(worldData.getWorldName());

        World.Environment environment = getEnvironment(worldData.getEnvironment());
        if(environment == null) {
            return false;
        }

        WorldType type = getType(worldData.getType());
        if(type == null) {
            return false;
        }

        worldCreator.environment(environment);
        worldCreator.type(type);
        worldCreator.generateStructures(worldData.isGenerateStructures());
        if(worldData.getSeed() != null)
            worldCreator.seed(worldData.getSeed());

        World world = worldCreator.createWorld();

        setAnimalAndMonsterSpawn(world, worldData, worldData.isSpawnAnimal(), worldData.isSpawnMonster());
        setPvp(world, worldData, worldData.isPvp());

        return true;
    }

    private void setAnimalAndMonsterSpawn(World world, WorldData worldData, boolean spawnAnimal, boolean spawnMonster) {
        world.setSpawnFlags(spawnAnimal, spawnMonster);
        worldData.setSpawnAnimal(spawnAnimal);
        worldData.setSpawnMonster(spawnMonster);

        if(spawnAnimal || spawnMonster)
            world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
        else
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
    }

    private void setPvp(World world, WorldData worldData, boolean value) {
        world.setPVP(value);
        worldData.setPvp(value);
    }

    private boolean isDefaultWorldName(String worldName) {
        for(String defaultWorldName : DEFAULT_WORLD_NAMES) {
            if(defaultWorldName.equals(worldName)) return true;
        }

        return false;
    }

    private boolean worldExists(String worldName) {
        for(WorldData worldData : worldDataList) {
            if(worldData.getWorldName().equals(worldName)) return true;
        }

        return false;
    }

    private boolean worldExistsIncludeDefaultWorld(String worldName) {
        for(WorldData worldData : worldDataList) {
            if(worldData.getWorldName().equals(worldName)) return true;
        }

        for(String defaultWorldName : DEFAULT_WORLD_NAMES) {
            if(defaultWorldName.equals(worldName)) return true;
        }

        return false;
    }

    private World.Environment getEnvironment(String environment) {
        if(environment.equalsIgnoreCase("normal"))
            return World.Environment.NORMAL;
        else if(environment.equalsIgnoreCase("nether"))
            return World.Environment.NETHER;
        else if(environment.equalsIgnoreCase("the_end"))
            return World.Environment.THE_END;

        return null;
    }

    private WorldType getType(String type) {
        if(type.equalsIgnoreCase("normal"))
            return WorldType.NORMAL;
        else if(type.equalsIgnoreCase("flat"))
            return WorldType.FLAT;

        return null;
    }

    private WorldData getWorldDataByWorldName(String worldName) {
        for(WorldData worldData : worldDataList)
            if(worldData.getWorldName().equals(worldName)) return worldData;

        return null;
    }

    public List<String> getWorldNames() {
        List<String> worldNameList = new ArrayList<>();
        for(WorldData worldData : worldDataList)
            worldNameList.add(worldData.getWorldName());

        return worldNameList;
    }

    private void deleteDirectory(File directory) throws IOException {
        if(!directory.exists()) throw new IOException();

        File[] files = directory.listFiles();
        if(files != null) {
            for(File file : files) {
                if(file.isDirectory())
                    deleteDirectory(file);
                else
                    file.delete();
            }
        }

        directory.delete();
    }

    private void zipFolder(File folder, String basePath, ZipOutputStream zos) throws IOException {
        if(!folder.exists()) throw new IOException();

        for(File file : folder.listFiles()) {
            String entryName = basePath + "/" + file.getName();

            if(file.isDirectory()) {
                zipFolder(file, entryName, zos);
            } else {
                zos.putNextEntry(new ZipEntry(entryName));
                try(FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) >= 0)
                        zos.write(buffer, 0, length);
                }

                zos.closeEntry();
            }
        }
    }

    public void save() {
        FileConfiguration config = plugin.getConfig();
        config.set("world", null);

        for(WorldData worldData : worldDataList) {
            String worldName = worldData.getWorldName();
            String path = "world." + worldName;

            config.set(path + ".environment", worldData.getEnvironment());
            config.set(path + ".type", worldData.getType());
            config.set(path + ".generateStructures", worldData.isGenerateStructures());
            config.set(path + ".seed", worldData.getSeed());
            config.set(path + ".spawnAnimal", worldData.isSpawnAnimal());
            config.set(path + ".spawnMonster", worldData.isSpawnMonster());
            config.set(path + ".pvp", worldData.isPvp());

            World world = Bukkit.getWorld(worldName);
            world.save();
        }

        plugin.saveConfig();
    }
}
