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

    public WorldData(String worldName, String environment, String type, boolean generateStructures, Long seed) {
        this.worldName = worldName;
        this.environment = environment;
        this.type = type;
        this.generateStructures = generateStructures;
        this.seed = seed;

        this.spawnAnimal = true;
        this.spawnMonster = true;
        this.pvp = true;
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
}
