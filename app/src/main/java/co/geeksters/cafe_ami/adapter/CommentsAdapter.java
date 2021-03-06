package co.geeksters.cafe_ami.adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import co.geeksters.cafe_ami.R;
import co.geeksters.cafe_ami.activities.GlobalMenuActivity;
import co.geeksters.cafe_ami.fragments.OneProfileFragment_;
import co.geeksters.cafe_ami.global.GlobalVariables;
import co.geeksters.cafe_ami.global.helpers.ViewHelpers;
import co.geeksters.cafe_ami.models.Comment;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.services.CommentService;

import static co.geeksters.cafe_ami.global.helpers.ParseHelpers.createJsonElementFromString;

/**
 * Created by soukaina on 04/02/15.
 */
public class CommentsAdapter {

    Activity context;
    List<Comment> commentList;
    LinearLayout childView;
    String accessToken;
    SharedPreferences preferences;
    Member currentUser;

    public CommentsAdapter(Activity context, List<Comment> commentList, LinearLayout childView, String accessToken) {
        this.context = context;
        this.commentList = commentList;
        Collections.reverse(this.commentList);
        this.childView = childView;
        this.accessToken = accessToken;
    }

    public void makeList() {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout llList = (LinearLayout) childView.findViewById(R.id.commentsLayout);

        llList.removeAllViews();


        for(int i = 0 ; i < commentList.size() ; i++) {



            View childView = inflater.inflate(R.layout.list_item_comment, null); //same layout you gave to the adapter

            TextView fullNameTextView = (TextView)childView.findViewById(R.id.fullName);
            fullNameTextView.setText(commentList.get(i).member.fullName);

            TextView commentTextView = (TextView)childView.findViewById(R.id.comment);
            commentTextView.setText(commentList.get(i).text);

            LinearLayout bottomLayout = (LinearLayout)childView.findViewById(R.id.bottom_layout);
            if(i == (commentList.size()-1))
                bottomLayout.setVisibility(View.GONE);

            Typeface typeFace=Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
            fullNameTextView.setTypeface(typeFace);
            commentTextView.setTypeface(typeFace);


            final int index = i;

            ImageView picture = (ImageView)childView.findViewById(R.id.picture);
            picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalVariables.isInMyProfileFragmentFromOpportunities = true;
                    FragmentTransaction fragmentTransaction = ((GlobalMenuActivity) GlobalVariables.activity).getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(commentList.get(index).member, 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

            ViewHelpers.setImageViewBackgroundFromURL(context, picture, commentList.get(i).member.image);

            ImageView deleteComment = (ImageView)childView.findViewById(R.id.deleteComment);
            preferences = context.getSharedPreferences("CurrentUser", context.MODE_PRIVATE);
            currentUser = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
            if(commentList.get(i).member.id == currentUser.id) {
                deleteComment.setVisibility(View.VISIBLE);
            }

            deleteComment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

             GlobalVariables.onDeleteComment = true;
             CommentService commentService = new CommentService(accessToken);
             commentService.deleteComment(commentList.get(index).postId, commentList.get(index).id);
             GlobalVariables.commentClickedIndex = index;
             // ald.dismiss();
             }
  /*                  });

                }
            });*/

            });

            llList.addView(childView,0);
        }
    }
}