package io.wyntr.peepster;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;




import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FeedsGridAdapter extends BaseAdapter {

    public static final String TAG = FeedsGridAdapter.class.getSimpleName();

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private List<ParseFeeds> phonearraylist = null;
    private ArrayList<ParseFeeds> arraylist;

    public FeedsGridAdapter(Context context, List<ParseFeeds> phonearraylist) {
        this.context = context;
        this.phonearraylist = phonearraylist;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<ParseFeeds>();
        this.arraylist.addAll(phonearraylist);
        imageLoader = new ImageLoader(context);
    }

    public class ViewHolder {
        ImageView phone;
        TextView user;
        TextView distance;
    }

    @Override
    public int getCount() {
        return phonearraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return phonearraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.feeds_image, null);
            // Locate the ImageView in gridview_item.xml
            holder.phone = (ImageView) view.findViewById(R.id.videoThumb);
            holder.user = (TextView)view.findViewById(R.id.grid_item_title);
            holder.distance=(TextView)view.findViewById(R.id.grid_item_distance);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Load image into GridView
        //imageLoader.DisplayImage(phonearraylist.get(position).getPhone(),
        //holder.phone);
        Picasso.with(context)
                .load(phonearraylist.get(position).getPhone())
                .transform(new CircleTransform())
                .error(R.drawable.ic_action_picture)
                .into(holder.phone);
        holder.user.setText(phonearraylist.get(position).getUser());

        holder.distance.setText(String.valueOf(phonearraylist.get(position).getdistance()));

        // Capture GridView item click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                String intentviews = String.valueOf(phonearraylist.get(position).getViews());
                Intent intent = new Intent(context, SingleVideoView.class);
                // Pass all data phone
                intent.putExtra("video", phonearraylist.get(position)
                        .getVideo());

                // Send single item click data to SingleItemView Class

                // Pass all data phone
                intent.putExtra("video", phonearraylist.get(position)
                        .getVideo());
                intent.putExtra("userId", phonearraylist.get(position).getUserId());
                intent.putExtra("objectId", phonearraylist.get(position).getObjectId());
                intent.putExtra("views", intentviews);

                context.startActivity(intent);

            }
        });
        return view;
    }
}