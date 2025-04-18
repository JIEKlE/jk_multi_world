package jiekie.command;

import jiekie.MultiWorldPlugin;
import jiekie.exception.*;
import jiekie.util.ChatUtil;
import jiekie.util.SoundUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

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

            case "초기화":
                resetWorld(player, args);
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

        boolean generateStructures = Boolean.parseBoolean(args[4]);
        Long seed = null;
        if(args.length > 5) {
            try {
                seed = Long.parseLong(args[5]);

            } catch(NumberFormatException e) {
                ChatUtil.showErrorMessage(player, ChatUtil.INVALID_SEED);
                return;
            }
        }

        try {
            plugin.getWorldManager().createWorld(args[1], args[2], args[3], generateStructures, seed);
            ChatUtil.createWorld(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (WorldCreationException e) {
            ChatUtil.showErrorMessage(player, e.getMessage());
        }
    }

    private void removeWorld(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/월드 제거 월드명)");
            return;
        }

        try {
            plugin.getWorldManager().deleteWorld(args[1]);
            ChatUtil.removeWorld(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (WorldDeletionException e) {
            ChatUtil.showErrorMessage(player, e.getMessage());
        }
    }

    private void setWorldRule(Player player, String[] args) {
        if(args.length < 4) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/월드 설정 월드명 항목 설정값)");
            return;
        }

        boolean value = Boolean.parseBoolean(args[3]);
        try {
            plugin.getWorldManager().setWorldRule(args[1], args[2], value);
            ChatUtil.setWorldRule(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (WorldRuleChangeException e) {
            ChatUtil.showErrorMessage(player, e.getMessage());
        }
    }

    private void resetWorld(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/월드 초기화 월드명)");
            return;
        }

        try {
            plugin.getWorldManager().resetWorld(args[1]);
            ChatUtil.resetWorld(player);
            SoundUtil.playNoteBlockBell(player);

        } catch (WorldResetException e) {
            ChatUtil.showErrorMessage(player, e.getMessage());
        }
    }

    private void backupWorld(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/월드 백업 월드명)");
            return;
        }

        try {
            Map<String, String> fileInfo = plugin.getWorldManager().backupWorld(args[1]);
            ChatUtil.backupWorld(player, fileInfo.get("path"), fileInfo.get("name"));
            SoundUtil.playNoteBlockBell(player);

        } catch (WorldBackupException e) {
            ChatUtil.showErrorMessage(player, e.getMessage());
        }
    }

    private void moveToWorld(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/월드 이동 월드명 [플레이어ID])");
            return;
        }

        if(args.length == 2) {
            try {
                plugin.getWorldManager().moveToWorld(player.getName(), args[1]);
            } catch (TeleportToWorldException e) {
                ChatUtil.showErrorMessage(player, e.getMessage());
            }
        } else {
            try {
                plugin.getWorldManager().moveToWorld(args[2], args[1]);
                ChatUtil.movePlayerToWorld(player);
                SoundUtil.playNoteBlockBell(player);

            } catch (TeleportToWorldException e) {
                ChatUtil.showErrorMessage(player, e.getMessage());
            }
        }

    }
}
