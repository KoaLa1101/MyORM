import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Criteria {
    private StringBuilder field;
    private ArrayList<String> values;

    public Criteria(String columnName, String value) {
        field = new StringBuilder();
        field.append(" WHERE ");
        values = new ArrayList<>();

        add("", columnName, value);
    }

    public boolean add(String condition, String columnName, String value) {
        if (!check(columnName, value)) return false;

        field.append(condition).append(columnName).append("=?");
        values.add(value);
        return true;
    }

    private boolean check(String columnName, String value) {
        if (columnName == null || value == null) return false;
        if (columnName.isEmpty() || value.isEmpty()) return false;
        return true;
    }

    public ArrayList<String> getValues() {
        return values;
    }


}
