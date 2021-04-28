import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Em {
    private ResultSet resultSet;
    private DataSource dataSource;
    private PreparedStatement preparedStatement;
    private StringBuilder stringBuilder;
    private String SQL;

    public static void main(String[] args) {
        Em em = new Em(new MyDataSource("jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC", "root", "KoaLa1101&"));
        Entity entity = new Entity();
        entity.setName("PING");
        String tableName = "entity";
        System.out.println(em.findAll(tableName, Entity.class, Integer.class));
    }

    public Em(DataSource dataSource) {
        this.dataSource = dataSource;
        stringBuilder = new StringBuilder();
    }

    public <T, ID> T findById(String tableName, Class<T> resultType, Class<ID> idType, ID idVal) {
        SQL = "SELECT * FROM ";
        stringBuilder.append(SQL).append(tableName).append(" WHERE id =?");
        try (Connection connection = dataSource.getConnection()) {
            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            preparedStatement.setObject(1, idVal);
            System.out.println(preparedStatement);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            T result = resultType.newInstance();
            Method m;
            for (Field i: resultType.getDeclaredFields()) {
                StringBuilder toChange = new StringBuilder();
                toChange.append(i.getName());
                toChange.setCharAt(0, Character.toUpperCase(toChange.charAt(0)));

                m = resultType.getMethod("set" + toChange, i.getType());
                m.invoke(result, resultSet.getObject(i.getName()));
            }
            stringBuilder.delete(0, stringBuilder.length());
            return result;
        } catch (SQLException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public <T, ID> List<T> findAll(String tableName, Class<T> resultType, Class<ID> idType) {
        List<T> arrayList = new ArrayList<>();
        SQL = "SELECT * FROM ";

        stringBuilder.append(SQL).append(tableName);
        try (Connection connection = dataSource.getConnection()) {
            preparedStatement = connection.prepareStatement(stringBuilder.toString());
            System.out.println(preparedStatement);
            resultSet = preparedStatement.executeQuery();
            Method m;
            while (resultSet.next()) {
                T result = resultType.newInstance();
                for (Field i: resultType.getDeclaredFields()) {
                    StringBuilder toChange = new StringBuilder();
                    toChange.append(i.getName());
                    toChange.setCharAt(0, Character.toUpperCase(toChange.charAt(0)));

                    m = resultType.getMethod("set" + toChange, i.getType());
                    m.invoke(result, resultSet.getObject(i.getName()));
                }
                stringBuilder.delete(0, stringBuilder.length());
                arrayList.add(result);
            }
            return arrayList;
        } catch (SQLException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException throwables) {
            throwables.printStackTrace();
            return null;
        }

    }

    public void emInsert(String tableName, Object entity) {
        StringBuilder tempSB = new StringBuilder();
        SQL = "INSERT INTO ";
        Class<?> entityClass = entity.getClass();
        try (Connection connection = dataSource.getConnection()) {
            stringBuilder.append(SQL).append(tableName).append(" ").append("(");
            for (Field i: entityClass.getDeclaredFields()) {
                i.setAccessible(true);
                if(!i.getName().equals("id")) {
                    stringBuilder.append(i.getName()).append(", ");
                    tempSB.append("'").append(i.get(entity)).append("'").append(", ");
                }
                i.setAccessible(false);

            }
            System.out.println(stringBuilder);
            System.out.println(tempSB);
            stringBuilder.delete(stringBuilder.length()-2, stringBuilder.length());
            stringBuilder.append(") VALUES (");
            tempSB.delete(tempSB.length()-2, tempSB.length());
            tempSB.append(");");
            stringBuilder.append(tempSB);

            SQL = stringBuilder.toString();
            System.out.println(SQL);
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.executeUpdate();

        } catch (SQLException | IllegalAccessException throwables) {
            throw new IllegalStateException(throwables);
        }

    }

}
