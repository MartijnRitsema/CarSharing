package carsharing;

import java.sql.Connection;

interface GeneralDao {
    void createTable();
    void dropTable();
}