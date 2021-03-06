package co.geeksters.cafe_ami.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.geeksters.cafe_ami.R;
import co.geeksters.cafe_ami.activities.GlobalMenuActivity;
import co.geeksters.cafe_ami.events.success.ChangeToListEvent;
import co.geeksters.cafe_ami.events.success.ResumeRadarEvent;
import co.geeksters.cafe_ami.events.success.UpdateMemberLocationEvent;
import co.geeksters.cafe_ami.global.BaseApplication;
import co.geeksters.cafe_ami.global.GlobalVariables;
import co.geeksters.cafe_ami.global.helpers.GPSTrackerHelpers;
import co.geeksters.cafe_ami.global.helpers.GeneralHelpers;
import co.geeksters.cafe_ami.global.helpers.ParseHelpers;
import co.geeksters.cafe_ami.global.helpers.ViewHelpers;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.services.MemberService;

import static co.geeksters.cafe_ami.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_people_finder)
public class PeopleFinderFragment extends Fragment {

    @ViewById(R.id.layout_switch)
    LinearLayout switchRadarActivation;

    @ViewById(R.id.radar_activate)
            TextView radarActivate;
    @ViewById(R.id.radar_no_activate)
            TextView radarNoActivate;

    @ViewById(R.id.radar_Button)
    Button radarButton;
    @ViewById(R.id.list_Button)
    Button listButton;
    @ViewById(R.id.radar_buttonlight)
    LinearLayout radarButtonLight;
    @ViewById(R.id.list_buttonlight)
    LinearLayout listButtonLight;

    public Boolean radarSelected = false;
    public  Boolean listSelected = false;

    public Boolean firstTime = true;

    boolean radarChecked = false;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String accessToken;
    Member currentMember;


    @AfterViews
    public void busRegistration(){
        BaseApplication.register(this);

        if(PreferenceManager.getDefaultSharedPreferences(GlobalVariables.activity).getBoolean("visit_info_search_members",true)) {


            PreferenceManager.getDefaultSharedPreferences(GlobalVariables.activity).edit().putBoolean("visit_info_search_members", false).commit();
            LayoutInflater inflater = GlobalVariables.activity.getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.pop_up_info_opportunity, null);

            ImageView cancelImage = (ImageView) dialoglayout.findViewById(R.id.cancel_popup);
            TextView infoText = (TextView) dialoglayout.findViewById(R.id.popup_info_text);

            infoText.setText("Radar lets you find Onsies close by! (why do we need to have the activate / de-activate instructions twice?)");

            Typeface typeFace = Typeface.createFromAsset(GlobalVariables.activity.getAssets(), "fonts/OpenSans-Regular.ttf");
            infoText.setTypeface(typeFace);

            AlertDialog.Builder builder = new AlertDialog.Builder(GlobalVariables.activity);
            builder.setView(dialoglayout);
            builder.setCancelable(true);
            final AlertDialog ald = builder.show();
            ald.setCancelable(true);

            cancelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ald.dismiss();
                }
            });

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if(!BaseApplication.isRegistered(this))
            BaseApplication.register(this);
        GlobalVariables.inRadarFragement = true;
        GlobalVariables.menuPart = 2;
        GlobalVariables.menuDeep = 0;
        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

            GlobalVariables.inRadarFragement = false;
            GlobalVariables.inMyProfileFragment = false;
            GlobalVariables.inMyTodosFragment = false;
            GlobalVariables.inMarketPlaceFragment = false;
        GlobalVariables.inRadarFragement = true;
        GlobalVariables.needReturnButton = false;
        ((GlobalMenuActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.title_find_fragment));
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.unregister(this);
        GlobalVariables.inRadarFragement = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
        GlobalVariables.inRadarFragement = true;
        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);
        return null;
    }

    public void updateLocationAndVisibility(){
        if (!GeneralHelpers.isGPSEnabled(getActivity())) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.exit_pop_up, null);
            TextView infoTitle = (TextView) dialoglayout.findViewById(R.id.infoTitle);
            TextView infotext = (TextView) dialoglayout.findViewById(R.id.infoText);
            ImageView infoimage = (ImageView) dialoglayout.findViewById(R.id.infoImage);
            Button no = (Button)dialoglayout.findViewById(R.id.cancel_image);
            Button yes = (Button)dialoglayout.findViewById(R.id.quite_image);

            Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
            infoTitle.setTypeface(null,typeFace.BOLD);
            infotext.setTypeface(null, typeFace.BOLD);
            no.setText("NO");
            yes.setText("Yes");
            infoTitle.setText("");
            infoTitle.setVisibility(View.GONE);
            infotext.setText("Your GPS seems to be disabled, do you want to enable it?");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(dialoglayout);
            builder.setCancelable(true);
            final AlertDialog ald =builder.show();
            ald.setCancelable(true);

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ald.dismiss();
                    verifyGpsActivation();
                }
            });
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    ald.dismiss();
                }
            });
        } else {
            verifyGpsActivation();
        }
    }

    public void verifyGpsActivation() {
        GPSTrackerHelpers gps = new GPSTrackerHelpers(getActivity());

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        Member updatedMember = currentMember;

        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            ((GlobalMenuActivity) getActivity()).setActionBarIconVisibility(true);
            radarChecked = true;
            radarActivate.setTextColor(Color.parseColor("#FFFFFF"));
            radarActivate.setBackgroundColor(Color.parseColor("#7cbdbd"));
            radarNoActivate.setTextColor(Color.parseColor("#a4a4a4"));
            radarNoActivate.setBackgroundColor(Color.parseColor("#FFFFFF"));

            // update longitude latitude
            updatedMember.longitude = (float) longitude;
            updatedMember.latitude  = (float) latitude;
            if (GeneralHelpers.isInternetAvailable(getActivity())) {
                GlobalVariables.updatePositionFromRadar = true;
                MemberService memberService = new MemberService(accessToken);
                updatedMember.radarVisibility = true;
                memberService.updateMember(currentMember.id, updatedMember);
                GlobalVariables.updatePosition = true;
                GlobalVariables.isMenuOnPosition = true;
                GlobalVariables.MENU_POSITION = 1;
            } else {
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection), true);
            }
        } else {

            ((GlobalMenuActivity) getActivity()).setActionBarIconVisibility(false);
            radarChecked = false;
            radarNoActivate.setTextColor(Color.parseColor("#FFFFFF"));
            radarNoActivate.setBackgroundColor(Color.parseColor("#7cbdbd"));
            radarActivate.setTextColor(Color.parseColor("#a4a4a4"));
            radarActivate.setBackgroundColor(Color.parseColor("#FFFFFF"));

            //  verifyGpsActivation();

        }
    }

    public void updateVisibility() {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        Member updatedMember = currentMember;

        if (GeneralHelpers.isInternetAvailable(getActivity())) {
            GlobalVariables.updatePositionFromRadar = true;
            MemberService memberService = new MemberService(accessToken);
            updatedMember.radarVisibility = false;
            memberService.updateMember(currentMember.id, updatedMember);
            GlobalVariables.updatePosition = true;
            GlobalVariables.isMenuOnPosition = true;
            GlobalVariables.MENU_POSITION = 1;
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection), true);
        }
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        GlobalVariables.inRadarFragement = false;
    }

    public void onResume() {
        super.onResume();
        if(!firstTime) {
            if (currentMember.radarVisibility) {
                updateLocationAndVisibility();
                ((GlobalMenuActivity) getActivity()).setActionBarIconVisibility(true);
            } else
                ((GlobalMenuActivity) getActivity()).setActionBarIconVisibility(false);
        }
        firstTime = false;
    }
    @AfterViews
    public void switchTreatments() {
        if (currentMember.radarVisibility)
        {
            radarChecked = true;
            ((GlobalMenuActivity) getActivity()).setActionBarIconVisibility(true);
            radarActivate.setTextColor(Color.parseColor("#FFFFFF"));
            radarActivate.setBackgroundColor(Color.parseColor("#7cbdbd"));
            radarNoActivate.setTextColor(Color.parseColor("#a4a4a4"));
            radarNoActivate.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else
        {
            ((GlobalMenuActivity) getActivity()).setActionBarIconVisibility(false);
            radarChecked = false;
            radarNoActivate.setTextColor(Color.parseColor("#FFFFFF"));
            radarNoActivate.setBackgroundColor(Color.parseColor("#7cbdbd"));
            radarActivate.setTextColor(Color.parseColor("#a4a4a4"));
            radarActivate.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        switchRadarActivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(PreferenceManager.getDefaultSharedPreferences(GlobalVariables.activity).getBoolean("radar_switch_checked",true)) {


                PreferenceManager.getDefaultSharedPreferences(GlobalVariables.activity).edit().putBoolean("radar_switch_checked", false).commit();
                LayoutInflater inflater = GlobalVariables.activity.getLayoutInflater();
                final View dialoglayout = inflater.inflate(R.layout.pop_up_info_opportunity, null);

                ImageView cancelImage = (ImageView) dialoglayout.findViewById(R.id.cancel_popup);
                TextView infoText = (TextView) dialoglayout.findViewById(R.id.popup_info_text);

                infoText.setText("Activate Radar to see others and be seen."+"\n De-activate Radar to be invisible.");

                Typeface typeFace = Typeface.createFromAsset(GlobalVariables.activity.getAssets(), "fonts/OpenSans-Regular.ttf");
                infoText.setTypeface(typeFace);

                AlertDialog.Builder builder = new AlertDialog.Builder(GlobalVariables.activity);
                builder.setView(dialoglayout);
                builder.setCancelable(true);
                final AlertDialog ald = builder.show();
                ald.setCancelable(true);

                cancelImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ald.dismiss();
                    }
                });

                } else {


                if (radarChecked) {
                    radarChecked = false;
                    ((GlobalMenuActivity) getActivity()).setActionBarIconVisibility(false);
                    radarNoActivate.setTextColor(Color.parseColor("#FFFFFF"));
                    radarNoActivate.setBackgroundColor(Color.parseColor("#7cbdbd"));
                    radarActivate.setTextColor(Color.parseColor("#a4a4a4"));
                    radarActivate.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    updateVisibility();


                } else {
                    radarChecked = true;
                    ((GlobalMenuActivity) getActivity()).setActionBarIconVisibility(true);
                    radarActivate.setTextColor(Color.parseColor("#FFFFFF"));
                    radarActivate.setBackgroundColor(Color.parseColor("#7cbdbd"));
                    radarNoActivate.setTextColor(Color.parseColor("#a4a4a4"));
                    radarNoActivate.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    updateLocationAndVisibility();

                }

            }
            }
        });
    }


    @AfterViews
    public void tabSetting(){

        android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
        PeopleFinderRadarFragment_ allFragment = (PeopleFinderRadarFragment_) fragmentManager.findFragmentByTag("radar");
        PeopleFinderListFragment_ meFragment = (PeopleFinderListFragment_) fragmentManager.findFragmentByTag("finder_list");
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        /** Detaches the androidfragment if exists */
        if(allFragment!=null) {
            fragmentTransaction.detach(allFragment);
        }
        /** Detaches the applefragment if exists */
        if(meFragment!=null) {
            fragmentTransaction.detach(meFragment);
        }
        fragmentTransaction.add(R.id.realtabcontent,new PeopleFinderRadarFragment_(), "radar");
        radarSelected = true;
        listSelected = false;
        listButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
        radarButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
        fragmentTransaction.commit();
    }

    @Click(R.id.radar_Button)
    public void onMeSelected(){
        if(!radarSelected)
            afterSwitchTreatments("radar");
    }

    @Click(R.id.list_Button)
    public void onAllSelected(){
        if (!listSelected)
            afterSwitchTreatments("finder_list");
    }

    @Subscribe
    public void onSaveLocationMemberEvent(UpdateMemberLocationEvent event) {
        if (GlobalVariables.updatePositionFromRadar) {
            GlobalVariables.updatePositionFromRadar = false;
            editor.putString("current_member", ParseHelpers.createJsonStringFromModel(event.member));
            editor.commit();
            if(radarSelected)
                afterSwitchTreatments("radar");
            else
                afterSwitchTreatments("finder_list");
        }
    }

    @Subscribe
    public void onChangeToPepoleFinderList(ChangeToListEvent event){
        afterSwitchTreatments("finder_list");
    }

    public void afterSwitchTreatments(String tabId){
        android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
        PeopleFinderRadarFragment_ radarFragment = (PeopleFinderRadarFragment_) fragmentManager.findFragmentByTag("radar");
        PeopleFinderListFragment_ listFragment = (PeopleFinderListFragment_) fragmentManager.findFragmentByTag("finder_list");
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        /** Detaches the androidfragment if exists */
        if(radarFragment!=null) {
            fragmentTransaction.detach(radarFragment);
            //fragmentTransaction.remove(radarFragment);
        }

        /** Detaches the applefragment if exists */
        if(listFragment!=null) {
            fragmentTransaction.detach(listFragment);
            //fragmentTransaction.remove(listFragment);
        }

        if(tabId.equalsIgnoreCase("radar")){ /** If current tab is Info */
            /** Create AndroidFragment and adding to fragmenttransaction */
            GlobalVariables.afterViewsRadar = true;
            fragmentTransaction.replace(R.id.realtabcontent, new PeopleFinderRadarFragment_(), "radar");
            radarSelected = true;
            listSelected = false;
            listButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
            radarButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
            /** Bring to the front, if already exists in the fragmenttransaction */
        } else {    /** If current tab is Market */
            /** Create AppleFragment and adding to fragmenttransaction */
            fragmentTransaction.replace(R.id.realtabcontent, new PeopleFinderListFragment_(), "finder_list");
            radarSelected = false;
            listSelected = true;
            listButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
            radarButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
            /** Bring to the front, if already exists in the fragmenttransaction */
        }

        fragmentTransaction.commit();

    }

    @Subscribe
    public void onResumeRadarEvent(ResumeRadarEvent event){

        verifyGpsActivation();

    }
}