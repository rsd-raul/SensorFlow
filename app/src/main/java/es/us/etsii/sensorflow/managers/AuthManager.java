package es.us.etsii.sensorflow.managers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import javax.inject.Inject;
import javax.inject.Singleton;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.domain.User;
import es.us.etsii.sensorflow.utils.Constants;
import es.us.etsii.sensorflow.utils.DialogUtils;
import es.us.etsii.sensorflow.views.MainActivity;

@Singleton
public class AuthManager implements OnCompleteListener<AuthResult>,
                                    GoogleApiClient.OnConnectionFailedListener {

    // --------------------------- VALUES ----------------------------

    private int SAFE = 1, UNSAFE = -1;

    // ------------------------- ATTRIBUTES --------------------------

    static User sUser = null;
    private FirebaseAuth mFirebaseAuth;
    private MainActivity mActivity;
    private GoogleApiClient mGoogleApiClient = null;
    private int CURRENT_STATUS = SAFE;

    // ------------------------- CONSTRUCTOR -------------------------

    @Inject
    AuthManager(FirebaseAuth firebaseAuth) {
        mFirebaseAuth = firebaseAuth;
    }

    // -------------------------- USE CASES --------------------------

    public static String getUserId(){
        return sUser != null ? sUser.getId() : "anonymous";
    }

    public void startGoogleApiClient(MainActivity activity){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApiIfAvailable(Wearable.API)
                .build();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void loginFirebase(MainActivity activity){
        if(CURRENT_STATUS == UNSAFE) {
            Toast.makeText(activity, R.string.problems_try_again, Toast.LENGTH_SHORT).show();
            return;
        }
        CURRENT_STATUS = UNSAFE;

        if(mGoogleApiClient == null)
            startGoogleApiClient(activity);

        FirebaseApp.initializeApp(activity);

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, Constants.GOOGLE_AUTH);

        mActivity = activity;
    }

    public void handleSignInResult(Intent data){
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        GoogleSignInAccount acct = result.getSignInAccount();

        if (result.isSuccess() && acct != null ) {
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(mActivity, this);
        } else
            handleConnectionFail();
    }

    private void handleConnectionFail() {
        Toast.makeText(mActivity, R.string.problems_try_again, Toast.LENGTH_SHORT).show();
        CURRENT_STATUS = SAFE;
    }

    public void signOut(){
        if(CURRENT_STATUS == UNSAFE) {
            Toast.makeText(mActivity, R.string.problems_try_again, Toast.LENGTH_SHORT).show();
            return;
        }
        CURRENT_STATUS = UNSAFE;

        mGoogleApiClient.stopAutoManage(mActivity);
        mGoogleApiClient.disconnect();
        mFirebaseAuth.signOut();
        sUser = null;

        CURRENT_STATUS = SAFE;

        DialogUtils.logoutDialog(mActivity);
    }

    public boolean isLoggedFirebase() {
        return sUser != null;
    }

    // -------------------------- LISTENER ---------------------------

    /**
     * Firebase connection completed
     */
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        if (task.isSuccessful() && user != null) {
            sUser = new User(user.getUid(), user.getDisplayName(), user.getEmail());
            FirebaseManager.createUser(sUser);
            Toast.makeText(mActivity, R.string.connected_firebase, Toast.LENGTH_SHORT).show();
            CURRENT_STATUS = SAFE;
        } else
            handleConnectionFail();
    }

    /**
     * Google Api Client login failure
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        handleConnectionFail();
    }
}
