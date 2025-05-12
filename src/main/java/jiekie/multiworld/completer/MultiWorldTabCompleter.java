package jiekie.multiworld.completer;

import jiekie.multiworld.MultiWorldPlugin;
import jiekie.nickname.api.NicknameAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiWorldTabCompleter implements TabCompleter {
    private final MultiWorldPlugin plugin;

    public MultiWorldTabCompleter(MultiWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!sender.hasPermission("jk.multi_world.command")) return Collections.emptyList();

        int length = args.length;
        if(length == 1)
            return Arrays.asList("생성", "제거", "설정", "초기화", "백업", "이동", "도움말");

        // 생성 제외
        String commandType = args[0];
        if(length == 2 && !(commandType.equals("생성") || commandType.equals("도움말")))
            return plugin.getWorldManager().getWorldNames();

        // 생성
        if(length == 3 && commandType.equals("생성"))
            return Arrays.asList("NORMAL", "NETHER", "THE_END");

        if(length == 4 && commandType.equals("생성"))
            return Arrays.asList("NORMAL", "FLAT");

        if(length == 5 && commandType.equals("생성"))
            return Arrays.asList("true", "false");

        if(length == 6 && commandType.equals("생성"))
            return List.of("시드");

        // 항목
        if(length == 3 && commandType.equals("설정"))
            return Arrays.asList("PVP", "시간흐름", "날씨변화", "팬텀스폰", "인벤세이브", "불번짐", "크리퍼폭발");

        if(length == 4 && commandType.equals("설정"))
            return Arrays.asList("true", "false");

        // 이동
        if(length == 3 && commandType.equals("이동"))
            return NicknameAPI.getInstance().getPlayerNameAndNicknameList();

        return Collections.emptyList();
    }
}
