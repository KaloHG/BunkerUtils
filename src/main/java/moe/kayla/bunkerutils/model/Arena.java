package moe.kayla.bunkerutils.model;

public class Arena {
    private String world;
    private String host;
    private Bunker bunker;

    public Arena(String world, String host, Bunker bunker) {
        this.world = world;
        this.host = host;
        this.bunker = bunker;
    }

    public String getWorld() {
        return world;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Bunker getBunker() {
        return bunker;
    }
}
