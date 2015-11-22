package info.robotbrain.res;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoboRes extends JavaPlugin implements Listener
{
    private static Pattern[] resSyntaxes = { Pattern.compile("res\\[(?<name>[a-zA-Z0-9_.]+)]"), Pattern.compile("\\./res tp (?<name>[a-zA-Z0-9_.]+)") };

    @Override
    public void onEnable()
    {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable()
    {
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        String msg = event.getMessage();
        ArrayList<String> reses = new ArrayList<String>();
        for (Pattern syntax : resSyntaxes) {
            Matcher matcher = syntax.matcher(msg);
            while (matcher.find()) {
                reses.add(matcher.group("name"));
            }
        }
        if (reses.size() > 0) {
            linkRes(reses);
        }
    }

    private void linkRes(ArrayList<String> reses)
    {
        String text = "Res link";
        if (reses.size() > 1) {
            text += "s: ";
        } else {
            text += ": ";
        }
        ComponentBuilder builder = new ComponentBuilder(text);
        for (int i = 0; i < reses.size(); i++) {
            String name = reses.get(i);
            builder.append(name, FormatRetention.NONE);
            builder.bold(true);
            builder.event(new ClickEvent(Action.RUN_COMMAND, "/res tp " + name));
            if (i + 1 < reses.size()) {
                builder.append(", ", FormatRetention.NONE);
            }
        }
        getServer().spigot().broadcast(builder.create());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            String[] lines = sign.getLines();
            for (String line : lines) {
                for (Pattern resSyntax : resSyntaxes) {
                    Matcher matcher = resSyntax.matcher(line);
                    if (matcher.matches()) {
                        event.getPlayer().chat("/res tp " + matcher.group("name"));
                        return;
                    }
                }
            }
        }
    }
}
