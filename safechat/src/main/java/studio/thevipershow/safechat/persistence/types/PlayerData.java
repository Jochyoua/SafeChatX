package studio.thevipershow.safechat.persistence.types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "player_data")
public final class PlayerData {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "repetition_flags")
    private int repetitionFlags = 0;

    @Column(name = "flood_flags")
    private int floodFlags = 0;

    @Column(name = "blacklist_flags")
    private int blacklistFlags = 0;

    @Column(name = "address_flags")
    private int addressFlags = 0;

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public int getRepetitionFlags() {
        return repetitionFlags;
    }

    public void setRepetitionFlags(int repetitionFlags) {
        this.repetitionFlags = repetitionFlags;
    }

    public int getFloodFlags() {
        return floodFlags;
    }

    public void setFloodFlags(int floodFlags) {
        this.floodFlags = floodFlags;
    }

    public int getBlacklistFlags() {
        return blacklistFlags;
    }

    public void setBlacklistFlags(int blacklistFlags) {
        this.blacklistFlags = blacklistFlags;
    }

    public int getAddressFlags() {
        return addressFlags;
    }

    public void setAddressFlags(int addressFlags) {
        this.addressFlags = addressFlags;
    }
}
