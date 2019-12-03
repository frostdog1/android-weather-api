package ac.rgu.coursework.model;

/**
 * Model for locations and location IDs
 */
public class LocationObject {

    private final int id;
    private final String location;

    public LocationObject(int id, String location) {
        this.id = id;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }
}
