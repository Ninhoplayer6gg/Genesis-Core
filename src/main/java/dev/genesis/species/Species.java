package dev.genesis.species;
import java.util.List;
public record Species(String id, String name, String speciesName, BodyType bodyType,
                      float width, float height, float eyeHeight, int level, String scan,
                      boolean playable, List<String> abilities) {
    public String powerId(boolean absolute) { return "genesis:" + id + (absolute ? "_absolute" : ""); }
}
