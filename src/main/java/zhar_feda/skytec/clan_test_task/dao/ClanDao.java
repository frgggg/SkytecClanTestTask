package zhar_feda.skytec.clan_test_task.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import zhar_feda.skytec.clan_test_task.exceptions.*;
import zhar_feda.skytec.clan_test_task.models.Clan;
import zhar_feda.skytec.clan_test_task.utils.Pair;
import zhar_feda.skytec.clan_test_task.validators.ValidationResult;
import zhar_feda.skytec.clan_test_task.validators.impl.ClanCreationValidator;
import zhar_feda.skytec.clan_test_task.validators.impl.ClanGoldDownValidation;
import zhar_feda.skytec.clan_test_task.validators.impl.ClanGoldUpValidation;

import java.util.List;

import static zhar_feda.skytec.clan_test_task.utils.SqlUtils.DEF_PAGE;
import static zhar_feda.skytec.clan_test_task.utils.SqlUtils.DEF_PAGE_SIZE;

@Service
public class ClanDao {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GoldUpdateResult {
        private Long before;
        private Long after;
    }

    public static final String INSERT_NEW_CLAN_SQL = "insert into clan (name, gold) values (?, 0)";
    public static final String FIND_BY_NAME_SQL = "select * from clan where name = ?";
    public static final String FIND_BY_ID_SQL = "select * from clan where id = ?";
    public static final String FIND_ALL_SQL = "select * from clan order by id asc limit ? offset ?";
    public static final String UPDATE_GOLD = "update clan set gold = ? where id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final ClanCreationValidator clanCreationValidator;
    private final ClanGoldUpValidation clanGoldUpValidation;
    private final ClanGoldDownValidation clanGoldDownValidation;

    @Autowired
    public ClanDao(JdbcTemplate jdbcTemplate, ClanCreationValidator clanCreationValidator, ClanGoldUpValidation clanGoldUpValidation, ClanGoldDownValidation clanGoldDownValidation) {
        this.jdbcTemplate = jdbcTemplate;
        this.clanCreationValidator = clanCreationValidator;
        this.clanGoldUpValidation = clanGoldUpValidation;
        this.clanGoldDownValidation = clanGoldDownValidation;
    }

    public Clan saveNew(String name) {
        ValidationResult validationResult = clanCreationValidator.isValid(name);
        if(validationResult.isNotValid()) {
            throw new ClanCreationException(validationResult.getMsg());
        }

        jdbcTemplate.update(INSERT_NEW_CLAN_SQL, name);

        return findByName(name);
    }


    public Clan findByName(String name) {
        try {
            return jdbcTemplate.queryForObject(FIND_BY_NAME_SQL, new Object[]{name}, new BeanPropertyRowMapper<>(Clan.class));
        } catch (EmptyResultDataAccessException e) {
            throw new DbObjectFindException("Clan", new Pair<>("name", name));
        }
    }

    public Clan findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, new Object[]{id}, new BeanPropertyRowMapper<>(Clan.class));
        } catch (EmptyResultDataAccessException e) {
            throw new DbObjectFindException("Clan", new Pair<>("id", id));
        }
    }

    public List<Clan> findAll(Integer page, Integer pageSize) {
        if(page == null || page < 0) {
            page = DEF_PAGE;
        }
        if(pageSize == null || pageSize < 0) {
            pageSize = DEF_PAGE_SIZE;
        }
        return jdbcTemplate.query(FIND_ALL_SQL, new Object[] {pageSize, page * pageSize}, new BeanPropertyRowMapper<>(Clan.class));
    }

    public GoldUpdateResult upGold(Long clanId, Long plusGold) {
        Clan clan = findById(clanId);
        ValidationResult validationResult = clanGoldUpValidation.isValid(new ClanGoldUpValidation.Args(clan, plusGold));
        if(validationResult.isNotValid()) {
            throw new ClanGoldUpException(clanId, validationResult.getMsg());
        }

        long newGoldVal = clan.getGold() + plusGold;
        jdbcTemplate.update(UPDATE_GOLD, clan.getGold() + plusGold, clanId);

        return new GoldUpdateResult(clan.getGold(), newGoldVal);
    }

    public GoldUpdateResult downGold(Long clanId, Long minusGold) {
        Clan clan = findById(clanId);
        ValidationResult validationResult = clanGoldDownValidation.isValid(new ClanGoldDownValidation.Args(clan, minusGold));
        if(validationResult.isNotValid()) {
            throw new ClanGoldDownException(clanId, validationResult.getMsg());
        }

        long newGoldVal = clan.getGold() - minusGold;
        jdbcTemplate.update(UPDATE_GOLD, clan.getGold() - minusGold, clanId);

        return new GoldUpdateResult(clan.getGold(), newGoldVal);
    }
}
