package zhar_feda.skytec.clan_test_task.exceptions;

public class ClanGoldUpException extends RuntimeException {

    public static final String MSG = "Can't up gold for clan %s: %s";

    public ClanGoldUpException(Long clanId, String msg) {
        super(String.format(MSG, clanId, msg));
    }
}
