package zhar_feda.skytec.clan_test_task.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import zhar_feda.skytec.clan_test_task.exceptions.ClanGoldTransactionCreationException;
import zhar_feda.skytec.clan_test_task.exceptions.DbObjectFindException;
import zhar_feda.skytec.clan_test_task.models.ClanGoldTransaction;
import zhar_feda.skytec.clan_test_task.models.ClanGoldTransactionGoal;
import zhar_feda.skytec.clan_test_task.models.ClanGoldTransactionState;
import zhar_feda.skytec.clan_test_task.utils.Pair;
import zhar_feda.skytec.clan_test_task.utils.SqlUtils;
import zhar_feda.skytec.clan_test_task.validators.ValidationResult;
import zhar_feda.skytec.clan_test_task.validators.impl.ClanGoldTransactionCreationValidator;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static zhar_feda.skytec.clan_test_task.utils.SqlUtils.*;

@Service
public class ClanGoldTransactionDao {

    public static final String INSERT_NEW = "insert into clan_gold_tr " +
            "(clanId, operationName, operationOwner, operationGoal, goldDiff, createAt, operationState) values " +
            "(?, ?, ?, ?, ?, now(), 'CREATED')";
    public static final String FIND_BY_ID_SQL = "select * from clan_gold_tr where id = ?";
    public static final String FIND_BY_ID_AND_STATE_SQL = "select * from clan_gold_tr where id = ? and operationState = ?";
    public static final String MOVE_TO_END_SQL = "update clan_gold_tr set (operationState, goldBefore, goldAfter, endAt) = (?, ?, ?, now()) where id = ?";

    public static final String FIND_CLANS_FOR_TR_PROCESSING_SQL =
            "select clanId, min(createAt) as createAt " +
            "    from clan_gold_tr " +
            "where clanId not in (%s) and operationState='CREATED' " +
            "    group by clanId";
    public static final String FIND_ALL_TR_FOR_PROCESSING_SQL =
            "select tr.* " +
            "    from clan_gold_tr tr join (" + FIND_CLANS_FOR_TR_PROCESSING_SQL + ") as cl " +
            "on tr.clanId = cl.clanId and tr.createAt = cl.createAt and tr.operationState='CREATED'";
    public static final String FIND_FIRST_TR_FOR_PROCESSING_PER_CLAN_SQL =
            "select min(sort_tr.id) as id,  sort_tr.createAt as createAt" +
            "    from (" + FIND_ALL_TR_FOR_PROCESSING_SQL + ") as sort_tr " +
            "group by sort_tr.clanId, sort_tr.createAt";

    public static final String FIND_FOR_PROCESSING_SQL =
            "select curr_tr.* " +
            "    from clan_gold_tr curr_tr join (" + FIND_FIRST_TR_FOR_PROCESSING_PER_CLAN_SQL + ") as tr_id " +
            "on curr_tr.id = tr_id.id " +
            "order by curr_tr.createAt " +
            "limit %s";
    public static final String DELETE_BY_ID_SQL = "delete from clan_gold_tr where id = ?";

    private static final DateTimeFormatter FIND_SQL_DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final String FIND_SQL_PREFIX =
            "select * from clan_gold_tr where operationOwner in (%s) ";

    private final JdbcTemplate jdbcTemplate;
    private final ClanGoldTransactionCreationValidator clanGoldTransactionCreationValidator;

    @Autowired
    public ClanGoldTransactionDao(JdbcTemplate jdbcTemplate, ClanGoldTransactionCreationValidator clanGoldTransactionCreationValidator) {
        this.jdbcTemplate = jdbcTemplate;
        this.clanGoldTransactionCreationValidator = clanGoldTransactionCreationValidator;
    }

    public ClanGoldTransaction create(Long clanId, String operationOwner, String operationName, Long goldDiff, ClanGoldTransactionGoal operationGoal) {
        ValidationResult validationResult =
                clanGoldTransactionCreationValidator.isValid(new ClanGoldTransactionCreationValidator.Args(
                        clanId, operationOwner, operationName, goldDiff, operationGoal));
        if(validationResult.isNotValid()) {
            throw new ClanGoldTransactionCreationException(validationResult.getMsg());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_NEW);
            ps.setLong(1, clanId);
            ps.setString(2, operationName);
            ps.setString(3, operationOwner);
            ps.setString(4, operationGoal.toString());
            ps.setLong(5, goldDiff);
            return ps;
        }, keyHolder);

        return findById((long) keyHolder.getKey());
    }

    public int moveTransactionsToSuccess(Long id, Long goldBefore, Long goldAfter) {
        return moveTransactionsToEnd(id, goldBefore, goldAfter, ClanGoldTransactionState.SUCCESS);
    }

    public int moveTransactionsToFailed(Long id, Long golds) {
        return moveTransactionsToEnd(id, golds, golds, ClanGoldTransactionState.FAILED);
    }

    private int moveTransactionsToEnd(Long id, Long goldBefore, Long goldAfter, ClanGoldTransactionState state) {
        return jdbcTemplate.update(MOVE_TO_END_SQL, state.toString(), goldBefore, goldAfter, id);
    }

    public ClanGoldTransaction findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, new Object[]{id}, new BeanPropertyRowMapper<>(ClanGoldTransaction.class));
        } catch (EmptyResultDataAccessException e) {
            throw new DbObjectFindException("ClanGoldTransaction", new Pair<>("id", id));
        }
    }

    public ClanGoldTransaction findByIdAndState(Long id, ClanGoldTransactionState state) {
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_AND_STATE_SQL, new Object[]{id, state}, new BeanPropertyRowMapper<>(ClanGoldTransaction.class));
        } catch (EmptyResultDataAccessException e) {
            throw new DbObjectFindException("ClanGoldTransaction", new Pair<>("id", id), new Pair<>("state", state));
        }
    }

    public List<ClanGoldTransaction> findForProcessing(
            List<Long> notClansIds,
            Integer limit) {
        String query = String.format(FIND_FOR_PROCESSING_SQL, SqlUtils.longListToSqlList(notClansIds), limit);
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ClanGoldTransaction.class));
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID_SQL, id);
    }

    public List<ClanGoldTransaction> find(
            List<String> operationOwners,
            List<Long> clansIds,
            List<String> operationNames,
            List<ClanGoldTransactionState> states,
            LocalDateTime createdAtFrom,
            LocalDateTime createdAtTo,
            Integer page,
            Integer pageSize) {
        String minQuery = String.format(FIND_SQL_PREFIX, SqlUtils.stringListToSqlList(operationOwners));
        StringBuilder query = new StringBuilder(minQuery);

        if(isListNotNullAndNotEmpty(clansIds)) {
            query.append("and clanId in (").append(SqlUtils.longListToSqlList(clansIds)).append(") ");
        }

        if(isListNotNullAndNotEmpty(operationNames)) {
            query.append("and operationName in (").append(SqlUtils.stringListToSqlList(operationNames)).append(") ");
        }

        if(isListNotNullAndNotEmpty(states)) {
            query.append("and operationState in (").append(SqlUtils.stringListToSqlList(states)).append(") ");
        }

        if(createdAtFrom != null) {
            String time = createdAtFrom.format(FIND_SQL_DATA_TIME_FORMATTER);
            query.append("and createAt >= '").append(time).append("' ");
        }

        if(createdAtTo != null) {
            String time = createdAtTo.format(FIND_SQL_DATA_TIME_FORMATTER);
            query.append("and createAt <= '").append(time).append("' ");
        }

        if(page == null || page < 0) {
            page = DEF_PAGE;
        }
        if(pageSize == null || pageSize < 0) {
            pageSize = DEF_PAGE_SIZE;
        }

        query.append("order by operationOwner ");
        query.append("limit ").append(pageSize).append(" offset ").append(page*pageSize);

        return jdbcTemplate.query(query.toString(), new BeanPropertyRowMapper<>(ClanGoldTransaction.class));
    }
}
