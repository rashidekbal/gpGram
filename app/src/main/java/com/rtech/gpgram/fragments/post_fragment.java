package com.rtech.gpgram.fragments;

import static android.content.Context.MODE_PRIVATE;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rtech.gpgram.R;
import com.rtech.gpgram.adapters.PostCommentsAdapter;
import com.rtech.gpgram.constants.SharedPreferencesKeys;
import com.rtech.gpgram.interfaces.NetworkCallbackInterfaceWithJsonObjectDelivery;
import com.rtech.gpgram.interfaces.NetworkCallbackInterface;
import com.rtech.gpgram.managers.CommentsManager;
import com.rtech.gpgram.managers.LikeManager;
import com.rtech.gpgram.managers.PostsManager;
import com.rtech.gpgram.models.Posts_Comments_Model;
import com.rtech.gpgram.models.Posts_Model;
import com.rtech.gpgram.utils.ReUsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class post_fragment extends Fragment {
ImageView posts_image_view,profile_img,like_btn,comment_btn;
TextView username_text,caption_text,like_count_text,comment_count_text,share_count_text;
int postId;
PostsManager postsManager;
LikeManager likeManager;
CommentsManager commentsManager;
Posts_Model postData;
SharedPreferences loginInfo;





    public post_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_posts,container,false);
        postsManager=new PostsManager(v.getContext());
        likeManager=new LikeManager(v.getContext());
        commentsManager=new CommentsManager(v.getContext());
        loginInfo=v.getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        init(v);
        // Inflate the layout for this fragment
        Glide.with(v.getContext()).load(R.drawable.post_placeholder).into(posts_image_view);
//        open profile on click of profile pic
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReUsableFunctions.openProfile(v.getContext(),postData.userId);

            }
        });
//        also on click of username
        username_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReUsableFunctions.openProfile(v.getContext(),postData.userId);

            }
        });

        return v;
    }

    private void init(View v) {
        posts_image_view=v.findViewById(R.id.post_image_view);
        profile_img=v.findViewById(R.id.profile_img);
        username_text=v.findViewById(R.id.username_text);
        caption_text=v.findViewById(R.id.caption_text);
        like_count_text=v.findViewById(R.id.likes_count_text);
        comment_count_text=v.findViewById(R.id.comments_count_text);
        share_count_text=v.findViewById(R.id.shares_count_text);
        like_btn=v.findViewById(R.id.like_btn_image);
        comment_btn=v.findViewById(R.id.comment_btn_image);

        postId=getArguments().getInt("postid");
        AndroidNetworking.initialize(v.getContext());
        loadData(v);

    }


    private void loadData(View v){
        postsManager.getPostWithId(postId, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {


                JSONArray array = response.optJSONArray("data");
                JSONObject object = array.optJSONObject(0);
                int postid = object.optInt("postid");
                String userid = object.optString("userid");
                String imageurl = object.optString("imageurl");
                String caption = object.optString("caption");
                String created_at = object.optString("created_at");
                String username = object.optString("username");
                String profilepic = object.optString("profilepic");
                String likedBy = object.optString("likedBy");
                int likeCount = object.optInt("likeCount");
                int commentCount = object.optInt("commentCount");
                int shareCount = object.optInt("shareCount");
                int isLiked = object.optInt("isLiked");


                postData= new Posts_Model(postid,
                        userid,
                        username,
                        profilepic,
                        imageurl,
                        caption,
                        created_at,
                        likedBy,
                        likeCount,
                        commentCount,
                        shareCount,
                        isLiked);
             setData(v,postData);



            }

            @Override
            public void onError(String err) {
                Log.d("postGetError", "onError: ".concat(err.toString()));

            }
        });
    }
    private void  setData(View view, Posts_Model data){
        Glide.with(view.getContext()).load(data.postUrl).placeholder(R.drawable.post_placeholder).into(posts_image_view);
        Glide.with(view.getContext()).load(data.userDpUrl).placeholder(R.drawable.blank_profile).into(profile_img);
        username_text.setText(data.username);
        if(data.caption.equals("null")||data.caption.isEmpty()){
            caption_text.setVisibility(View.GONE);
        }else{
            caption_text.setText(data.caption);
        }

        like_count_text.setText(String.valueOf(data.likeCount));
        comment_count_text.setText(String.valueOf(data.commentCount));
        share_count_text.setText(String.valueOf(data.shareCount));
        if (data.isliked) {
            like_btn.setImageResource(R.drawable.red_heart_active_icon);

        }else{
            like_btn.setImageResource(R.drawable.heart_inactive_icon_white);
        }

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.isliked){
//                    unlike
                    like_btn.setImageResource(R.drawable.heart_inactive_icon_white);
                    data.isliked=false;
                    data.likeCount=data.likeCount-1;
                    like_count_text.setText(String.valueOf(data.likeCount));
                    likeManager.UnlikePost(data.postId, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(String err) {
                            Log.d("unlikeError", "onError: ".concat(err.toString()));
                            like_btn.setImageResource(R.drawable.red_heart_active_icon);
                            data.isliked=true;
                            data.likeCount=data.likeCount+1;
                            like_count_text.setText(String.valueOf(data.likeCount));

                        }
                    });

                }else{
//                    like
                    like_btn.setImageResource(R.drawable.red_heart_active_icon);
                    data.isliked=true;
                    data.likeCount=data.likeCount+1;
                    like_count_text.setText(String.valueOf(data.likeCount));
                    likeManager.likePost(data.postId, new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(String err) {
                            Log.d("likeError", "onError: ".concat(err.toString()));
                            like_btn.setImageResource(R.drawable.heart_inactive_icon_white);
                            data.isliked=false;
                            data.likeCount=data.likeCount-1;
                            like_count_text.setText(String.valueOf(data.likeCount));

                        }
                    });
                }
            }
        });
        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComments(view);

            }
        });



    }


    public void showComments(View v) {
        // This method will be used to show comments for the post
        // You can implement a dialog or a new fragment to display comments
       BottomSheetDialog commentDialog=new BottomSheetDialog(v.getContext(),R.style.TransparentBottomSheet);
        commentDialog.setContentView(R.layout.posts_comment_layout);
        commentDialog.setCancelable(true);
        FrameLayout dialogFrame=commentDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if(dialogFrame!=null){
            BottomSheetBehavior<FrameLayout> behaviour=BottomSheetBehavior.from(dialogFrame);
            // Set bottom sheet properties
            behaviour.setDraggable(false);
            behaviour.setState(STATE_EXPANDED);
            behaviour.setFitToContents(true);
        }




        commentDialog.show();
        ArrayList<Posts_Comments_Model> dataList=new ArrayList<>();
        PostCommentsAdapter postCommentsAdapter=new PostCommentsAdapter(v.getContext(),dataList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(v.getContext(),LinearLayoutManager.VERTICAL,false);
        RecyclerView comments_recyclerView=commentDialog.findViewById(R.id.comments_recyclerView);
        assert comments_recyclerView != null;
        comments_recyclerView.setLayoutManager(layoutManager);
        comments_recyclerView.setAdapter(postCommentsAdapter);
        EditText inputComment=commentDialog.findViewById(R.id.comment_editText);
        ImageView sendCommentBtn=commentDialog.findViewById(R.id.post_comment_imgBtn);
        ImageView userProfileImg=commentDialog.findViewById(R.id.user_profile);
        TextView posting_progressbar=commentDialog.findViewById(R.id.posting_progress_text);
        ShimmerFrameLayout shimmerFrameLayout=commentDialog.findViewById(R.id.shimmer_comments_holder);
        assert shimmerFrameLayout != null;
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        LinearLayout noCommentsLayout=commentDialog.findViewById(R.id.no_comment_msg_linear_layout);
        comments_recyclerView.setVisibility(View.GONE);
        assert noCommentsLayout != null;
        noCommentsLayout.setVisibility(View.GONE);
        Glide.with(requireActivity()).load(loginInfo.getString(SharedPreferencesKeys.USER_PROFILE_PIC,"null")).placeholder(R.drawable.blank_profile).error(R.drawable.blank_profile).circleCrop().into(userProfileImg);

        // Fetch comments from server
        commentsManager.getCommentOf(postId, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray data=response.getJSONArray("data");
                    if(data.length()>0){
                        noCommentsLayout.setVisibility(View.GONE);
                        comments_recyclerView.setVisibility(View.VISIBLE);
                        // Add each comment to the list
                        for (int i=0;i<data.length();i++){
                            JSONObject individualComment=data.getJSONObject(i);
                            dataList.add(new Posts_Comments_Model(individualComment.getInt("commentid"),individualComment.getInt("postid"),individualComment.getInt("comment_likes_count"),individualComment.getInt("isLiked"),individualComment.getString("userid"),individualComment.getString("username"),individualComment.getString("profilepic"),individualComment.getString("comment_text"),individualComment.getString("createdAt")));
                        }
                        comments_recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                postCommentsAdapter.notifyDataSetChanged();

                            }
                        });
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }else {
                        // Show no comments layout if empty
                        noCommentsLayout.setVisibility(View.VISIBLE);
                        comments_recyclerView.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(String err) {
                Toast.makeText(v.getContext(), "Error fetching comments: ".concat(Objects.requireNonNull(err)), Toast.LENGTH_SHORT).show();
                shimmerFrameLayout.stopShimmer();
                noCommentsLayout.setVisibility(View.VISIBLE);
                comments_recyclerView.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.GONE);

            }
        });


        // Add comment to the post
        assert sendCommentBtn != null;
        sendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow( v.getWindowToken(),0);

                sendCommentBtn.setClickable(false);
                sendCommentBtn.setVisibility(View.GONE);
                assert posting_progressbar != null;
                posting_progressbar.setVisibility(View.VISIBLE);
                assert inputComment != null;
                String commentText=inputComment.getText().toString();
                if(commentText.isEmpty()){
                    Toast.makeText(v.getContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                commentsManager.addComment(postId, commentText, new NetworkCallbackInterfaceWithJsonObjectDelivery() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        posting_progressbar.setVisibility(View.GONE);
                        sendCommentBtn.setVisibility(View.VISIBLE);
                        sendCommentBtn.setClickable(true);
                        try{
                            int commentid=response.getInt("commentid");
                            // Add new comment to the top of the list
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                                dataList.addFirst(new Posts_Comments_Model(
                                        commentid,postId,
                                        0,
                                        0,
                                        loginInfo.getString("userid","unknown"),
                                        loginInfo.getString("username","unknown"),
                                        loginInfo.getString("profileUrl","https://res.cloudinary.com/dphwlcyhg/image/upload/v1747240475/ulpdxajfwpwhlt4ntzn5.webp"),
                                        commentText,new Date().toString()));
                                postCommentsAdapter.notifyItemInserted(0);
                                comments_recyclerView.scrollToPosition(0);
                            }
                            inputComment.setText("");
                        }catch(JSONException jsonError){
                            // Handle JSON error
                        }

                    }

                    @Override
                    public void onError(String err) {
                        posting_progressbar.setVisibility(View.GONE);
                        sendCommentBtn.setVisibility(View.VISIBLE);
                        sendCommentBtn.setClickable(true);
                        Log.d("commentError", "onError: ".concat(err));

                    }
                });
            }
        });
    }
}