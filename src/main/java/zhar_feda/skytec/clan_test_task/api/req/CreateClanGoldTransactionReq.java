package zhar_feda.skytec.clan_test_task.api.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zhar_feda.skytec.clan_test_task.models.ClanGoldTransactionGoal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateClanGoldTransactionReq {
    private Long clanId;
    private String operationOwner;
    private String operationName;
    private Long goldDiff;
    private ClanGoldTransactionGoal operationGoal;
}
