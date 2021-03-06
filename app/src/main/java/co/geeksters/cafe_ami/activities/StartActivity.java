package co.geeksters.cafe_ami.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import co.geeksters.cafe_ami.R;
import co.geeksters.cafe_ami.events.failure.ConnectionFailureEvent;
import co.geeksters.cafe_ami.events.failure.UnauthorizedFailureEvent;
import co.geeksters.cafe_ami.events.success.SaveMemberEvent;
import co.geeksters.cafe_ami.global.BaseApplication;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.services.MemberService;

import static co.geeksters.cafe_ami.global.helpers.ParseHelpers.createJsonElementFromString;

@EActivity(R.layout.activity_start)
public class StartActivity extends Activity {
    private SharedPreferences.Editor editor;


    @ViewById
    pl.droidsonroids.gif.GifImageView loadingGif;

    @ViewById
    TextView welcome;


    @AfterViews
    public void busRegistration(){
        BaseApplication.register(this);
    }
    public static GoogleCloudMessaging gcm;
    public static String regid;
    public static Context context;

    @AfterViews
    public void setPreferencesEditorAndVerifyLogin(){
        SharedPreferences preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        editor = preferences.edit();

        String accessToken = preferences.getString("access_token","").replace("\"", "");
        Typeface typeFace=Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        welcome.setTypeface(typeFace);
        loadingGif.setVisibility(View.VISIBLE);


        if (!accessToken.equals("")) {
                Member currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
                MemberService memberService = new MemberService(accessToken);
                memberService.updateMember(currentMember.id, currentMember);
            }
        else{

            loadingGif.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, LoginActivity_.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();


        }

    }


    @Override
    public void onStart() {
        super.onStart();
        if(!BaseApplication.isRegistered(this))
            BaseApplication.register(this);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Subscribe
    public void onConnectionFailureEvent(ConnectionFailureEvent event)
    {

        loadingGif.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, LoginActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();


    }

    @Subscribe
    public void onUpdatefailureEvent(UnauthorizedFailureEvent event)
    {
        loadingGif.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, LoginActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();


    }


    @Subscribe
    public void onUpdateEvent(SaveMemberEvent event){
        loadingGif.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, GlobalMenuActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public void onDestroy () {
        super.onDestroy();
        if(BaseApplication.isRegistered(this))
        BaseApplication.unregister(this);
    }
}



