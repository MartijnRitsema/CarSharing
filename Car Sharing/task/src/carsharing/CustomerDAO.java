package carsharing;

import java.util.List;

interface CustomerDao extends GeneralDao {
    public List<Customer> getCustomers();
    public Customer getCustomer(int id);
    public boolean updateCustomer(Customer customer);
    public boolean insertCustomer(Customer customer);
    public boolean deleteCustomer(Customer customer);
}