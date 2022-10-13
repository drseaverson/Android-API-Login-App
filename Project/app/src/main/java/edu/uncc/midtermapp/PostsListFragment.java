package edu.uncc.midtermapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import okhttp3.*;

public class PostsListFragment extends Fragment {
    private static final String ARG_USER_TOKEN = "ARG_USER_TOKEN";
    TextView textViewName, textViewPageNum;
    Button buttonLogout, buttonCreatePost;
    RecyclerView recyclerViewPost, recyclerViewPage;
    ArrayList<Post> posts = new ArrayList<>();
    UserToken mUserToken;
    int pageNumber = 1;
    int pageNum = 0;
    static OkHttpClient client = new OkHttpClient();
    LinearLayoutManager postLayoutManager, pageLayoutManager;
    CreatePostRecyclerAdapter postAdapter;
    PageRecyclerAdapter pageAdapter;

    public PostsListFragment() {
        //getPosts(pageNumber);
        // Required empty public constructor
    }

    public static PostsListFragment newInstance(UserToken userToken) {
        PostsListFragment fragment = new PostsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_TOKEN, userToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PostListener) {
            postListener = (PostListener) context;
        } else {
            throw new RuntimeException(context.toString() + " need to implement PostListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserToken = (UserToken)getArguments().getSerializable(ARG_USER_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts_list, container, false);
        recyclerViewPage = view.findViewById(R.id.recyclerViewPage);
        recyclerViewPost = view.findViewById(R.id.recyclerViewPost);
        //recyclerview initialization for posts
        postLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewPost.setLayoutManager(postLayoutManager);
        postAdapter = new CreatePostRecyclerAdapter(posts, mUserToken, pageNumber);
        recyclerViewPost.setAdapter(postAdapter);
        //recyclerview initialization for page numbers
        pageLayoutManager = new LinearLayoutManager(getActivity());
        pageLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewPage.setLayoutManager(pageLayoutManager);
        pageAdapter = new PageRecyclerAdapter(pageNum);
        recyclerViewPage.setAdapter(pageAdapter);
        textViewPageNum = view.findViewById(R.id.textViewPageNum);
        textViewName = view.findViewById(R.id.textViewName);
        getPosts(pageNumber);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        buttonCreatePost = view.findViewById(R.id.buttonCreatePost);
        textViewName.setText("Welcome " + mUserToken.getFullname());
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserToken = null;
                SharedPreferences sharedPref = getActivity().getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("token", "");
                editor.putString("fullName", "");
                editor.putString("userId", "");
                editor.apply();
                postListener.logout();
            }
        });
        buttonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postListener.createPost(mUserToken);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Posts");
    }

    public void getPosts(int page) {

        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/posts?page=" + page)
                .addHeader("Authorization", "BEARER " + mUserToken.getToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getActivity(), "Unable to call posts !!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String body = response.body().string();
                    try {
                        JSONObject json = new JSONObject(body);
                        int totalPages = Integer.parseInt(json.getString("totalCount"));
                        pageNum = (int) Math.ceil((double) totalPages / 10); //calculates the number of pages needed for posts
                        int currentPage = Integer.parseInt(json.getString("page"));
                        int pageSize = Integer.parseInt(json.getString("pageSize"));
                        JSONArray postArray = json.getJSONArray("posts");
                        for (int i = 0; i < pageSize; i++) {
                            JSONObject jsonPost = postArray.getJSONObject(i);
                            posts.add(new Post(jsonPost.getString("created_by_name"), jsonPost.getString("post_id"), jsonPost.getString("created_by_uid"), jsonPost.getString("post_text"), jsonPost.getString("created_at")));
                        }
                        //prints list of received posts to prove its not empty
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textViewPageNum.setText("Showing Page " + currentPage + " out of " + pageNum);
                                Log.d("demo", "run: " + posts);
                                postAdapter.postsList = posts;
                                postAdapter.notifyDataSetChanged();
                                pageAdapter.pages = pageNum;
                                pageAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Toast.makeText(getActivity(), "Unable to get posts", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    PostListener postListener;

    public interface PostListener {
        void logout();
        void createPost(UserToken userToken);
    }
}