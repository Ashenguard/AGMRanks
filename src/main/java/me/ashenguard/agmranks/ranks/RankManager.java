package me.ashenguard.agmranks.ranks;

import me.ashenguard.agmranks.AGMRanks;
import me.ashenguard.agmranks.ranks.systems.EconomySystem;
import me.ashenguard.agmranks.ranks.systems.ExperienceSystem;
import me.ashenguard.agmranks.ranks.systems.PlaytimeSystem;
import me.ashenguard.agmranks.ranks.systems.RankingSystem;
import me.ashenguard.api.messenger.Messenger;
import me.ashenguard.api.utils.encoding.Ordinal;

import java.io.File;
import java.util.LinkedHashMap;

public class RankManager {
    private final AGMRanks plugin = AGMRanks.getInstance();
    private final Messenger messenger = AGMRanks.getMessenger();

    public final File folder = new File(plugin.getDataFolder(), "Ranks");
    public final RankingSystem rankingSystem;

    public RankManager() {
        if (!folder.exists() && folder.mkdirs()) messenger.Debug("General", "Ranks folder wasn't found, A new one created");
        File temporary = new File(folder, "1st.yml");
        if (!temporary.exists()) plugin.saveResource("Ranks/1st.yml", false);

        String type = plugin.getConfig().getString("RankingSystem.Type", "Money");
        if (EconomySystem.isType(type)) rankingSystem = new EconomySystem();
        else if (ExperienceSystem.isType(type)) rankingSystem = new ExperienceSystem();
        else if (PlaytimeSystem.isType(type)) rankingSystem = new PlaytimeSystem();
        else rankingSystem = new EconomySystem();
        rankingSystem.onEnable();
    }

    private LinkedHashMap<Integer, Rank> ranks = new LinkedHashMap<>();
    public void loadRanks() {
        ranks = new LinkedHashMap<>();

        for (int id = 1; isRankDefined(id); id++) {
            Rank temp = new Rank(id);
            saveRank(temp);
        }

        messenger.Debug("Ranks", "All Ranks has been loaded", "Ranks Count= §6" + ranks.size());
    }

    public void saveRank(Rank rank) {
        if (ranks.containsKey(rank.id)) return;
        ranks.putIfAbsent(rank.id, rank);
        messenger.Debug("Ranks", "Rank has been loaded", "Rank= §6" + Ordinal.to(rank.id), "Group= §6" + rank.group, "Name= §6" + rank.getTranslatedName(), "Cost= §6" + rank.cost);
    }

    public boolean isRankDefined(int id) {
        return new File(folder, Ordinal.to(id) + ".yml").exists();
    }

    public Rank getRank(int i) {
        return ranks.getOrDefault(i, null);
    }
    public LinkedHashMap<Integer, Rank> getRanks() {
        return new LinkedHashMap<>(ranks);
    }
}