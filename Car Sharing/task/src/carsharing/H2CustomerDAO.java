package carsharing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class H2CustomerDao implements CustomerDao{

    private Supplier<Connection> connection;
    private Connection con;
    private PreparedStatement st;

    private final String GET_CUSTOMERS_SQL = "SELECT * FROM CUSTOMER";

    private final String GET_CUSTOMER_SQL = "SELECT * FROM CUSTOMER WHERE ID = ?";

    private final String UPDATE_CUSTOMER_SQL = "UPDATE CUSTOMER SET NAME = ?, " +
            "RENTED_CAR_ID = ? WHERE ID = ?";

    private final String INSERT_CUSTOMER_SQL = "INSERT INTO CUSTOMER (NAME) VALUES (?)";

    private final String DELETE_CUSTOMER_SQL = "DELETE FROM CUSTOMER WHERE ID = ?";

    private final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS CUSTOMER" +
            "(ID INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "NAME VARCHAR UNIQUE NOT NULL," +
            "RENTED_CAR_ID INTEGER DEFAULT NULL," +
            "FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID))";

    private final String DROP_TABLE_SQL = "DROP TABLE CUSTOMER";

    public H2CustomerDao(Supplier<Connection> connectionSupplier) {
        this.connection = connectionSupplier;
    }

    @Override
    public List<Customer> getCustomers() {
        List<Customer> cl = new ArrayList<>();
        try {
            con = connection.get();
            st = con.prepareStatement(GET_CUSTOMERS_SQL);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                int rsId = rs.getInt(1);
                String rsName = rs.getNString(2);
                Integer rsCarId = rs.getInt(3);
                if(rs.wasNull()) rsCarId = null;
                cl.add(new Customer(rsId, rsName, rsCarId));
            }
        } catch (Exception ex) {
            System.out.println("Error during a customer list selection.\n" + ex.getMessage());
        } finally {
            releaseResources();
        }
        return cl;
    }

    @Override
    public Customer getCustomer(int id) {
        Customer customer = null;
        try {
            con = connection.get();
            st = con.prepareStatement(GET_CUSTOMER_SQL);
            ResultSet rs = st.executeQuery();

            if(rs.first()) {
                int rsId = rs.getInt(1);
                String rsName = rs.getNString(2);
                Integer rsCarId = rs.getInt(3);
                if(rs.wasNull()) rsCarId = null;
                customer = new Customer(rsId, rsName, rsCarId);
            }
        } catch (Exception ex) {
            System.out.println("Error during a customer search.\n"+ ex.getMessage());
        } finally {
            releaseResources();
        }
        return customer;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        boolean result = true;
        try {
            con = connection.get();
            st = con.prepareStatement(UPDATE_CUSTOMER_SQL);
            st.setString(1, customer.getName());
            if(customer.getRentedCarId() == null){
                st.setNull(2, Types.INTEGER);
            } else {
                st.setInt(2,customer.getRentedCarId());
            }
            st.setInt(3, customer.getId());
            st.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error during a customer update.\n" + ex.getMessage());
            result = false;
        } finally {
            releaseResources();
        }
        return result;
    }

    @Override
    public boolean insertCustomer(Customer customer) {
        boolean result = true;
        try {
            con = connection.get();
            st = con.prepareStatement(INSERT_CUSTOMER_SQL);
            st.setString(1, customer.getName());
            st.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error while trying to add a customer.\n" + ex.getMessage());
            result = false;
        } finally {
            releaseResources();
        }
        return result;
    }

    @Override
    public boolean deleteCustomer(Customer customer) {
        boolean result = true;
        try {
            con = connection.get();
            st = con.prepareStatement(DELETE_CUSTOMER_SQL);
            st.setInt(1, customer.getId());
            st.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error while trying to delete a customer.\n" + ex.getMessage());
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
            System.out.println("Error while creating the customer table.\n" + ex.getMessage());
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
            System.out.println("Error while trying to drop the customer table.\n" + ex.getMessage());
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