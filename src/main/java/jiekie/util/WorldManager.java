package jiekie.util;

import jiekie.MultiWorldPlugin;
import jiekie.exception.*;
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
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WorldManager {
    private final MultiWorldPlugin plugin;
    private final Map<String, WorldData> worldDataMap = new HashMap<>();
    private final Set<String> DEFAULT_WORLD_NAMES = Set.of("world", "world_nether", "world_the_end");

    public WorldManager(MultiWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        worldDataMap.clear();
        FileConfiguration config = plugin.getConfig();

        ConfigurationSection worldSection = config.getConfigurationSection("world");
        if(worldSection == null) return;

        for(String worldName : worldSection.getKeys(false)) {
            String path = "world." + worldName;
            try {
                WorldData worldData = createWorldData(
                        worldName
                        , config.getString(path + ".environment")
                        , config.getString(path + ".type")
                        , config.getBoolean(path + ".generateStructures")
                        , config.contains(path + ".seed") ? config.getLong(path + ".seed") : null
                );

                worldData.setSpawnAnimal(config.getBoolean(path + ".spawnAnimal"));
                worldData.setSpawnMonster(config.getBoolean(path + ".spawnMonster"));
                worldData.setPvp(config.getBoolean(path + ".pvp"));

                worldDataMap.put(worldName, worldData);
                createWorldByWorldData(worldData);

            } catch (WorldCreationException e) {
                e.printStackTrace();
            }
        }
    }

    public void createWorld(String worldName, String environmentName, String typeName, boolean generateStructures, Long seed) throws WorldCreationException {
        WorldData worldData = createWorldData(worldName, environmentName, typeName, generateStructures, seed);
        createWorldByWorldData(worldData);
    }

    public void deleteWorld(String worldName) throws WorldDeletionException {
        if(isDefaultWorldName(worldName))
            throw new WorldDeletionException(ChatUtil.DEFAULT_WORLD_NAME);

        if(!isCreatedWorld(worldName))
            throw new WorldDeletionException(ChatUtil.WORLD_NOT_CREATED);

        World world = Bukkit.getWorld(worldName);
        if(world == null)
            throw new WorldDeletionException(ChatUtil.WORLD_NOT_FOUND);

        if(playersExistInWorld(world))
            throw new WorldDeletionException(ChatUtil.PLAYERS_EXIST);

        Bukkit.unloadWorld(worldName, false);
        WorldData worldData = getWorldDataByWorldName(worldName);
        worldDataMap.remove(worldName);

        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        try {
            deleteDirectory(worldFolder);
        } catch (IOException e) {
            throw new WorldDeletionException(ChatUtil.FAIL_TO_DELETE_WORLD_FOLDER);
        }
    }

    public void setWorldRule(String worldName, String rule, boolean value) throws WorldRuleChangeException {
        if(isCreatedWorldIncludeDefault(worldName))
            throw new WorldRuleChangeException(ChatUtil.WORLD_NOT_CREATED);

        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            throw new WorldRuleChangeException(ChatUtil.WORLD_NOT_FOUND);
        }

        WorldData worldData = getWorldDataByWorldName(worldName);

        if(rule.equals("동물스폰"))
            setAnimalSpawn(world, worldData, value);
        else if(rule.equals("몬스터스폰"))
            setMonsterSpawn(world, worldData, value);
        else if(rule.equals("PVP"))
            setPvp(world, worldData, value);
       else
            throw new WorldRuleChangeException(ChatUtil.INVALID_RULE);

       world.save();
    }

    public void resetWorld(String worldName) throws WorldResetException {
        if(isDefaultWorldName(worldName))
            throw new WorldResetException(ChatUtil.DEFAULT_WORLD_NAME);

        if(!isCreatedWorld(worldName))
            throw new WorldResetException(ChatUtil.WORLD_NOT_CREATED);

        World world = Bukkit.getWorld(worldName);
        if(world == null)
            throw new WorldResetException(ChatUtil.WORLD_NOT_FOUND);

        if(playersExistInWorld(world))
            throw new WorldResetException(ChatUtil.PLAYERS_EXIST);

        Bukkit.unloadWorld(worldName, false);
        WorldData worldData = getWorldDataByWorldName(worldName);

        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        try {
            deleteDirectory(worldFolder);
        } catch (IOException e) {
            throw new WorldResetException(ChatUtil.FAIL_TO_DELETE_WORLD_FOLDER);
        }

        try {
            createWorldByWorldData(worldData);
        } catch (WorldCreationException e) {
            throw new WorldResetException(ChatUtil.FAIL_TO_CREATE_WORLD);
        }
    }

    public Map<String, String> backupWorld(String worldName) throws WorldBackupException {
        if(!isCreatedWorldIncludeDefault(worldName))
            throw new WorldBackupException(ChatUtil.WORLD_NOT_FOUND);

        World world = Bukkit.getWorld(worldName);
        if(world == null)
            throw new WorldBackupException(ChatUtil.WORLD_NOT_FOUND);

        if(playersExistInWorld(world))
            throw new WorldBackupException(ChatUtil.PLAYERS_EXIST);

        Bukkit.unloadWorld(worldName, true);

        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        File backupFolder = new File(Bukkit.getWorldContainer(), "world_backups");
        if(!backupFolder.exists()) backupFolder.mkdirs();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String formattedTime = now.format(formatter);

        Map<String, String> fileInfo = new java.util.HashMap<>();

        File zipFile = new File(backupFolder, worldName + "_" + formattedTime + ".zip");
        try(FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos)) {
            zipFolder(worldFolder, worldName, zos);

            WorldData worldData = getWorldDataByWorldName(worldName);
            createWorldByWorldData(worldData);

            fileInfo.put("path", zipFile.getParent());
            fileInfo.put("name", zipFile.getName());

        } catch (IOException e) {
            throw new WorldBackupException(ChatUtil.FAIL_TO_BACKUP_WORLD_FOLDER);

        } catch (WorldCreationException e) {
            throw new WorldBackupException(ChatUtil.FAIL_TO_CREATE_WORLD);
        }

        return fileInfo;
    }

    public void moveToWorld(String playerName, String worldName) throws TeleportToWorldException {
        if(!isCreatedWorldIncludeDefault(worldName))
            throw new TeleportToWorldException(ChatUtil.WORLD_NOT_CREATED);

        World world = Bukkit.getWorld(worldName);
        if(world == null)
            throw new TeleportToWorldException(ChatUtil.WORLD_NOT_FOUND);

        Player player = Bukkit.getPlayerExact(playerName);
        if(player == null)
            throw new TeleportToWorldException(ChatUtil.PLAYER_NOT_FOUND);

        player.teleport(world.getSpawnLocation());
        SoundUtil.playTeleport(player);
    }

    private WorldData createWorldData(String worldName, String environmentName, String typeName, boolean generateStructures, Long seed) throws WorldCreationException {
        if(isDefaultWorldName(worldName))
            throw new WorldCreationException(ChatUtil.DEFAULT_WORLD_NAME);

        if(isCreatedWorld(worldName))
            throw new WorldCreationException(ChatUtil.WORLD_NAME_ALREADY_EXISTS);

        if(!worldName.matches("[a-zA-Z0-9_\\-]+"))
            throw new WorldCreationException(ChatUtil.INVALID_WORLD_NAME);

        WorldData worldData = new WorldData(worldName, environmentName, typeName, generateStructures, seed);
        return worldData;
    }

    private World createWorldByWorldData(WorldData worldData) throws WorldCreationException {
        WorldCreator worldCreator = new WorldCreator(worldData.getWorldName());

        World.Environment environment = getEnvironment(worldData.getEnvironment());
        if(environment == null)
            throw new WorldCreationException(ChatUtil.INVALID_ENVIRONMENT);

        WorldType type = getType(worldData.getType());
        if(type == null)
            throw new WorldCreationException(ChatUtil.INVALID_TYPE);

        // 월드 생성
        worldCreator.environment(environment);
        worldCreator.type(type);
        worldCreator.generateStructures(worldData.isGenerateStructures());
        if(worldData.getSeed() != null)
            worldCreator.seed(worldData.getSeed());
        World world = worldCreator.createWorld();

        // 규칙 설정
        setAnimalSpawn(world, worldData, worldData.isSpawnAnimal());
        setMonsterSpawn(world, worldData, worldData.isSpawnMonster());
        setPvp(world, worldData, worldData.isPvp());

        return world;
    }

    private boolean isDefaultWorldName(String worldName) {
        return DEFAULT_WORLD_NAMES.contains(worldName);
    }

    private boolean isCreatedWorld(String worldName) {
        return worldDataMap.containsKey(worldName);
    }

    private boolean isCreatedWorldIncludeDefault(String worldName) {
        if(worldDataMap.containsKey(worldName)) return true;
        return DEFAULT_WORLD_NAMES.contains(worldName);
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

    private boolean playersExistInWorld(World world) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getWorld().equals(world)) return true;
        }

        return false;
    }

    private void setAnimalSpawn(World world, WorldData worldData, boolean value) {
        boolean allowMonsters = world.getAllowMonsters();
        world.setSpawnFlags(value, allowMonsters);

        if(worldData != null)
            worldData.setSpawnAnimal(value);

        if(value || allowMonsters)
            world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
        else
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
    }

    private void setMonsterSpawn(World world, WorldData worldData, boolean value) {
        boolean allowAnimals = world.getAllowAnimals();
        world.setSpawnFlags(allowAnimals, value);

        if(worldData != null)
            worldData.setSpawnMonster(value);

        if(value || allowAnimals)
            world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
        else
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
    }

    private void setPvp(World world, WorldData worldData, boolean value) {
        world.setPVP(value);

        if(worldData != null)
            worldData.setPvp(value);
    }

    private WorldData getWorldDataByWorldName(String worldName) {
        return worldDataMap.get(worldName);
    }

    public List<String> getWorldNames() {
        if(worldDataMap.isEmpty()) return Collections.emptyList();
        return new ArrayList<>(worldDataMap.keySet());
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

        for(WorldData worldData : worldDataMap.values()) {
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
