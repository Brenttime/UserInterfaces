package setup;

import java.util.Properties;
import models.DBProps;

public class CreateTables {

  public static void main(String[] args) throws Exception {
    Properties props = DBProps.getProps();
    System.out.format("\n---- database = %s\n", DBProps.which);
    
    Helper.createTables(props);
  }
}
