package co.geeksters.cafe_ami.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import co.geeksters.cafe_ami.adapter.ListViewMarketAdapter;
import co.geeksters.cafe_ami.adapter.PostsAdapter;
import co.geeksters.cafe_ami.adapter.PostsAdapterOneProfile;
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
public class MeMarketPlaceFragment extends Fragment {
    // ArrayList for Listview
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

    public void listPostForCurrentMemberService() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            //spinner.setVisibility(View.VISIBLE);
            PostService postService = new PostService(accessToken);
            postService.listPostsForMe();
            loading.setVisibility(View.VISIBLE);
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @AfterViews
    public void listPostForCurrentMember() {

        //myPostsSearchForm.setBackgroundColor(Color.parseColor("#eeeeee"));

        listPostForCurrentMemberService();
        //loading.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onGetListPostsEvent(PostsEvent event) {

       // spinner.setVisibility(View.GONE);
        postsList = event.posts;
//        ArrayList<HashMap<String, String>> posts = Post.postsInfoForItem(postsList);
        GlobalVariables.replyFromMyMarket = false;
        GlobalVariables.replyToAll = false;

        PostsAdapterOneProfile adapter = new PostsAdapterOneProfile(inflater, this.getActivity(), postsMarket, Post.orderDescPost(postsList), accessToken, currentUser);
        adapter.makeList();

        loading.setVisibility(View.INVISIBLE);
        if(postsList.isEmpty()) emptySearch.setVisibility(View.VISIBLE);
        else                  emptySearch.setVisibility(View.INVISIBLE);
    }

    @Subscribe
    public void onGetDeletedPostEvent(PostEvent event) {
        for(int i=0; i<postsList.size(); i++) {
            if(postsList.get(i).id == event.post.id) {
                postsList.remove(i);
                break;
            }
        }

        PostsAdapterOneProfile adapter = new PostsAdapterOneProfile(inflater, this.getActivity(), postsMarket, Post.orderDescPost(postsList), accessToken, currentUser);
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