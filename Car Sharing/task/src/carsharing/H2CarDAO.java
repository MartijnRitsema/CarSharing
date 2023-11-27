package carsharing;

        import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.function.Supplier;

class H2CarDao implements CarDao{

    private Supplier<Connection> connection;
    private Connection con;
    private PreparedStatement st;

    private final String GET_CARS_SQL = "SELECT * FROM CAR";

    private final String GET_COMPANY_CARS_SQL = "SELECT * FROM CAR WHERE COMPANY_ID = ?";

    private final String GET_CAR_SQL = "SELECT * FROM CAR WHERE ID = ?";

    private final String UPDATE_CAR_SQL = "UPDATE CAR SET NAME = ?, COMPANY_ID = ? WHERE ID = ?";

    private final String INSERT_CAR_SQL = "INSERT INTO CAR (NAME, COMPANY_ID) VALUES (?,?)";

    private final String DELETE_CAR_SQL = "DELETE FROM CAR WHERE ID = ?";

    private final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS CAR " +
            "(ID INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "NAME VARCHAR UNIQUE NOT NULL," +
            "COMPANY_ID INTEGER NOT NULL," +
            "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID));";

    private final String DROP_TABLE_SQL = "DROP TABLE CAR";

    public H2CarDao(Supplier<Connection> connectionSupplier) {
        this.connection = connectionSupplier;
    }

    @Override
    public List<Car> getCars() {
        List<Car> cl = new ArrayList<>();
        try {
            con = connection.get();
            st = con.prepareStatement(GET_CARS_SQL);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                cl.add(new Car(rs.getInt(1), rs.getNString(2), rs.getInt(3)));
            }
        } catch (Exception ex) {
            System.out.println("Error during a car list selection.\n" + ex.getMessage());
        } finally {
            releaseResources();
        }
        return cl;
    }

    @Override
    public List<Car> getCars(int companyId) {
        List<Car> cl = new ArrayList<>();
        try {
            con = connection.get();
            st = con.prepareStatement(GET_COMPANY_CARS_SQL);
            st.setInt(1,companyId);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                cl.add(new Car(rs.getInt(1), rs.getNString(2),rs.getInt(3)));
            }
        } catch (Exception ex) {
            System.out.println("Error during a car list selection.\n" + ex.getMessage());
        } finally {
            releaseResources();
        }
        return cl;
    }

    @Override
    public Car getCar(int id) {
        Car car = null;
        try {
            con = connection.get();
            st = con.prepareStatement(GET_CAR_SQL);
            st.setInt(1,id);
            ResultSet rs = st.executeQuery();
            if(rs.first()) car = new Car(rs.getInt(1), rs.getNString(2), rs.getInt(3));
        } catch (Exception ex) {
            System.out.println("Error during a car search.\n"+ ex.getMessage());
        } finally {
            releaseResources();
        }
        return car;
    }

    @Override
    public boolean updateCar(Car car) {
        boolean result = true;
        try {
            con = connection.get();
            st = con.prepareStatement(UPDATE_CAR_SQL);
            st.setString(1, car.getName());
            st.setInt(2, car.getCompanyId());
            st.setInt(3, car.getId());
            st.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error during a car update.\n" + ex.getMessage());
            result = false;
        } finally {
            releaseResources();
        }
        return result;
    }

    @Override
    public boolean insertCar(Car car) {
        boolean result = true;
        try {
            con = connection.get();
            st = con.prepareStatement(INSERT_CAR_SQL);
            st.setString(1, car.getName());
            st.setInt(2, car.getCompanyId());
            st.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error while trying to add a car.\n" + ex.getMessage());
            result = false;
        } finally {
            releaseResources();
        }
        return result;
    }

    @Override
    public boolean deleteCar(Car car) {
        boolean result = true;
        try {
            con = connection.get();
            st = con.prepareStatement(DELETE_CAR_SQL);
            st.setInt(1, car.getId());
            st.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error while trying to delete a car.\n" + ex.getMessage());
            result = false;
        } finally {
            releaseResources();
        }
        return result;
    }

    @Override
    public void createTable() {
        try {
            con = connection.get();
            st = con.prepareStatement(CREATE_TABLE_SQL);
            st.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error while creating the car table.\n" + ex.getMessage());
        } finally {
            releaseResources();
        }
    }

    @Override
    public void dropTable() {
        try {
            con = connection.get();
            st = con.prepareStatement(DROP_TABLE_SQL);
            st.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error while trying to drop the car table.\n" + ex.getMessage());
        } finally {
            releaseResources();
        }
    }

    private void releaseResources() {
        try {
            if(st != null) st.close();
            if(con != null) con.close();
        } catch (Exception ex) {}
    }
}
