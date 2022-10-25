package zhar_feda.skytec.clan_test_task.exceptions;

public class ClanGoldDownException extends RuntimeException {

    public static final String MSG = "Can't down gold for clan %s: %s";

    public ClanGoldDownException(Long clanId, String msg) {
        super(String.format(MSG, clanId, msg));
    }
}
