package thaumcraft.common.research.theorycraft;

import java.util.ArrayList;
import java.util.List;

public record TCResearchTableDiagnosticReport(List<Row> rows) {
    public TCResearchTableDiagnosticReport {
        rows = List.copyOf(rows);
    }

    public boolean passed() {
        return rows.stream().allMatch(Row::passed);
    }

    public long passedCount() {
        return rows.stream().filter(Row::passed).count();
    }

    public long failedCount() {
        return rows.size() - passedCount();
    }

    static Builder builder() {
        return new Builder();
    }

    public record Row(String check, boolean passed, String notes) {
    }

    static final class Builder {
        private final ArrayList<Row> rows = new ArrayList<>();

        void pass(String check, String notes) {
            rows.add(new Row(check, true, notes));
        }

        void fail(String check, String notes) {
            rows.add(new Row(check, false, notes));
        }

        void check(String check, boolean passed, String notes) {
            rows.add(new Row(check, passed, notes));
        }

        TCResearchTableDiagnosticReport build() {
            return new TCResearchTableDiagnosticReport(rows);
        }
    }
}
