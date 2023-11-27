package carsharing;

import java.sql.Connection;
import java.util.List;

interface CompanyDao extends GeneralDao {
    public List<Company> getCompanies();
    public Company getCompany(int id);
    public boolean updateCompany(Company company);
    public boolean insertCompany(Company company);
    public boolean deleteCompany(Company company);
}