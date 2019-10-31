package iKguana.profiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

public class Profiler implements Listener {
    private static Profiler $instance = null;
    public HashMap<String, Config> configs;
    private File folder;

    public Profiler(ProfilerPlugin plugin) {
        if ($instance != null)
            return;

        $instance = this;

        folder = plugin.getDataFolder();
        folder.mkdirs();

        configs = new HashMap<>();
    }

    public boolean isPlayerRegistered(String name) {
        for (String player : getProfileList())
            if (player.equalsIgnoreCase(name))
                return true;
        return false;
    }

    public String getExactName(String name) {
        for (String player : getProfileList())
            if (player.equalsIgnoreCase(name))
                return player;
        return null;
    }

    public Config open(String player) {
        Config config;

        if (configs.containsKey(player))
            config = configs.get(player);
        else {
            config = new Config(folder + File.separator + player + ".yml", Config.YAML);
            configs.put(player, config);
            rc(player);
            reloadProfileList();
	}
        return config;
    }

    public void close(String player) {
        if (configs.containsKey(player)) {
            cc(configs.get(player));
            configs.get(player).save();
            configs.remove(player);
        }
    }

    ArrayList<String> cache = new ArrayList<>();

    public void reloadProfileList() {
        cache = new ArrayList<>();
        for (File data : folder.listFiles())
            if (data.getName().endsWith(".yml"))
                cache.add(data.getName().replace(".yml", ""));

    }

    public ArrayList<String> getProfileList() {
        return cache;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        open(event.getPlayer().getName());
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        close(event.getPlayer().getName());
    }

    public void saveAll() {
        for (Config cfg : configs.values()) {
            cc(cfg);
            cfg.save();
        }
    }

    public void rc(String player) {
        if (player.toLowerCase().equals(player))
            return;
        File file = new File(folder + File.separator + player.toLowerCase() + ".yml");
        if (file.exists()) {
            Config originalCfg = new Config(folder + File.separator + player.toLowerCase() + ".yml", Config.YAML);
            file.delete();
            Config newCfg = open(player);
            newCfg.setAll((LinkedHashMap<String, Object>) originalCfg.getAll());
            newCfg.save();
        }
    }

    public void cc(Config cfg) {
        ConfigSection newCS = new ConfigSection();
        ArrayList<String> keys = new ArrayList<>(cfg.getKeys(false));
        Collections.sort(keys);
        for (String key : keys)
            newCS.set(key, cfg.get(key));
        cfg.setAll(newCS);
    }

    public static Profiler getInstance() {
        return $instance;
    }
}
