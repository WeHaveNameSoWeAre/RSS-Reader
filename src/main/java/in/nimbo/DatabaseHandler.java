package in.nimbo;

import in.nimbo.Model.Channel;
import in.nimbo.Model.Item;

public class DatabaseHandler {
    private static DatabaseHandler instance = null;


    private DatabaseHandler() {

    }

    public static DatabaseHandler getInstance() {
        if (instance == null)
            instance = new DatabaseHandler();

        return instance;
    }

    public int insertChannel(Channel channel){
        // TODO: 7/11/18
        return 0;
    }
    
    public boolean checkItemExists(Item item){
        // TODO: 7/11/18
        return false;
    }

}
