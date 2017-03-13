package com.sourcey.materiallogindemo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;

import com.parse.ParseObject;
import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.Utils.CircleTransform;
import com.sourcey.materiallogindemo.Utils.ParseConstants;
import com.sourcey.materiallogindemo.ui.ChatActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by GuillermoArturo on 27/04/2016.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private Context context;
    private List<ParseObject>friends;

    public FriendsAdapter(Context context, List<ParseObject>friends){
        this.context = context;
        this.friends = friends;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(rootView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.bind(friends.get(position));

    }

    @Override
    public int getItemCount(){
        return friends.size();
    }

    public void refill(List<ParseObject> list){
        friends.clear();
        friends.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.friend_photo_item)
        ImageView friend_photo;
        @Bind(R.id.friend_name)
        TextView friend_name;
        @Bind(R.id.friend_description)
        TextView friend_description;

        private String friend_id;

        public ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(ParseObject event){
            friend_id = event.getObjectId();

            Picasso.with(context).load(event.getParseFile(ParseConstants.KEY_FRIEND_PHOTO).getUrl()).
                    transform(new CircleTransform()).fit().centerCrop().tag(context).into(friend_photo);

            friend_name.setText(event.getString(ParseConstants.KEY_FRIEND_NAME));
            friend_description.setText(event.getString(ParseConstants.KEY_FRIEND_DESCRIPTION));

        }

        @Override
        public void onClick(View view){
            Intent chat = new Intent(context, ChatActivity.class);
            chat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chat);
        }

    }
}
