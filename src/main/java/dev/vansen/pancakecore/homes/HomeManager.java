package dev.vansen.pancakecore.homes;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import dev.vansen.pancakecore.PancakeCore;
import dev.vansen.pancakecore.homes.util.Home;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class HomeManager {
    private static final Kryo kryo = new Kryo();

    static {
        kryo.register(HashMap.class, new DefaultSerializers.KryoSerializableSerializer());
        kryo.setRegistrationRequired(false);
    }

    public static void createHome(OfflinePlayer player, int index, Location location) {
        PancakeCore.sqliteHomes()
                .add("homes")
                .value("uuid", player.getUniqueId().toString())
                .value("index", index)
                .value("location", serialize(location.serialize()))
                .insert();
    }

    public static void deleteHome(OfflinePlayer player, int index) {
        PancakeCore.sqliteHomes()
                .delete("homes")
                .where("uuid", player.getUniqueId().toString())
                .where("index", index)
                .execute();
    }

    public static boolean isSet(OfflinePlayer player, int index) {
        return PancakeCore.sqliteHomes()
                .read()
                .table("homes")
                .where("uuid", player.getUniqueId().toString())
                .where("index", index)
                .fetch() != null;
    }

    public static void updateHome(OfflinePlayer player, int index, Location location) {
        PancakeCore.sqliteHomes()
                .update("homes")
                .where("uuid", player.getUniqueId().toString())
                .where("index", index)
                .set("location", serialize(location.serialize()))
                .execute();
    }

    public static Home getHome(OfflinePlayer player, int index) {
        byte[] data = PancakeCore.sqliteHomes()
                .read()
                .table("homes")
                .where("uuid", player.getUniqueId().toString())
                .where("index", index)
                .fetch(byte[].class);
        if (data == null) return null;
        return new Home(index, Location.deserialize(deserialize(data)));
    }

    public static byte[] serialize(Map<String, Object> map) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        kryo.writeObject(output, map);
        output.close();
        return bos.toByteArray();
    }

    @SuppressWarnings("all")
    public static Map<String, Object> deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Input input = new Input(bis);
        Map<String, Object> map = kryo.readObject(input, HashMap.class);
        input.close();
        return map;
    }
}