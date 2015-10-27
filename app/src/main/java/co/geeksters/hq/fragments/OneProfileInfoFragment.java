package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.geeksters.hq.R;
import co.geeksters.hq.events.success.SaveMemberForLogoutEvent;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.PredicateLayout;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.GeneralHelpers.isInternetAvailable;
import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;
import static co.geeksters.hq.global.helpers.ViewHelpers.createViewInterest;

@EFragment(R.layout.fragment_one_profile_info)
public class OneProfileInfoFragment extends Fragment {

    @ViewById(R.id.organisationContent)
    TextView organisationContent;

    @ViewById(R.id.organisationTitle)
    TextView organisationTitle;

    @ViewById(R.id.missionContent)
    TextView missionContent;

    @ViewById(R.id.missionTitle)
    TextView missionTitle;

    @ViewById(R.id.shortBioContent)
    TextView shortBioContent;

    @ViewById(R.id.shortBioTitle)
    TextView shortBioTitle;

    @ViewById(R.id.logoutButton)
    Button logoutButton;

       @ViewById(R.id.interestsContent)
    PredicateLayout interestsContent;

    @ViewById(R.id.interestContent)
    LinearLayout interestContent;

    @ViewById(R.id.interestsTitle)
    TextView interestsTitle;
    @ViewById(R.id.logout_layout)
    RelativeLayout logoutLayout;

    // Beans
    LayoutInflater layoutInflater;
    SharedPreferences preferences;
    Member memberToDisplay;
    String accessToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater = inflater;

        return null;
    }

    @AfterViews
    void initFieldsFromCurrentMemberInformation() {
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token", "").toString().replace("\"","");

        if(GlobalVariables.isCurrentMember) {
            memberToDisplay = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
        } else {
            memberToDisplay = Member.createUserFromJson(createJsonElementFromString(preferences.getString("profile_member", "")));
            logoutLayout.setVisibility(View.GONE);

        }

        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        missionContent.setTypeface(typeFace);
        missionTitle.setTypeface(typeFace);
        organisationContent.setTypeface(typeFace);
        organisationTitle.setTypeface(typeFace);
        shortBioContent.setTypeface(typeFace);
        shortBioTitle.setTypeface(typeFace);
        interestsTitle.setTypeface(typeFace);
        logoutButton.setTypeface(null, typeFace.BOLD);

        organisationContent.setText(memberToDisplay.returnNameForNullCompaniesValue());
        missionContent.setText(memberToDisplay.goal);
        shortBioContent.setText(memberToDisplay.blurp);

        if(memberToDisplay.interests.size() != 0)
            interestsTitle.setVisibility(View.VISIBLE);

        for(int i = 0; i < memberToDisplay.interests.size(); i++)
            createViewInterest(getActivity(), layoutInflater, interestsContent, memberToDisplay.interests.get(i).name);
    }

    @Click(R.id.logoutButton)
    public void logout(){
        //showProgress(true, getActivity(), meScrollView, logoutProgress);
        // Test internet availability
        if(isInternetAvailable(getActivity())) {
            memberToDisplay.deviceToken = " ";
            memberToDisplay.deviceType = " ";
            MemberService memberService = new MemberService(accessToken);
            memberService.updateMember(memberToDisplay.id, memberToDisplay);
        } else {
            //showProgress(false, getActivity(), meScrollView, logoutProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }


}