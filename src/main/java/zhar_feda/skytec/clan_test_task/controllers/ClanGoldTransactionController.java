package zhar_feda.skytec.clan_test_task.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhar_feda.skytec.clan_test_task.api.req.CreateClanGoldTransactionReq;
import zhar_feda.skytec.clan_test_task.api.req.FindClanGoldTransactionsReq;
import zhar_feda.skytec.clan_test_task.models.ClanGoldTransaction;
import zhar_feda.skytec.clan_test_task.dao.ClanGoldTransactionDao;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/clan_gold_transaction")
public class ClanGoldTransactionController {

    private final ClanGoldTransactionDao clanGoldTransactionDao;

    @Autowired
    public ClanGoldTransactionController(ClanGoldTransactionDao clanGoldTransactionDao) {
        this.clanGoldTransactionDao = clanGoldTransactionDao;
    }

    @PostMapping("/find_by_filters")
    public List<ClanGoldTransaction> find(@RequestBody FindClanGoldTransactionsReq req) {
        log.info("Try find for {}", req);
        return clanGoldTransactionDao.find(
                req.getOperationOwners(),
                req.getClansIds(),
                req.getOperationNames(),
                req.getStates(),
                req.getCreatedAtFrom(),
                req.getCreatedAtTo(),
                req.getPage(),
                req.getPageSize()
        );
    }

    @GetMapping("/{id}")
    public ClanGoldTransaction findById(@PathVariable Long id) {
        return clanGoldTransactionDao.findById(id);
    }

    @PostMapping
    public ClanGoldTransaction create(@RequestBody CreateClanGoldTransactionReq transactionReq) {
        log.info("Try create new : {}", transactionReq);
        ClanGoldTransaction result = clanGoldTransactionDao.create(
                transactionReq.getClanId(),
                transactionReq.getOperationOwner(),
                transactionReq.getOperationName(),
                transactionReq.getGoldDiff(),
                transactionReq.getOperationGoal()
        );
        log.info("New transaction: {}", result);
        return result;
    }
}
