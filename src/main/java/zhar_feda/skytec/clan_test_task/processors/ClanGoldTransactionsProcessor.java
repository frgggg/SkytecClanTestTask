package zhar_feda.skytec.clan_test_task.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import zhar_feda.skytec.clan_test_task.models.Clan;
import zhar_feda.skytec.clan_test_task.models.ClanGoldTransaction;
import zhar_feda.skytec.clan_test_task.models.ClanGoldTransactionGoal;
import zhar_feda.skytec.clan_test_task.dao.ClanGoldTransactionDao;
import zhar_feda.skytec.clan_test_task.dao.ClanDao;
import zhar_feda.skytec.clan_test_task.service.ClanGoldTransactionService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
@Component
public class ClanGoldTransactionsProcessor {

    private final ClanGoldTransactionService clanGoldTransactionService;
    private final ClanGoldTransactionDao clanGoldTransactionDao;
    private final int maxTransactionsPerTick;
    private final Set<Long> clansInProcessSet = ConcurrentHashMap.newKeySet();
    private ExecutorService executor;

    @Autowired
    public ClanGoldTransactionsProcessor(ClanGoldTransactionService clanGoldTransactionService,
                                         ClanGoldTransactionDao clanGoldTransactionDao,
                                         @Value("${clan_gold_transactions.processor.max_transactions_per_tick}") int maxTransactionsPerTick) {
        this.clanGoldTransactionService = clanGoldTransactionService;
        this.clanGoldTransactionDao = clanGoldTransactionDao;
        this.maxTransactionsPerTick = maxTransactionsPerTick;

        executor = Executors.newFixedThreadPool(maxTransactionsPerTick);
    }

    @Scheduled(fixedDelay = 10)
    public void processTransactions() {
        // get executor free space
        int newTransactionsForProcessingCount = maxTransactionsPerTick - clansInProcessSet.size();
        if(newTransactionsForProcessingCount < 1) return;

        // get in processing clans ids
        List<Long> clansInProcess = new ArrayList<>(clansInProcessSet);

        // get free clans latest transactions with executor free space limit
        List<ClanGoldTransaction> transactionsForProcess = clanGoldTransactionDao.findForProcessing(
                clansInProcess, newTransactionsForProcessingCount);

        transactionsForProcess.forEach(it -> {
            // try to add new class to set
            if(clansInProcessSet.add(it.getClanId())) {
                executor.execute(() -> {
                    try {
                        clanGoldTransactionService.tryDoTransaction(it.getId());
                    } catch (RuntimeException e) {
                        clanGoldTransactionService.safelyMoveTransactionToError(it.getClanId(), it.getId());
                    } finally {
                        // free clan for new processing
                        clansInProcessSet.remove(it.getClanId());
                    }
                });
            }
        });
    }
}
