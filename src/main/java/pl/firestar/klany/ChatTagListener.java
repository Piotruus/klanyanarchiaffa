package pl.firestar.klany;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

/**
 * Dokleja przed nickiem na czacie ranking GRACZA (domyslnie [1000]).
 * Opakowuje format ulozony przez Essentials, wiec jego configu nie trzeba ruszac.
 * Format w configu: czat.format, dostepne: {punkty} {tag} {zabojstwa} {smierci}
 */
public class ChatTagListener implements Listener {

    private final FirestarKlany plugin;

    public ChatTagListener(FirestarKlany plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncChatEvent e) {
        if (!plugin.getConfig().getBoolean("czat.tag-w-czacie", true)) return;
        ChatRenderer poprzedni = e.renderer();
        e.renderer((zrodlo, nazwa, wiadomosc, widz) -> {
            Component baza = poprzedni.render(zrodlo, nazwa, wiadomosc, widz);
            UUID id = zrodlo.getUniqueId();
            RankingManager r = plugin.ranking();
            Klan klan = plugin.manager().klanGracza(id);
            RankingManager.Stat st = r.stat(id);

            String prefix = plugin.getConfig().getString("czat.format", "&8[&6{punkty}&8] ")
                    .replace("{punkty}", String.valueOf(r.punkty(id)))
                    .replace("{tag}", klan == null ? "Brak" : klan.tag())
                    .replace("{zabojstwa}", st == null ? "0" : String.valueOf(st.zabojstwa))
                    .replace("{smierci}", st == null ? "0" : String.valueOf(st.smierci));
            return Text.c(prefix).append(baza);
        });
    }
}
