public class CriteriaBuilder {
    private final Criteria criteria;

    public CriteriaBuilder(String columnName, String value) {
        criteria = new Criteria(columnName, value);
    }

    public CriteriaBuilder or(String columnName, String value) {
        criteria.add(" OR ", columnName, value);
        return this;
    }
    public CriteriaBuilder and(String columnName, String value) {
        criteria.add(" AND ", columnName, value);
        return this;
    }

    public Criteria build() {
        return criteria;
    }
}
