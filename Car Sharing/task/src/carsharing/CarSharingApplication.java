package carsharing;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CarSharingApplication {

    private final CompanyDao COMPANY_DAO;
    private final CarDao CAR_DAO;
    private final CustomerDao CUSTOMER_DAO;

    private State state = State.MAIN_MENU;
    private Scanner scn = new Scanner(System.in);
    private Company selectedCompany;
    private Customer selectedCustomer;
    private Car selectedCar;

    private enum State {
        MAIN_MENU,
        MANAGER_MENU,
        MANAGER_COMPANY_MENU,
        MANAGER_CHOOSE_COMPANY,
        CUSTOMER_SELECT,
        CUSTOMER_MENU,
        RENT_CAR_CHOOSE_COMPANY_MENU,
        RENT_CAR_CHOOSE_CAR_MENU,
        APP_CLOSING
    }

    public CarSharingApplication (CarSharingFactory factory) {
        //get database access objects for tables
        this.COMPANY_DAO = factory.getCompanyDao();
        this.CAR_DAO = factory.getCarDao();
        this.CUSTOMER_DAO = factory.getCustomerDao();

        //drop all tables in the right order (for tests only)
        //CUSTOMER_DAO.dropTable();
        //CAR_DAO.dropTable();
        //COMPANY_DAO.dropTable();

        //create tables if they don't exist (in the right order)
        COMPANY_DAO.createTable();
        CAR_DAO.createTable();
        CUSTOMER_DAO.createTable();
    }

    public void run() {
        while(true) {
            switch(state) {
                case MAIN_MENU:
                    mainMenu();
                    break;
                case MANAGER_MENU:
                    managerMenu();
                    break;
                case MANAGER_CHOOSE_COMPANY:
                    managerChooseCompany();
                    break;
                case MANAGER_COMPANY_MENU:
                    managerCompanyMenu();
                    break;
                case CUSTOMER_SELECT:
                    customerSelect();
                    break;
                case CUSTOMER_MENU:
                    customerMenu();
                    break;
                case RENT_CAR_CHOOSE_COMPANY_MENU:
                    rentCarChooseCompanyMenu();
                    break;
                case RENT_CAR_CHOOSE_CAR_MENU:
                    rentCarChooseCarMenu();
                    break;
                case APP_CLOSING:
                    System.out.println("Bye bye...");
                    return;
                default:
                    System.out.println("Unhandled application state.\n Application closing...");
                    return;
            }
        }
    }

    private void mainMenu() {
        System.out.println("1. Log in as a manager");
        System.out.println("2. Log in as a customer");
        System.out.println("3. Create a customer");
        System.out.println("0. Exit");
        String input = scn.nextLine();
        if("0".equals(input)) {
            state = State.APP_CLOSING;
        } else {
            switch (input) {
                case "1":
                    state = State.MANAGER_MENU;
                    break;
                case "2":
                    state = State.CUSTOMER_SELECT;
                    break;
                case "3":
                    createCustomer();
                    break;
                default:
                    System.out.println("Invalid input. Try again.");
            }
        }
    }

    private void createCustomer() {
        System.out.println("Enter the customer name:");
        String name = scn.nextLine();
        if(CUSTOMER_DAO.insertCustomer(new Customer(0, name, 0))){
            System.out.println("The customer was added!");
        }
    }

    private void managerMenu() {
        System.out.println("1. Company list");
        System.out.println("2. Create a company");
        System.out.println("0. Back");
        String selection = scn.nextLine();
        if("0".equals(selection)) {
            state = State.MAIN_MENU;
        } else {
            switch (selection) {
                case "1":
                    state = State.MANAGER_CHOOSE_COMPANY;
                    break;
                case "2":
                    createCompany();
                    break;
                default:
                    System.out.println("Invalid input. Try again.");
            }
        }
    }

    private void createCompany() {
        System.out.println("Enter the company name:");
        String name = scn.nextLine();
        if(COMPANY_DAO.insertCompany(new Company(0, name))) {
            System.out.println("The company was created!");
        }
    }

    private void managerChooseCompany() {
        List<Company> cl = COMPANY_DAO.getCompanies();                  //get list of all companies
        if(cl.isEmpty()) {                                              //if there are no companies go back to menu
            System.out.println("The company list is empty!");
            state = State.MANAGER_MENU;
        } else {
            System.out.println("Choose a company:");                  //print company list and wait for input
            int order = 1;
            for(Company c : cl){
                System.out.printf("%d. %s\n", order++, c.getName());
            }
            System.out.println("0. Back");
            String selection = scn.nextLine();
            if("0".equals(selection)) {                                 //go back to manager menu
                state = State.MANAGER_MENU;
            } else {
                int selectionIndex = 0;
                try {                                                   //check if input is valid and in range
                    selectionIndex = Integer.parseInt(selection);
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid input. Try again.");
                }
                if(selectionIndex > 0 && selectionIndex <= order) {
                    selectedCompany = cl.get(selectionIndex - 1);       //store selection and proceed to next menu
                    state = State.MANAGER_COMPANY_MENU;
                } else {
                    System.out.println("Invalid input. Try again.");
                }
            }
        }
    }

    private void managerCompanyMenu() {
        System.out.printf("'%s' company\n", selectedCompany.getName());
        System.out.println("1. Car list");
        System.out.println("2. Create a car");
        System.out.println("0. Back");
        String selection = scn.nextLine();
        if("0".equals(selection)) {                                 //go back to manager menu
            state = State.MANAGER_MENU;
        } else {
            switch (selection) {
                case "1":
                    carList();
                    break;
                case "2":
                    createCar();
                    break;
                default:
                    System.out.println("Invalid input. Try again.");
            }
        }
    }

    private void carList() {
        List<Car> cl = CAR_DAO.getCars(selectedCompany.getId());
        if(cl.isEmpty()) {
            System.out.println("The car list is empty!");
        } else {
            int order = 1;
            for (Car c : cl) {
                System.out.printf("%d. %s\n", order++, c.getName());
            }
        }
    }

    private void createCar() {
        System.out.println("Enter the car name:");
        String name = scn.nextLine();
        if(CAR_DAO.insertCar(new Car(0, name, selectedCompany.getId()))) {
            System.out.println("The car was added!");
        }
    }

    private void customerSelect() {
        List<Customer> cl = CUSTOMER_DAO.getCustomers();
        if(cl.isEmpty()){
            System.out.println("The customer list is empty!");
            state = State.MAIN_MENU;
        } else {
            System.out.println("Customer list:");
            int order = 1;
            for (Customer c : cl) {
                System.out.printf("%d. %s\n", order++, c.getName());
            }
            System.out.println("0. Back");
            String selection = scn.nextLine();
            if ("0".equals(selection)) {                                 //go back to manager menu
                state = State.MAIN_MENU;
            } else {
                int selectionIndex = 0;
                try {                                                   //check if input is valid and in range
                    selectionIndex = Integer.parseInt(selection);
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid input. Try again.");
                }
                if (selectionIndex > 0 && selectionIndex <= order) {
                    selectedCustomer = cl.get(selectionIndex - 1);       //store selection and proceed to next menu
                    state = State.CUSTOMER_MENU;
                } else {
                    System.out.println("Invalid input. Try again.");
                }
            }
        }
    }

    private void customerMenu() {
        System.out.println("1. Rent a car");
        System.out.println("2. Return a rented car");
        System.out.println("3. My rented car");
        System.out.println("0. Back");
        String selection = scn.nextLine();
        if ("0".equals(selection)) {                                 //go back to manager menu
            state = State.MAIN_MENU;
        } else {
            switch(selection){
                case "1":
                    state = State.RENT_CAR_CHOOSE_COMPANY_MENU;
                    break;
                case "2":
                    returnCar();
                    break;
                case "3":
                    myRentedCar();
                    break;
                default:
                    System.out.println("Invalid input. Try again.");
            }
        }
    }

    private void returnCar() {
        if(selectedCustomer.getRentedCarId() == null) {
            System.out.println("You didn't rent a car!");
        } else {
            selectedCustomer.setRentedCarId(null);
            if(CUSTOMER_DAO.updateCustomer(selectedCustomer)) {
                System.out.println("You've returned a rented car!");
            }
        }
    }

    private void myRentedCar() {
        if(selectedCustomer.getRentedCarId() == null) {
            System.out.println("You didn't rent a car!");
        } else {
            Car rentedCar = CAR_DAO.getCar(selectedCustomer.getRentedCarId());
            Company carCompany = COMPANY_DAO.getCompany(rentedCar.getCompanyId());
            System.out.println("Your rented car:\n" +
                    rentedCar.getName() +
                    "\nCompany:\n" +
                    carCompany.getName());
        }
    }

    private void rentCarChooseCompanyMenu() {
        if(selectedCustomer.getRentedCarId() != null) {
            System.out.println("You've already rented a car!");
            state = State.CUSTOMER_MENU;
            return;
        }
        List<Company> cl = COMPANY_DAO.getCompanies();                  //get list of all companies
        if(cl.isEmpty()) {                                              //if there are no companies go back to menu
            System.out.println("The company list is empty!");
            state = State.CUSTOMER_MENU;
        } else {
            System.out.println("Choose a company:\n");                  //print company list and wait for input
            int order = 1;
            for(Company c : cl){
                System.out.printf("%d. %s\n", order++, c.getName());
            }
            System.out.println("0. Back");
            String selection = scn.nextLine();
            if("0".equals(selection)) {                                 //go back to manager menu
                state = State.CUSTOMER_MENU;
            } else {
                int selectionIndex = 0;
                try {                                                   //check if input is valid and in range
                    selectionIndex = Integer.parseInt(selection);
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid input. Try again.");
                }
                if(selectionIndex > 0 && selectionIndex <= order) {
                    selectedCompany = cl.get(selectionIndex - 1);       //store selection and proceed to next menu
                    state = State.RENT_CAR_CHOOSE_CAR_MENU;
                } else {
                    System.out.println("Invalid input. Try again.");
                }
            }
        }
    }

    private void rentCarChooseCarMenu() {
        List<Car> clAll = CAR_DAO.getCars(selectedCompany.getId());
        List<Integer> rented = CUSTOMER_DAO.getCustomers()
                .stream()
                .filter(c -> c.getRentedCarId() != null)
                .mapToInt(Customer::getRentedCarId)
                .boxed()
                .collect(Collectors.toList());
        List<Car> cl = clAll.stream().filter(c -> !rented.contains(c.getId())).collect(Collectors.toList());
        if(cl.isEmpty()) {                                              //if there are no companies go back to menu
            System.out.println("The car list is empty!");
            state = State.RENT_CAR_CHOOSE_COMPANY_MENU;
        } else {
            System.out.println("Choose a car:\n");                  //print company list and wait for input
            int order = 1;
            for(Car c : cl){
                System.out.printf("%d. %s\n", order++, c.getName());
            }
            System.out.println("0. Back");
            String selection = scn.nextLine();
            if("0".equals(selection)) {                                 //go back to manager menu
                state = State.RENT_CAR_CHOOSE_COMPANY_MENU;
            } else {
                int selectionIndex = 0;
                try {                                                   //check if input is valid and in range
                    selectionIndex = Integer.parseInt(selection);
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid input. Try again.");
                }
                if(selectionIndex > 0 && selectionIndex <= order) {
                    selectedCar = cl.get(selectionIndex - 1);
                    selectedCustomer.setRentedCarId(selectedCar.getId());
                    CAR_DAO.updateCar(selectedCar);
                    if(CUSTOMER_DAO.updateCustomer(selectedCustomer)) {
                        System.out.println("You rented '" + selectedCar.getName() + "'");
                    }
                    state = State.CUSTOMER_MENU;
                } else {
                    System.out.println("Invalid input. Try again.");
                }
            }
        }
    }
}