package com.sourcey.materiallogindemo.adapters;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Gravity;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.sourcey.materiallogindemo.Utils.CircleTransform;
import com.sourcey.materiallogindemo.Utils.ParseConstants;
import com.sourcey.materiallogindemo.view.Message;
import com.sourcey.materiallogindemo.R;

import java.util.List;

import com.squareup.picasso.Picasso;

/**
 * Created by GuillermoArturo on 28/04/2016.
 */
public class ChatListAdapter extends ArrayAdapter<Message> {

    private String mUserId;
    private Context context;

    public ChatListAdapter(Context context, String userId, List<Message> messages) {
        super(context, 0, messages);
        this.context = context;
        this.mUserId = userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, parent, false);

            final ViewHolder holder = new ViewHolder();
            holder.imageOther = (ImageView)convertView.findViewById(R.id.ivProfileOther);
            holder.imageMe = (ImageView)convertView.findViewById(R.id.ivProfileMe);
            holder.body = (TextView)convertView.findViewById(R.id.tvBody);
            convertView.setTag(holder);
        }

        final ParseObject message = getItem(position);
        final ViewHolder holder = (ViewHolder)convertView.getTag();
        final boolean isMe = message.getString(ParseConstants.KEY_ID_USER).equals(mUserId);

        if (isMe) {
            holder.imageMe.setVisibility(View.VISIBLE);
            holder.imageOther.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        } else {
            holder.imageOther.setVisibility(View.VISIBLE);
            holder.imageMe.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        }

        final ImageView profileView = isMe ? holder.imageMe : holder.imageOther;

        ParseObject photo_profile = message.getParseObject(ParseConstants.KEY_USER_RELATION);

        photo_profile.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    String photo_url = null;

                    ParseFile photo = object.getParseFile(ParseConstants.KEY_USER_PHOTO);
                    if (photo != null) {
                        photo_url = photo.getUrl();

                        if (!photo_url.isEmpty()) {
                            Picasso.with(getContext()).load(photo_url).transform(new CircleTransform()).fit().centerCrop().tag(context).into(profileView);
                        }
                    }
                }
            }
        });
        holder.body.setText(message.getString(ParseConstants.KEY_BODY));
        return convertView;
    }

    final class ViewHolder {
        public ImageView imageOther;
        public ImageView imageMe;
        public TextView body;
    }
}
