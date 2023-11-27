package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class H2CompanyDao implements CompanyDao {

    private Supplier<Connection> connection;
    private Connection con;
    private PreparedStatement st;

    private final String GET_COMPANIES_SQL = "SELECT * FROM COMPANY";

    private final String GET_COMPANY_SQL = "SELECT * FROM COMPANY WHERE ID = ?";

    private final String UPDATE_COMPANY_SQL = "UPDATE COMPANY SET NAME = ? WHERE ID = ?";

    private final String INSERT_COMPANY_SQL = "INSERT INTO COMPANY (NAME) VALUES (?);";

    private final String DELETE_COMPANY_SQL = "DELETE FROM COMPANY WHERE ID = ?";

    private final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS COMPANY " +
            "(ID INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "NAME VARCHAR UNIQUE NOT NULL);";

    private final String DROP_TABLE_SQL = "DROP TABLE COMPANY";

    public H2CompanyDao(Supplier<Connection> connectionSupplier) {
        this.connection = connectionSupplier;
    }

    @Override
    public List<Company> getCompanies() {
        List<Company> cl = new ArrayList<>();
        try {
            con = connection.get();
            st = con.prepareStatement(GET_COMPANIES_SQL);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                cl.add(new Company(rs.getInt(1), rs.getNString(2)));
            }
        } catch (Exception ex) {
            System.out.println("Error during a company list selection.\n" + ex.getMessage());
        } finally {
            releaseResources();
        }
        return cl;
    }

    @Override
    public Company getCompany(int id) {
        Company company = null;
        try {
            con = connection.get();
            st = con.prepareStatement(GET_COMPANY_SQL);
            st.setInt(1,id);
            ResultSet rs = st.executeQuery();
            if(rs.first()) company = new Company(rs.getInt(1), rs.getNString(2));
        } catch (Exception ex) {
            System.out.println("Error during a company search.\n"+ ex.getMessage());
        } finally {
            releaseResources();
        }
        return company;
    }

    @Override
    public boolean updateCompany(Company company) {
        boolean result = true;
        try {
            con = connection.get();
            st = con.prepareStatement(UPDATE_COMPANY_SQL);
            st.setString(1, company.getName());
            st.setInt(2, company.getId());
            st.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error during a company update.\n" + ex.getMessage());
            result = false;
        } finally {
            releaseResources();
        }
        return result;
    }

    @Override
    public boolean insertCompany(Company company) {
        boolean result = true;
        try {
            con = connection.get();
            st = con.prepareStatement(INSERT_COMPANY_SQL);
            st.setString(1, company.getName());
            st.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error while trying to add a company.\n" + ex.getMessage());
            result = false;
        } finally {
            releaseResources();
        }
        return result;
    }

    @Override
    public boolean deleteCompany(Company company) {
        boolean result = true;
        try {
            con = connection.get();
            st = con.prepareStatement(DELETE_COMPANY_SQL);
            st.setInt(1, company.getId());
            st.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error while trying to delete a company.\n" + ex.getMessage());
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
            System.out.println("Error while creating the company table.\n" + ex.getMessage());
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
            System.out.println("Error while trying to drop the company table.\n" + ex.getMessage());
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
