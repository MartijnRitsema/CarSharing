package carsharing;

import java.util.List;

public interface CarDao extends GeneralDao{
    public List<Car> getCars();
    public List<Car> getCars(int companyId);
    public Car getCar(int id);
    public boolean updateCar(Car car);
    public boolean insertCar(Car car);
    public boolean deleteCar(Car car);
}