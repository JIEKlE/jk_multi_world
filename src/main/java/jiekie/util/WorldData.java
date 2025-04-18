package jiekie.util;

public class WorldData {
    /* 필수 정보 */
    private String worldName;
    private String environment;
    private String type;
    private boolean generateStructures;
    
    /* 선택 정보 */
    private Long seed;
    
    /* 게임 규칙 */
    private boolean spawnAnimal;
    private boolean spawnMonster;
    private boolean pvp;
    private boolean dayLightCycle;
    private boolean weatherCycle;
    private boolean spawnVillagers;
    private boolean spawnPhantom;
    private boolean keepInventory;
    private boolean fireTick;
    private boolean mobGriefing;

    public WorldData(String worldName, String environment, String type, boolean generateStructures, Long seed) {
        this.worldName = worldName;
        this.environment = environment;
        this.type = type;
        this.generateStructures = generateStructures;
        this.seed = seed;

        this.spawnAnimal = true;
        this.spawnMonster = true;
        this.pvp = true;
        this.dayLightCycle = true;
        this.weatherCycle = true;
        this.spawnVillagers = true;
        this.spawnPhantom = true;
        this.keepInventory = false;
        this.fireTick = true;
        this.mobGriefing = true;
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

    public boolean isSpawnAnimal() {
        return spawnAnimal;
    }

    public void setSpawnAnimal(boolean spawnAnimal) {
        this.spawnAnimal = spawnAnimal;
    }

    public boolean isSpawnMonster() {
        return spawnMonster;
    }

    public void setSpawnMonster(boolean spawnMonster) {
        this.spawnMonster = spawnMonster;
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

    public boolean isSpawnVillagers() {
        return spawnVillagers;
    }

    public void setSpawnVillagers(boolean spawnVillagers) {
        this.spawnVillagers = spawnVillagers;
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
}
