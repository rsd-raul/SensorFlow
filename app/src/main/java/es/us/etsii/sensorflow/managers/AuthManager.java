package es.us.etsii.sensorflow.managers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import javax.inject.Inject;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.domain.User;
import es.us.etsii.sensorflow.utils.Constants;

public class AuthManager implements OnCompleteListener<AuthResult>,
                                    GoogleApiClient.OnConnectionFailedListener,
                                    ResultCallback<Status> {

    // --------------------------- VALUES ----------------------------

//    private static final String TAG = "AuthManager";

    // ------------------------- ATTRIBUTES --------------------------

    public static User sUser = null;
    private FirebaseAuth mFirebaseAuth;
    private AppCompatActivity mActivity;
    private GoogleApiClient mGoogleApiClient;

    // ------------------------- CONSTRUCTOR -------------------------

    @Inject
    public AuthManager(FirebaseAuth firebaseAuth) {
        // Configure Google Sign In
        mFirebaseAuth = firebaseAuth;
    }

    public void init(AppCompatActivity activity){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, Constants.GOOGLE_AUTH);

        mActivity = activity;
    }

    // -------------------------- USE CASES --------------------------

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
        Toast.makeText(mActivity, "PROBLEMS", Toast.LENGTH_SHORT).show();
    }

    public void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(this);
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
            Toast.makeText(mActivity, "CONNECTED to FIREBASE", Toast.LENGTH_SHORT).show();
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

    /**
     * Google Api Client logout completed
     */
    @Override
    public void onResult(@NonNull Status status) {
        // TODO offer to delete all the user data? modify interface? do nothing?
    }
}
