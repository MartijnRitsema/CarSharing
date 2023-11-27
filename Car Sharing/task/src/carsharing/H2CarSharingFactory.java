package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;

public class H2CarSharingFactory implements CarSharingFactory {

    private final String URL;
    private final String DRIVER = "org.h2.Driver";

    public H2CarSharingFactory(String url){
        URL = url;
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException cnfe) {
            System.out.println(DRIVER + " class not found.");
        }
    }

    Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL);
            connection.setAutoCommit(true);
        } catch (Exception ex) {
            System.out.println("Error during connection creation\n" + ex.getMessage());
            try {
                if(connection != null) connection.close();
            } catch (Exception se) {
                System.out.println(se.getMessage());
            }
        }
        return connection;
    }

    @Override
    public CompanyDao getCompanyDao() {
        return new H2CompanyDao(this::getConnection);
    }

    @Override
    public CustomerDao getCustomerDao() {
        return new H2CustomerDao(this::getConnection);
    }

    @Override
    public CarDao getCarDao() {
        return new H2CarDao(this::getConnection);
    }
}