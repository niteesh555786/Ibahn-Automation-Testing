package bizbrolly.svarochiapp.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Arun on 21/02/17.
 */

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "SvarochiDatabase";

    public static final int VERSION = 1;
}
