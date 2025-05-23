package jiekie.multiworld.model;

public class WorldData {
    /* 필수 정보 */
    private final String worldName;
    private final String environment;
    private final String type;
    private final boolean generateStructures;
    
    /* 선택 정보 */
    private final Long seed;
    
    /* 게임 규칙 */
    private boolean pvp;
    private boolean dayLightCycle;
    private boolean weatherCycle;
    private boolean spawnPhantom;
    private boolean keepInventory;
    private boolean fireTick;
    private boolean mobGriefing;
    private boolean mobSpawning;

    public WorldData(String worldName, String environment, String type, boolean generateStructures, Long seed) {
        this.worldName = worldName;
        this.environment = environment;
        this.type = type;
        this.generateStructures = generateStructures;
        this.seed = seed;

        this.pvp = true;
        this.dayLightCycle = true;
        this.weatherCycle = true;
        this.spawnPhantom = true;
        this.keepInventory = false;
        this.fireTick = true;
        this.mobGriefing = true;
        this.mobSpawning = true;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getType() {
        return type;
    }

    public boolean isGenerateStructures() {
        return generateStructures;
    }

    public Long getSeed() {
        return seed;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isDayLightCycle() {
        return dayLightCycle;
    }

    public void setDayLightCycle(boolean dayLightCycle) {
        this.dayLightCycle = dayLightCycle;
    }

    public boolean isWeatherCycle() {
        return weatherCycle;
    }

    public void setWeatherCycle(boolean weatherCycle) {
        this.weatherCycle = weatherCycle;
    }

    public boolean isSpawnPhantom() {
        return spawnPhantom;
    }

    public void setSpawnPhantom(boolean spawnPhantom) {
        this.spawnPhantom = spawnPhantom;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public boolean isFireTick() {
        return fireTick;
    }

    public void setFireTick(boolean fireTick) {
        this.fireTick = fireTick;
    }

    public boolean isMobGriefing() {
        return mobGriefing;
    }

    public void setMobGriefing(boolean mobGriefing) {
        this.mobGriefing = mobGriefing;
    }

    public boolean isMobSpawning() {
        return mobSpawning;
    }

    public void setMobSpawning(boolean mobSpawning) {
        this.mobSpawning = mobSpawning;
    }
}
