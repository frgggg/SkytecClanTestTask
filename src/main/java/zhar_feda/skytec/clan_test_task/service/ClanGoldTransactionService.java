package zhar_feda.skytec.clan_test_task.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import zhar_feda.skytec.clan_test_task.dao.ClanDao;
import zhar_feda.skytec.clan_test_task.dao.ClanGoldTransactionDao;
import zhar_feda.skytec.clan_test_task.models.ClanGoldTransaction;
import zhar_feda.skytec.clan_test_task.models.ClanGoldTransactionGoal;

@Slf4j
@Service
public class ClanGoldTransactionService {
    private final ClanDao clanDao;
    private final ClanGoldTransactionDao clanGoldTransactionDao;

    @Autowired
    public ClanGoldTransactionService(ClanDao clanDao, ClanGoldTransactionDao clanGoldTransactionDao) {
        this.clanDao = clanDao;
        this.clanGoldTransactionDao = clanGoldTransactionDao;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void tryDoTransaction(Long transactionId) {
        log.debug("Try do transaction {}", transactionId);
        ClanDao.GoldUpdateResult updateResult;
        ClanGoldTransaction transaction = clanGoldTransactionDao.findById(transactionId);
        if(transaction.getOperationGoal().equals(ClanGoldTransactionGoal.UP)) {
            updateResult = clanDao.upGold(transaction.getClanId(), transaction.getGoldDiff());
        } else {
            updateResult = clanDao.downGold(transaction.getClanId(), transaction.getGoldDiff());
        }
        log.debug("Transaction {} ended", transactionId);
        clanGoldTransactionDao.moveTransactionsToSuccess(transaction.getId(), updateResult.getBefore(), updateResult.getAfter());
    }

    public void safelyMoveTransactionToError(Long clanId, Long transactionId) {
        try {
            Long clanGolds = trySafelyGetClanGolds(clanId, transactionId);
            clanGoldTransactionDao.moveTransactionsToFailed(transactionId, clanGolds);
        } catch (RuntimeException e) {
            log.error("Can't move transaction {} to error: {}", transactionId, e);
            trySafelyDeleteTransaction(transactionId);
        }
    }

    private Long trySafelyGetClanGolds(Long clanId, Long transactionId) {
        try {
            return clanDao.findById(clanId).getGold();
        } catch (RuntimeException e) {
            log.error("Can't get clan {} for error transaction {}: {}", clanId, transactionId, e);
            return -1L;
        }
    }

    private void trySafelyDeleteTransaction(Long transactionId) {
        try {
            clanGoldTransactionDao.deleteById(transactionId);
        } catch (RuntimeException e) {
            log.error("Can't delete error transaction {}: {}", transactionId, e);
        }
    }
}
