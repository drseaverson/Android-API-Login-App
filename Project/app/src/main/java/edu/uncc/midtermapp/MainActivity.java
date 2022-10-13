/*
Derek Seaverson
Midterm App
10/18/2021
11:33pm
 */
package edu.uncc.midtermapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, SignUpFragment.SignUpFragmentListener, PostsListFragment.PostListener, CreatePostFragment.CreateListener {
    UserToken mUserToken;


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = this.getSharedPreferences("SharedPref", MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        String fullName = sharedPref.getString("fullName", "");
        String userId = sharedPref.getString("userId", "");
        if (!token.equals("")) {
            mUserToken = new UserToken(token, fullName, userId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rootView, PostsListFragment.newInstance(mUserToken))
                    .commit();
        }
        //Log.d("demo", "onStart: " + token);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void createAccount() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new SignUpFragment())
                .commit();
    }

    @Override
    public void loginSuccessfulGotoPosts(UserToken userToken) {
        mUserToken = userToken;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, PostsListFragment.newInstance(mUserToken))
                .commit();
    }

    @Override
    public void cancelSignUp() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void registerSuccessfulGotoPosts(UserToken userToken) {
        mUserToken = userToken;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, PostsListFragment.newInstance(mUserToken))
                .commit();
    }

    @Override
    public void logout() {
        mUserToken = null;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void createPost(UserToken userToken) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, CreatePostFragment.newInstance(userToken))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void createPost() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void cancelPost() {
        getSupportFragmentManager().popBackStack();
    }
}