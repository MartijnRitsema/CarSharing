package carsharing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    static String jdbcUrl = "jdbc:h2:./src/carsharing/db/";
    static String databaseFileName = "H2databaseFile2";

    public static void main(String[] args) {
        //setup
        List<String> listOfArgs = Arrays.stream(args).collect(Collectors.toList());
        int index;
        if((index = listOfArgs.indexOf("-databaseFileName")) != -1){
            databaseFileName = listOfArgs.get(index + 1);
        }
        jdbcUrl += databaseFileName;

        CarSharingFactory carSharingFactory = new H2CarSharingFactory(jdbcUrl);
        CarSharingApplication app = new CarSharingApplication(carSharingFactory);
        app.run();
    }
}