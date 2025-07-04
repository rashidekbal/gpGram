package com.rtech.gpgram.fragments;

import static com.bumptech.glide.Priority.HIGH;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.rtech.gpgram.BuildConfig;
import com.rtech.gpgram.R;
import com.rtech.gpgram.activities.FollowerFollowingList;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.Post_fragmentSetCallback;
import com.rtech.gpgram.models.Profile_Model;
import com.rtech.gpgram.viewmodels.ProfileViewModel;


public class profileFragment extends Fragment {

    SharedPreferences loginInfo;
    TextView userid_text,username_text,posts_count_text,followers_count_text,following_count_text,bio_text;
    AppCompatButton EditProfile_btn;
    BottomNavigationView profile_bottom_navigation_view;
    ImageView profileImg;
    String baseUrl= BuildConfig.BASE_URL;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout profileLayout;
    // Base URL for the API, can be set in BuildConfig or directly here
    Profile_Model userdata;
Post_fragmentSetCallback callback;
ProfileViewModel profileViewModel;

    public profileFragment(Post_fragmentSetCallback callback) {
        // Required empty public constructor
        this.callback=callback;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     View v=inflater.inflate(R.layout.fragment_profile, container, false);
     init(v);

    profileViewModel.getProfileLiveData().observe(getViewLifecycleOwner(), new Observer<Profile_Model>() {
         @Override
         public void onChanged(Profile_Model profileModel) {
                if(profileModel!=null){
                    userdata=profileModel;
                    setUserdata(v);
                }else {

                }

         }
     });


        profile_bottom_navigation_view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.posts){
                    change_fragment(new Posts_of_profile(new Post_fragmentSetCallback() {
                        @Override
                        public void openPostFragment(String url, int postid) {
                            callback.openPostFragment(url,postid);

                        }

                        @Override
                        public void openEditor() {

                        }
                    }));

                }else if (item.getItemId()==R.id.tags){}                return true;
            }
        });
profile_bottom_navigation_view.setSelectedItemId(R.id.posts);
        followers_count_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), FollowerFollowingList.class);
                intent.putExtra("type","followers");
                intent.putExtra("userid",loginInfo.getString(SharedPreferencesKeys.USER_ID,userid_text.getText().toString()));
                startActivity(intent);

            }
        });
        following_count_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Intent intent=new Intent(getContext(), FollowerFollowingList.class);
                intent.putExtra("type","following");
                intent.putExtra("userid",loginInfo.getString(SharedPreferencesKeys.USER_ID,userid_text.getText().toString()));
                startActivity(intent);


            }
        });
        EditProfile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.openEditor();
            }
        });

        return v;
    }


    public void init(View v){
        userid_text=v.findViewById(R.id.userid_text);
        username_text=v.findViewById(R.id.username_text);
        profile_bottom_navigation_view=v.findViewById(R.id.profile_bottom_navigation_view);
        profileImg=v.findViewById(R.id.profile_img);
        posts_count_text=v.findViewById(R.id.posts_count_text);
        followers_count_text=v.findViewById(R.id.followers_count_text);
        following_count_text=v.findViewById(R.id.following_count_text);
        bio_text=v.findViewById(R.id.bio_text);
        EditProfile_btn=v.findViewById(R.id.EditProfile_btn);
        AndroidNetworking.initialize(v.getContext());
        shimmerFrameLayout=v.findViewById(R.id.profile_shimmer);
        profileLayout=v.findViewById(R.id.profile_layout);
        loginInfo=getContext().getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME,v.getContext().MODE_PRIVATE);
        profileViewModel=new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    }

    public void change_fragment(Fragment fragment){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_profile_page,fragment)
                .commit();

    }

    private void setUserdata(View v){
        if(userdata.profilepic.equals("null")){
            Glide.with(v.getContext()).load(R.drawable.blank_profile).circleCrop().priority(Priority.HIGH).into(profileImg);}
        else {
            Glide.with(v.getContext()).load(userdata.profilepic).circleCrop().priority(HIGH).into(profileImg);
        }
        userid_text.setText(userdata.userid);
        username_text.setText(userdata.username);
        posts_count_text.setText(String.valueOf(userdata.posts));
        followers_count_text.setText(String.valueOf(userdata.followers));
        following_count_text.setText(String.valueOf(userdata.following));
        bio_text.setText(userdata.bio);
        shimmerFrameLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.VISIBLE);


    }
}