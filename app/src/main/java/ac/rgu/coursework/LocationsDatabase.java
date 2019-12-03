package ac.rgu.coursework;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ac.rgu.coursework.model.LocationObject;

/**
 * Database to store user locations
 */
public class LocationsDatabase extends SQLiteOpenHelper {

    // Database name
    private static final String dbName = "LOCATIONS_DATABASE";

    // Version
    private static final int dbVersion = 1;

    // Table name
    private static final String dbTable = "main";

    // Database columns
    private static final String KEY_ID = "id";
    private static final String KEY_LOC = "location";

    public LocationsDatabase() {
        super(OneStormApplication.getContext(), dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Database of locations and unique integer ID
        String CREATE_LOCATION_TABLE = "CREATE TABLE " + dbTable + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_LOC + " TEXT"
                + ")";
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Re-create database if it was upgraded
        db.execSQL("DROP TABLE IF EXISTS " + dbTable);
        onCreate(db);
    }

    /**
     * Remove locations from the database
     *
     * @param id unique ID of city location
     */
    public void removeLocation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + dbTable + " WHERE " + KEY_ID + "= '" + id + "'");
        db.close();
    }

    /**
     * Save locations to the database
     *
     * @param id       ID of location
     * @param location Location string
     */
    public void saveLocation(int id, String location) {
        SQLiteDatabase db = this.getWritableDatabase();

        String COLUMNS = " (" + KEY_ID + ", " + KEY_LOC + ")";
        String VALUES = " VALUES('" + id + "','" + location + "')";
        String query = "INSERT OR REPLACE INTO "
                + dbTable
                + COLUMNS
                + VALUES;

        db.execSQL(query);
        db.close();
    }

    /**
     * Get all locations and IDs from database
     *
     * @return locations and IDs
     */
    public List<LocationObject> getLocations() {
        List<LocationObject> locationList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + dbTable;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to the list
        if (cursor.moveToFirst()) {
            do {
                int id = (cursor.getInt(0));
                String location = (cursor.getString(1));

                // Use id and location from row in db to make LocationObject
                LocationObject locObj = new LocationObject(id, location);

                // Add it to the main list of locations to be returned at the end
                locationList.add(locObj);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return locationList;
    }
}
