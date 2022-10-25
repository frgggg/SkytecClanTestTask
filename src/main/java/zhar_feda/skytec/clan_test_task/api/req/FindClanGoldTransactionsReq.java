package zhar_feda.skytec.clan_test_task.api.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zhar_feda.skytec.clan_test_task.models.ClanGoldTransactionState;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindClanGoldTransactionsReq {
    private List<String> operationOwners;
    private List<Long> clansIds;
    private List<String> operationNames;
    private List<ClanGoldTransactionState> states;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private Integer page;
    private Integer pageSize;
}
