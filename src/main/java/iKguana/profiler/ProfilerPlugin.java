package iKguana.profiler;

import cn.nukkit.plugin.PluginBase;

public class ProfilerPlugin extends PluginBase {
	public void onEnable() {
		getDataFolder().mkdirs();
		
		getServer().getPluginManager().registerEvents(new Profiler(this), this);
	}

	public void onDisable() {
		Profiler.getInstance().saveAll();
	}
}
