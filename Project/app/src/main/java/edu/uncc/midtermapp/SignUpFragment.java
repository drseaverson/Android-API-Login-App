package edu.uncc.midtermapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.uncc.midtermapp.databinding.FragmentSignUpBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpFragment extends Fragment {
    OkHttpClient client = new OkHttpClient();
    UserToken userToken;
    FragmentSignUpBinding binding;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Create New Account");

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cancelSignUp();
            }
        });


        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname = binding.editTextFullname.getText().toString();
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();

                if(fullname.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid fullname !!", Toast.LENGTH_SHORT).show();
                } else if(email.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid email !!", Toast.LENGTH_SHORT).show();
                } else if(password.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid password !!", Toast.LENGTH_SHORT).show();
                } else {
                    RequestBody formBody = new FormBody.Builder()
                            .add("name", fullname)
                            .add("email", email)
                            .add("password", password)
                            .build();

                    Request request = new Request.Builder()
                            .url("https://www.theappsdr.com/posts/signup")
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Toast.makeText(getActivity(), "Unable to create new account !!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if(response.isSuccessful()){
                                String body = response.body().string();
                                try {
                                    JSONObject json = new JSONObject(body);
                                    String token = json.getString("token");
                                    String fullname = json.getString("user_fullname");
                                    String userId = json.getString("user_id");
                                    userToken = new UserToken(token, fullname, userId);

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SharedPreferences sharedPref = getActivity().getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("token", userToken.getToken());
                                            editor.putString("fullName", userToken.getFullname());
                                            editor.putString("userId", userToken.getUserId());
                                            editor.apply();
                                            mListener.registerSuccessfulGotoPosts(userToken);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "Unable to create new account !!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String body = response.body().string();
                                try {
                                    JSONObject json = new JSONObject(body);
                                    String message = json.getString("message");

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });



                }

            }
        });


    }

    SignUpFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (SignUpFragmentListener) context;
    }

    interface SignUpFragmentListener {
        void cancelSignUp();
        void registerSuccessfulGotoPosts(UserToken userToken);
    }
}