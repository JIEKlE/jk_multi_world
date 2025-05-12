package jiekie.multiworld.api;

import jiekie.multiworld.exception.WorldResetException;
import jiekie.multiworld.manager.WorldManager;

public class MultiWorldAPI {
    private static MultiWorldAPI instance;
    private final WorldManager worldManager;

    private MultiWorldAPI(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public static void initialize(WorldManager worldManager) {
        if(instance == null)
            instance = new MultiWorldAPI(worldManager);
    }

    public static MultiWorldAPI getInstance() {
        return instance;
    }

    public void resetWorld(String worldName) throws WorldResetException {
        worldManager.resetWorld(worldName);
    }
}
