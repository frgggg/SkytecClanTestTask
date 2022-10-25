package zhar_feda.skytec.clan_test_task.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair <K, V> {
    private K key;
    private V value;
}
