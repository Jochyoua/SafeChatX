package studio.thevipershow.safechat.persistence.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "player_data")
public class PlayerData implements Cloneable {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @ElementCollection
    @CollectionTable(
            name = "flag_mapping",
            joinColumns = {@JoinColumn(name = "flag_id", referencedColumnName = "uuid")})
    @MapKeyColumn(name = "flag_name")
    @Column(name = "flags")
    private Map<String, Integer> flagsMap = new HashMap<>();

    @NotNull
    public Map<String, Integer> getFlagsMap() {
        return flagsMap;
    }

    public void setFlagsMap(@NotNull Map<String, Integer> flagsMap) {
        this.flagsMap = Objects.requireNonNull(flagsMap);
    }

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = Objects.requireNonNull(uuid);
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
