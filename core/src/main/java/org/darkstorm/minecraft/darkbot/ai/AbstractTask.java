package org.darkstorm.minecraft.darkbot.ai;

import org.darkstorm.minecraft.darkbot.MinecraftBot;

/**
 * Created by Marco on 1/6/2016.
 */
public abstract class AbstractTask implements Task {
    protected final MinecraftBot bot;
    protected final TaskManager manager;

    public AbstractTask(MinecraftBot bot) {
        this.bot = bot;
        this.manager = bot.getTaskManager();
    }

    protected final Activity getActivity() {
        return manager.getActivity();
    }
    protected final void setActivity(Activity activity) {
        manager.setActivity(activity);
    }
    protected final boolean hasActivity() {
        return manager.hasActivity();
    }
}
