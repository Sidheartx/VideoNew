package io.wyntr.peepster;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siddharth on 4/4/16.
 */
public class MyPostsAdapter extends BaseAdapter {
    public static final String TAG = FeedsGridAdapter.class.getSimpleName();
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    private List<ParsePosts> postsarraylist = null;
    private ArrayList<ParsePosts> arraylist;

    public MyPostsAdapter(Context context, List<ParsePosts> postsarraylist) {
        this.context = context;
        this.postsarraylist = postsarraylist;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<ParsePosts>();
        this.arraylist.addAll(postsarraylist);
    }

    public class ViewHolder {
        ImageView phone;
        ImageView delete;
        TextView views;
    }

    @Override
    public int getCount() {
        return postsarraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return postsarraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.posts_image, null);
            // Locate the ImageView in gridview_item.xml
            holder.phone = (ImageView) view.findViewById(R.id.postsThumb);
            holder.delete = (ImageView) view.findViewById(R.id.postsDelete);
            holder.delete.setVisibility(View.GONE);
            holder.delete.setImageResource(R.drawable.people);
            holder.views = (TextView) view.findViewById(R.id.views_counter);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Picasso.with(context)
                .load(postsarraylist.get(position).getPhone())
                .transform(new CircleTransform())
                .error(R.drawable.people)
                .into(holder.phone);
        holder.views.setText(String.valueOf(postsarraylist.get(position).getViews()));
        // Capture GridView item click
        // Capture GridView item click
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(context, SingleVideoView.class);
                // Pass all data phone
                intent.putExtra("video", postsarraylist.get(position)
                        .getVideo());
                intent.putExtra("userId", postsarraylist.get(position).getUserId());
                intent.putExtra("objectId", postsarraylist.get(position).getObjectId());
                context.startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.phone.setVisibility(View.GONE);
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseObject.createWithoutData("AroundMe", postsarraylist.get(position).getObjectId()).deleteEventually();
                        postsarraylist.remove(position);
                        notifyDataSetChanged();
                    }
                });
                return false;
            }
        });
        return view;
    }
}