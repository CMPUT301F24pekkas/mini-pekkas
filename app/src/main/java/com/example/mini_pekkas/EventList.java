import android.graphics.Bitmap;


import com.example.mini_pekkas.AppUser;
import com.google.firebase.firestore.auth.User;

import java.util.Date;


import java.util.List;

public class Event {
    // Attributes
    private String id;
    private String title;
    private String name;
    private AppUser eventHost;
    private String description;
    private String startDate;
    private String endDate;
    private int price;
    private String facility;
    private double facilityGeoLat;
    private double facilityGeoLong;
    private int limit;
    private UserList attendees;
    private UserList waitlist;
    private String checkinID;
    private String checkinRq;
    private boolean geo;

    // Constructors
    public Event(String id, String name, User eventHost, String description, String startDate, String endDate, int price,
                 String facility, double facilityGeoLat, double facilityGeoLong, int limit, UserList attendees,
                 UserList waitlist, String checkinID, String checkinRq, boolean geo) {
        this.id = id;
        this.name = name;
        this.eventHost = eventHost;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.facility = facility;
        this.facilityGeoLat = facilityGeoLat;
        this.facilityGeoLong = facilityGeoLong;
        this.limit = limit;
        this.attendees = attendees;
        this.waitlist = waitlist;
        this.checkinID = checkinID;
        this.checkinRq = checkinRq;
        this.geo = geo;
    }


    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public User getEventHost() { return eventHost; }
    public void setEventHost(User eventHost) { this.eventHost = eventHost; }


    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getFacility() { return facility; }
    public void setFacility(String facility) { this.facility = facility; }

    public double getFacilityGeoLat() { return facilityGeoLat; }
    public void setFacilityGeoLat(double facilityGeoLat) { this.facilityGeoLat = facilityGeoLat; }

    public double getFacilityGeoLong() { return facilityGeoLong; }
    public void setFacilityGeoLong(double facilityGeoLong) { this.facilityGeoLong = facilityGeoLong; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public UserList getAttendees() { return attendees; }
    public void setAttendees(UserList attendees) { this.attendees = attendees; }

    public UserList getWaitlist() { return waitlist; }
    public void setWaitlist(UserList waitlist) { this.waitlist = waitlist; }

    public String getCheckinID() { return checkinID; }
    public void setCheckinID(String checkinID) { this.checkinID = checkinID; }

    public String getCheckinRq() { return checkinRq; }
    public void setCheckinRq(String checkinRq) { this.checkinRq = checkinRq; }

    public boolean isGeo() { return geo; }
    public void setGeo(boolean geo) { this.geo = geo; }

    


    public Bitmap getQRCodeFromID(int width, int height) {
        return null;
    }
}
