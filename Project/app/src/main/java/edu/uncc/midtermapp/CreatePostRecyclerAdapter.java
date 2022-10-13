package edu.uncc.midtermapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreatePostRecyclerAdapter extends RecyclerView.Adapter<CreatePostRecyclerAdapter.PostViewHolder> {

    ArrayList<Post> postsList;
    static UserToken userToken;
    static OkHttpClient client = new OkHttpClient();
    int page = 1;

    public CreatePostRecyclerAdapter(ArrayList<Post> posts, UserToken userToken, int page) {
        this.postsList = posts;
        this.userToken = userToken;
        this.page = page;
    }

    public CreatePostRecyclerAdapter() {

    }

    public void setData(ArrayList<Post> posts) {
        this.postsList = posts;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_row_items, parent, false);
        CreatePostRecyclerAdapter.PostViewHolder postViewHolder = new CreatePostRecyclerAdapter.PostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CreatePostRecyclerAdapter.PostViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = postsList.get(position);
        holder.textViewPost.setText(post.getPost());
        holder.textViewPostCreator.setText(post.getPostName());
        holder.textViewPostTime.setText(post.getTime());
        if (post.created_by_uid.equals(userToken.getUserId())) {
            holder.imageButtonTrash.setVisibility(View.VISIBLE);
            holder.imageButtonTrash.setClickable(true);
        }
        holder.position = position;
        holder.post = post;
        holder.imageButtonTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postsList.remove(post);
                setData(postsList);
                RequestBody formBody = new FormBody.Builder()
                        .add("post_id", post.post_id)
                        .build();

                Request request = new Request.Builder()
                        .url("https://www.theappsdr.com/posts/delete")
                        .addHeader("Authorization", "BEARER " + userToken.getToken())
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        //Toast.makeText(itemView.getContext(), "Cannot delete post !!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            String body = response.body().string();
                            try {
                                JSONObject json = new JSONObject(body);
                                String message = json.getString("message");
                                Request request = new Request.Builder()
                                        .url("https://www.theappsdr.com/posts?page=" + page)
                                        .addHeader("Authorization", "BEARER " + userToken.getToken())
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        //Toast.makeText(getActivity(), "Unable to call posts !!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            String body = response.body().string();
                                            try {
                                                JSONObject json = new JSONObject(body);
                                                int pageSize = Integer.parseInt(json.getString("pageSize"));
                                                JSONArray postArray = json.getJSONArray("posts");
                                                postsList.clear();
                                                for (int i = 0; i < pageSize; i++) {
                                                    JSONObject jsonPost = postArray.getJSONObject(i);
                                                    postsList.add(new Post(jsonPost.getString("created_by_name"), jsonPost.getString("post_id"), jsonPost.getString("created_by_uid"), jsonPost.getString("post_text"), jsonPost.getString("created_at")));
                                                }
                                                ((Activity) holder.rootView.getContext()).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        setData(postsList);
                                                    }
                                                });
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                //Toast.makeText(getActivity(), "Unable to get posts", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Toast.makeText(itemView.getContext(), "Failure to delete post !!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView textViewPost, textViewPostCreator, textViewPostTime;
        ImageButton imageButtonTrash;
        int position;
        Post post;
        ConstraintLayout parentLayout;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            parentLayout = itemView.findViewById(R.id.postLayout);
            textViewPost = itemView.findViewById(R.id.textViewPost);
            textViewPostCreator = itemView.findViewById(R.id.textViewPostCreator);
            textViewPostTime = itemView.findViewById(R.id.textViewPostTime);
            imageButtonTrash = itemView.findViewById(R.id.imageButtonTrash);
        }
    }
}
