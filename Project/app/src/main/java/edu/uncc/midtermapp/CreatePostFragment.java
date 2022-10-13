package edu.uncc.midtermapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreatePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_TOKEN = "ARG_USER_TOKEN";
    UserToken mUserToken;
    EditText editTextPost;
    Button buttonSubmitPost, buttonCancelPost;
    OkHttpClient client = new OkHttpClient();

    // TODO: Rename and change types of parameters


    public CreatePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreatePostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreatePostFragment newInstance(UserToken userToken) {
        CreatePostFragment fragment = new CreatePostFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_TOKEN, userToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateListener) {
            createListener = (CreateListener) context;
        } else {
            throw new RuntimeException(context.toString() + " need to implement CreateListener");
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
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);
        // Inflate the layout for this fragment
        editTextPost = view.findViewById(R.id.editTextPost);
        buttonCancelPost = view.findViewById(R.id.buttonCancelPost);
        buttonSubmitPost = view.findViewById(R.id.buttonSubmitPost);
        buttonCancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createListener.cancelPost();
            }
        });
        buttonSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editTextPost.getText().toString();
                if (text.equals("")) {
                    Toast.makeText(getActivity(), "Please enter text for your post", Toast.LENGTH_SHORT).show();
                } else {
                    //create post API call and send back to posts page once complete
                    RequestBody formBody = new FormBody.Builder()
                            .add("post_text", text)
                            .build();

                    Request request = new Request.Builder()
                            .url("https://www.theappsdr.com/posts/create")
                            .post(formBody)
                            .addHeader("Authorization", "BEARER " + mUserToken.token)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Toast.makeText(getActivity(), "Unable to create post !!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if(response.isSuccessful()){
                                String body = response.body().string();
                                try {
                                    JSONObject json = new JSONObject(body);
                                    String message = json.getString("message");

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                            createListener.createPost();
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "Unable to create post", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Create Post");
    }

    CreateListener createListener;

    public interface CreateListener {
        void createPost();
        void cancelPost();
    }
}