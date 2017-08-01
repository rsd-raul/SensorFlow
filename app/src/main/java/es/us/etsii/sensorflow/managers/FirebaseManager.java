package es.us.etsii.sensorflow.managers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.us.etsii.sensorflow.domain.Event;
import es.us.etsii.sensorflow.domain.User;

public abstract class FirebaseManager {

    /**
     * Create a user in our DB when the user registers for the first totalTime
     */
    static void createUser(final User user){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Create the user only if it doesn't exists
        final DatabaseReference usersRef = database.getReference("users");

        usersRef.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.getValue() == null)
                    usersRef.child(user.getId()).setValue(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO warn user? postpone user creation?
            }
        });
    }

    /**
     * Store a new event in the Database and create the relation between the current user and such
     * event.
     *
     * @param event event to be stored.
     */
    public static void createEvent(Event event){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Create new event with unique key
        DatabaseReference eventsRef = database.getReference("events");
        String eventId = eventsRef.push().getKey();
        eventsRef.child(eventId).setValue(event);

        // Create the relation User - Event
        DatabaseReference userEventsRef = database.getReference("userEvents");
        userEventsRef.child(AuthManager.sUser.getId()).child(eventId).setValue(event.getType());
    }
}
