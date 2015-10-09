package co.geeksters.hq.adapter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.fragments.OneProfileFragment_;
import co.geeksters.hq.fragments.UpdateTodoFragment_;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Todo;
import co.geeksters.hq.services.TodoService;

/**
 * Created by soukaina on 04/02/15.
 */
public class TodosMembersAdapter {


    private List<Member> memberList;
    String accessToken;
    LinearLayout llList;
    LayoutInflater inflater;
    SharedPreferences preferences;
    Member currentUser;
    Fragment context;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    public TodosMembersAdapter(LayoutInflater inflater, Member currentUser,LinearLayout llList, ArrayList<Member> memberList, String accessToken, Fragment fragment) {

        this.context = fragment;
        this.currentUser = currentUser;
        this.memberList = memberList;
        this.accessToken = accessToken;
        this.llList = llList;
        this.inflater = inflater;

    }

    public void makeList() {
        llList.removeAllViews();

        for(int i = 0 ; i < memberList.size(); i++) {
            final int index = i;

            final LinearLayout childView = (LinearLayout) inflater.inflate(R.layout.list_todo_horizontal_members, null, false);

            ImageView picture = (ImageView) childView.findViewById(R.id.picture);
            ViewHelpers.setImageViewBackgroundFromURL(context.getActivity(), picture, memberList.get(i).image);

            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = context.getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(memberList.get(index), 0);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

             llList.addView(childView);

        }

        }
}