package jiekie.command;

import jiekie.MultiWorldPlugin;
import jiekie.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultiWorldCommand implements CommandExecutor {
    private final MultiWorldPlugin plugin;

    public MultiWorldCommand(MultiWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            ChatUtil.notPlayer(sender);
            return true;
        }

        Player player = (Player) sender;
        if(!player.isOp()) {
            ChatUtil.notOp(player);
            return true;
        }

        if(args == null || args.length == 0) {
            ChatUtil.commandHelper(player);
            return true;
        }

        switch(args[0]) {
            case "생성":
                createWorld(player, args);
                break;

            case "제거":
                removeWorld(player, args);
                break;

            case "설정":
                setWorldRule(player, args);
                break;

            case "백업":
                backupWorld(player, args);
                break;

            case "이동":
                moveToWorld(player, args);
                break;

            case "도움말":
                ChatUtil.commandList(player);
                break;

            default:
                ChatUtil.commandHelper(player);
                break;
        }

        return true;
    }

    private void createWorld(Player player, String[] args) {
        if(args.length < 5) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/월드 생성 월드명 환경 타입 구조물여부 [시드])");
            return;
        }

        String worldName = args[1];
        if(!worldName.matches("[a-zA-Z0-9_\\-]+")) {
            ChatUtil.wrongWorldName(player);
            return;
        }

        boolean generateStructures = Boolean.parseBoolean(args[4]);
        Long seed = null;
        if(args.length > 5) {
            try {
                seed = Long.parseLong(args[5]);

            } catch(NumberFormatException e) {
                ChatUtil.seedNotNumber(player);
                return;
            }
        }

        plugin.getWorldManager().createWorld(player, worldName, args[2], args[3], generateStructures, seed);
    }

    private void removeWorld(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/월드 제거 월드명)");
            return;
        }

        plugin.getWorldManager().removeWorld(player, args[1]);
    }

    private void setWorldRule(Player player, String[] args) {
        if(args.length < 4) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/월드 설정 월드명 항목 설정값)");
            return;
        }

        boolean value = Boolean.parseBoolean(args[3]);
        plugin.getWorldManager().setWorldRule(player, args[1], args[2], value);
    }

    private void backupWorld(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/월드 백업 월드명)");
            return;
        }

        plugin.getWorldManager().backupWorld(player, args[1]);
    }

    private void moveToWorld(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/월드 이동 월드명 [플레이어ID])");
            return;
        }

        if(args.length == 2)
            plugin.getWorldManager().moveToWorld(player, args[1]);
        else
            plugin.getWorldManager().movePlayerToWorld(player, args[1], args[2]);
    }
}
