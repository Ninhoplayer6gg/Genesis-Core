package dev.genesis.species;
import java.util.*;
public final class SpeciesRegistry {
    private static final Map<String, Species> SPECIES = new LinkedHashMap<>();
    static {
        add(new Species("adaptaris","Adaptaris","Theryx",BodyType.HUMANOID_HEAVY,.7f,2.05f,1.78f,1,"",true,
            List.of("Golpe de pressão","Carapaça viva","Reparação","Resposta em cadeia","Salto tectônico")));
        add(new Species("ferronox","Ferronox","Oryndal",BodyType.HUMANOID,.65f,1.95f,1.7f,2,"metal",true,
            List.of("Estilhaço orbital","Polarizar","Coroa de ferro","Jaula de fluxo","Sustentação magnética")));
        add(new Species("colonyx","Colonyx","Miríade vesperal",BodyType.EXOTIC,1.05f,1.25f,1.05f,3,"colony",true,
            List.of("Lança colonial","Membrana escudo","Reagrupar","Floração de membros","Escalada colonial")));
        add(new Species("veyl","Veyl","Elythari",BodyType.FLOATING,.7f,2f,1.7f,5,"",false,List.of()));
        add(new Species("severax","Severax","Kherass",BodyType.MONSTROUS,.8f,2.1f,1.8f,5,"",false,List.of()));
        add(new Species("atmos","Atmos","Nimbri",BodyType.FLOATING,.8f,2f,1.7f,5,"",false,List.of()));
        add(new Species("reformis","Reformis","Tessari",BodyType.HUMANOID,.7f,2f,1.7f,5,"",false,List.of()));
        add(new Species("solurion","Solurion","Aureontes",BodyType.HUMANOID_HEAVY,1f,2.6f,2.2f,5,"",false,List.of()));
        add(new Species("cryon","Cryon","Isotheri",BodyType.HUMANOID,.7f,2f,1.7f,5,"",false,List.of()));
        add(new Species("nytherian","Nytherian","Nytheri",BodyType.EXOTIC,.7f,2f,1.7f,5,"",false,List.of()));
        add(new Species("causalis","Causalis","Axiomar",BodyType.EXOTIC,.7f,2f,1.7f,30,"",false,List.of()));
        add(new Species("mimetrix","Mimetrix","Lemnari",BodyType.MONSTROUS,.8f,2f,1.7f,30,"",false,List.of()));
    }
    private static void add(Species s) { if(SPECIES.putIfAbsent(s.id(),s)!=null) throw new IllegalStateException(s.id()); }
    public static Species get(String id) { return SPECIES.get(id); }
    public static Collection<Species> all() { return Collections.unmodifiableCollection(SPECIES.values()); }
    public static List<Species> playable() { return SPECIES.values().stream().filter(Species::playable).toList(); }
    public static boolean ownedPower(String id) { return playable().stream().anyMatch(s->s.powerId(false).equals(id)||s.powerId(true).equals(id)); }
    private SpeciesRegistry() { }
}
