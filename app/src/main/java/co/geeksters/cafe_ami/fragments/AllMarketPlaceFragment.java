package co.geeksters.cafe_ami.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.cafe_ami.R;
import co.geeksters.cafe_ami.activities.GlobalMenuActivity;
import co.geeksters.cafe_ami.adapter.ListViewMarketAdapter;
import co.geeksters.cafe_ami.adapter.PostsAdapter;
import co.geeksters.cafe_ami.events.success.CommentEvent;
import co.geeksters.cafe_ami.events.success.PostEvent;
import co.geeksters.cafe_ami.events.success.PostsEvent;
import co.geeksters.cafe_ami.global.BaseApplication;
import co.geeksters.cafe_ami.global.GlobalVariables;
import co.geeksters.cafe_ami.global.helpers.GeneralHelpers;
import co.geeksters.cafe_ami.global.helpers.ViewHelpers;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.models.Post;
import co.geeksters.cafe_ami.services.PostService;

import static co.geeksters.cafe_ami.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_one_profile_market_place)
public class AllMarketPlaceFragment extends Fragment {
    String accessToken;
    List<Post> postsList = new ArrayList<Post>();
    ListViewMarketAdapter adapter;
    LayoutInflater inflater;
    Member currentUser;

    @ViewById(R.id.empty_search)
    LinearLayout emptySearch;

    @ViewById(R.id.loading)
    LinearLayout loading;

    @ViewById(R.id.postsMarket)
    LinearLayout postsMarket;

    @ViewById(R.id.myPostsSearchForm)
    LinearLayout myPostsSearchForm;

    public void listAllPostService() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            PostService postService = new PostService(accessToken);
            postService.listAllPosts();
            loading.setVisibility(View.VISIBLE);

        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @AfterViews
    public void listPostForCurrentMember() {

        listAllPostService();
    }

    @Subscribe
    public void onGetListPostsEvent(PostsEvent event) {
        postsList = event.posts;
        GlobalVariables.replyFromMyMarket = false;
        GlobalVariables.replyToAll = true;

        PostsAdapter adapter = new PostsAdapter(inflater, this.getActivity(), postsMarket, Post.orderDescPost(postsList), accessToken, currentUser);
        adapter.makeList();
        loading.setVisibility(View.INVISIBLE);
        if(postsList.isEmpty()) emptySearch.setVisibility(View.VISIBLE);
        else                  emptySearch.setVisibility(View.INVISIBLE);


        if(GlobalVariables.notifiyedByPost) {
            Post notifiedPost = new Post();
            for (int i = 0; i < event.posts.size(); i++) {
                if (event.posts.get(i).id == GlobalVariables.notificationPostId) {
                    notifiedPost = event.posts.get(i);
                    break;
                }
            }
            GlobalVariables.onReply = true;
            GlobalVariables.notifiyedByPost = false;
            GlobalVariables.notificationPostId = -1;

            FragmentTransaction fragmentTransaction = ((GlobalMenuActivity) GlobalVariables.activity).getSupportFragmentManager().beginTransaction();
            Fragment fragment = new ReplyMarketFragment_().newInstance(notifiedPost.id, notifiedPost.comments);
            fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
            fragmentTransaction.replace(R.id.contentFrame, fragment);
            fragmentTransaction.commit();
        }
    }

    @Subscribe
    public void onGetDeletedPostEvent(PostEvent event) {
        for(int i=0; i<postsList.size(); i++) {
            if(postsList.get(i).id == event.post.id) {
                postsList.remove(i);
                break;
            }
        }

        PostsAdapter adapter = new PostsAdapter(inflater, this.getActivity(), postsMarket, Post.orderDescPost(postsList), accessToken, currentUser);
        adapter.makeList();
        loading.setVisibility(View.INVISIBLE);
        if(postsList.isEmpty()) emptySearch.setVisibility(View.VISIBLE);
        else                  emptySearch.setVisibility(View.INVISIBLE);

    }


    @Subscribe
    public void onGetDeletedCommentEvent(CommentEvent event) {
        for(int i=0; i<postsList.size(); i++) {
            if(postsList.get(i).id == event.comment.postId) {
                for ( int j = 0 ; j<postsList.get(i).comments.size(); j++){
                    if (postsList.get(i).comments.get(j).id == event.comment.id)
                    {
                        postsList.get(i).comments.remove(j);
                        break;
                    }
                }

            }
        }
        PostsAdapter adapter = new PostsAdapter(inflater, this.getActivity(), postsMarket, Post.orderDescPost(postsList), accessToken, currentUser);
        adapter.makeList();
        loading.setVisibility(View.INVISIBLE);
        if(postsList.isEmpty()) emptySearch.setVisibility(View.VISIBLE);
        else                  emptySearch.setVisibility(View.INVISIBLE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");
        currentUser = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        PostsAdapter.lastClickedPosts = new ArrayList<Integer>();

        this.inflater = inflater;

        return null;
    }
}