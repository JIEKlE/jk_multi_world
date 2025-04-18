package jiekie.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtil {
    /* 에러 메시지 */
    public static String INVALID_ENVIRONMENT = "사용할 수 없는 월드 환경입니다. (NORMAL | NETHER | THE_END)";
    public static String INVALID_TYPE = "사용할 수 없는 월드 타입입니다. (NORMAL | FLAT)";
    public static String INVALID_WORLD_NAME = "월드 이름은 영어와 숫자, '_' 또는 '-'만 입력할 수 있습니다.";
    public static String INVALID_SEED = "시드는 숫자만 입력할 수 있습니다.";
    public static String DEFAULT_WORLD_NAME = "사용할 수 없는 월드명입니다. (기본 월드명)";
    public static String WORLD_NAME_ALREADY_EXISTS = "해당 월드명을 이미 사용 중입니다.";
    public static String WORLD_NOT_CREATED = "생성되지 않은 월드입니다.";
    public static String WORLD_NOT_FOUND = "존재하지 않는 월드입니다.";
    public static String PLAYERS_EXIST = "해당 월드에 플레이어가 1명 이상 존재합니다.";
    public static String INVALID_RULE = "존재하지 않는 규칙입니다.";
    public static String PLAYER_NOT_FOUND = "해당 이름을 가진 플레이어를 찾을 수 없습니다.";

    public static String FAIL_TO_CREATE_WORLD = "월드를 생성하는데 실패했습니다.";
    public static String FAIL_TO_DELETE_WORLD_FOLDER = "월드를 폴더를 제거하는데 실패했습니다.";
    public static String FAIL_TO_BACKUP_WORLD_FOLDER = "월드를 폴더를 백업하는데 실패했습니다.";

    public static String BROADCAST_DELETE_WORLD = "[ " + ChatColor.RED + ChatColor.BOLD + "공 지" + ChatColor.RESET + " ] 월드를 제거 중입니다. 모든 플레이어는 이동을 멈춰주세요.";
    public static String BROADCAST_RESET_WORLD = "[ " + ChatColor.RED + ChatColor.BOLD + "공 지" + ChatColor.RESET + " ] 월드를 초기화 중입니다. 모든 플레이어는 이동을 멈춰주세요.";
    public static String BROADCAST_BACKUP_WORLD = "[ " + ChatColor.RED + ChatColor.BOLD + "공 지" + ChatColor.RESET + " ] 월드를 백업 중입니다. 모든 플레이어는 이동을 멈춰주세요.";

    public static String getWarnPrefix() {
        return "[ " + ChatColor.YELLOW + "❗" + ChatColor.WHITE + " ] ";
    }

    /* 유효성 검사 */
    public static void notPlayer(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "플레이어가 아닙니다.");
    }

    public static void notOp(Player player) {
        player.sendMessage(getWarnPrefix() + "권한이 없습니다.");
    }

    public static String wrongCommand() {
        return getWarnPrefix() + "명령어 사용법이 잘못되었습니다.";
    }

    /* 피드백 */
    public static void showErrorMessage(Player player, String message) {
        player.sendMessage(getWarnPrefix() + message);
    }

    public static void createWorld(Player player) {
        player.sendMessage(getWarnPrefix() + "월드를 생성했습니다.");
    }

    public static void removeWorld(Player player) {
        player.sendMessage(getWarnPrefix() + "월드를 제거했습니다.");
    }

    public static void resetWorld(Player player) {
        player.sendMessage(getWarnPrefix() + "월드를 초기화했습니다.");
    }

    public static void backupWorld(Player player, String path, String fileName) {
        player.sendMessage(getWarnPrefix() + "월드를 백업했습니다.");
        player.sendMessage("　　　경로 : " + path);
        player.sendMessage("　　　파일명 : " + fileName);
    }

    public static void setWorldRule(Player player) {
        player.sendMessage(getWarnPrefix() + "월드 규칙을 설정했습니다.");
    }

    public static void movePlayerToWorld(Player player) {
        player.sendMessage(getWarnPrefix() + "플레이어를 이동시켰습니다.");
    }
    
    /* 명령어 설명 */
    public static void commandHelper(Player player) {
        player.sendMessage(getWarnPrefix() + "/월드 도움말" + ChatColor.GRAY + " : 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void commandList(Player player) {
        player.sendMessage(getWarnPrefix() + "월드 명령어 목록");
        player.sendMessage("　　　① /월드 생성 월드명 환경 타입 구조물생성여부 [시드]");
        player.sendMessage(ChatColor.GRAY + "　　　　　: 월드를 생성합니다.");
        player.sendMessage("　　　② /월드 제거 월드명");
        player.sendMessage(ChatColor.GRAY + "　　　　　: 월드를 제거합니다.");
        player.sendMessage("　　　③ /월드 설정 월드명 항목 설정값");
        player.sendMessage(ChatColor.GRAY + "　　　　　: 월드 규칙을 설정합니다.");
        player.sendMessage("　　　④ /월드 초기화 월드명");
        player.sendMessage(ChatColor.GRAY + "　　　　　: 월드를 초기화합니다.");
        player.sendMessage("　　　⑤ /월드 백업 월드명");
        player.sendMessage(ChatColor.GRAY + "　　　　　: 월드를 백업합니다.");
        player.sendMessage("　　　⑥ /월드 이동 월드명");
        player.sendMessage(ChatColor.GRAY + "　　　　　: 월드로 이동합니다.");
        player.sendMessage("　　　⑦ /월드 이동 월드명 플레이어ID");
        player.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어를 월드로 이동시킵니다.");
        player.sendMessage("　　　⑧ /월드 도움말");
        player.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");
    }
}
