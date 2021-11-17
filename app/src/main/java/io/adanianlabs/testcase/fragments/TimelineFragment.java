package io.adanianlabs.testcase.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fxn.stash.Stash;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.adanianlabs.testcase.R;
import io.adanianlabs.testcase.adapters.PostAdapter;
import io.adanianlabs.testcase.dependancies.Constants;
import io.adanianlabs.testcase.models.Post;
import io.adanianlabs.testcase.models.User;


public class TimelineFragment extends Fragment {

    private Unbinder unbinder;
    private View root;
    private Context context;

    private User loggedInUser;

    private PostAdapter mAdapter;
    private ArrayList<Post> postArrayList;

    @BindView(R.id.shimmer_my_container)
    ShimmerFrameLayout shimmer_my_container;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.no_post_lyt)
    LinearLayout no_post_lyt;

    @BindView(R.id.error_lyt)
    LinearLayout error_lyt;


    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        this.context = ctx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_timeline, container, false);
        unbinder = ButterKnife.bind(this, root);

        loggedInUser = (User) Stash.getObject(Constants.LOGGED_IN_USER, User.class);


        loadListedPosts();

        postArrayList = new ArrayList<>();
        mAdapter = new PostAdapter(context, postArrayList);


        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        recyclerView.setAdapter(mAdapter);

        return root;
    }

    private void loadListedPosts(){

        String auth_token = loggedInUser.getAccesstoken();


        AndroidNetworking.get(Constants.ENDPOINT+Constants.LIST_POSTS)
                .addHeaders("Authorization",auth_token)
                .addHeaders("Content-Type", "application.json")
                .addHeaders("Accept", "*/*")
                .addHeaders("Accept", "gzip, deflate, br")
                .addHeaders("Connection","keep-alive")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
//                        Log.e(TAG, response.toString());

                        postArrayList.clear();

                        if (recyclerView!=null)
                            recyclerView.setVisibility(View.VISIBLE);

                        if (shimmer_my_container!=null){
                            shimmer_my_container.stopShimmerAnimation();
                            shimmer_my_container.setVisibility(View.GONE);
                        }


                        try {

                            boolean  status = response.has("success") && response.getBoolean("success");
                            String message = response.has("message") ? response.getString("message") : "";

                            if (status){

                                JSONArray myArray = response.getJSONArray("data");

                                if (myArray.length() > 0){


                                    for (int i = 0; i < myArray.length(); i++) {

                                        JSONObject item = (JSONObject) myArray.get(i);

                                        int id = item.has("id") ? item.getInt("id") : 0;
                                        String mediaurl = item.has("mediaurl") ? item.getString("mediaurl") : "";
                                        String post = item.has("post") ? item.getString("post") : "";
                                        String type = item.has("type") ? item.getString("type") : "";
                                        int comments = item.has("comments") ? item.getInt("comments") : 0;

                                        JSONObject user = item.getJSONObject("User");
                                        String profilepicture = user.has("profilepicture") ? user.getString("profilepicture") : "";
                                        String username = user.has("username") ? user.getString("username") : "";


                                        Post newPost = new Post(id,post,mediaurl,type,profilepicture,username);

                                        postArrayList.add(newPost);
                                        mAdapter.notifyDataSetChanged();

                                    }

                                }else {
                                    //no data found
                                    no_post_lyt.setVisibility(View.VISIBLE);


                                }

                            }
                            else {

                                Snackbar.make(root.findViewById(R.id.frag_timeline), message, Snackbar.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
//                        Log.e(TAG, error.getErrorBody());
                        if (recyclerView!=null)
                            recyclerView.setVisibility(View.VISIBLE);

                        if (shimmer_my_container!=null){
                            shimmer_my_container.stopShimmerAnimation();
                            shimmer_my_container.setVisibility(View.GONE);
                        }

//                        Log.e(TAG, String.valueOf(error.getErrorCode()));

                        if (error.getErrorCode() == 0){

                            no_post_lyt.setVisibility(View.VISIBLE);
                        }
                        else {

                            error_lyt.setVisibility(View.VISIBLE);
                            Snackbar.make(root.findViewById(R.id.frag_timeline), error.getErrorBody(), Snackbar.LENGTH_LONG).show();

                        }

                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmer_my_container.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        shimmer_my_container.stopShimmerAnimation();
        super.onPause();
    }

}