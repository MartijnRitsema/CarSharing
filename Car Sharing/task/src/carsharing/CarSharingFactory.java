package carsharing;

public interface CarSharingFactory {
    CompanyDao getCompanyDao();
    CustomerDao getCustomerDao();
    CarDao getCarDao();
}