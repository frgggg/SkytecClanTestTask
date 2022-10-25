package zhar_feda.skytec.clan_test_task.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClanGoldTransaction {
    private Long id;

    private Long clanId;
    private String operationOwner;
    private String operationName;
    private ClanGoldTransactionGoal operationGoal;

    private Long goldBefore;
    private Long goldDiff;
    private Long goldAfter;

    private ClanGoldTransactionState operationState;

    private LocalDateTime createAt;
    private LocalDateTime endAt;


}
