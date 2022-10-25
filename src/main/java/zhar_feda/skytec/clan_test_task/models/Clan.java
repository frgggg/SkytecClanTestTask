package zhar_feda.skytec.clan_test_task.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clan {
    private Long id;
    private String name;
    private Long gold;
}
