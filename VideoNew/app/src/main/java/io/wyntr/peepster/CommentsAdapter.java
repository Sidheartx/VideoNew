package io.wyntr.peepster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siddharth on 4/4/16.
 */
public class CommentsAdapter extends BaseAdapter {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private List<ParseComments> parseCommentsList = null;
    private ArrayList<ParseComments> arraylist;
    public CommentsAdapter(Context context,
                           List<ParseComments> parseCommentsList) {
        this.context = context;
        this.parseCommentsList = parseCommentsList;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<ParseComments>();
        this.arraylist.addAll(parseCommentsList);
        imageLoader = new ImageLoader(context);
    }
    public class ViewHolder {
        TextView comment;
    }
    @Override
    public int getCount() {
        return parseCommentsList.size();
    }
    @Override
    public Object getItem(int position) {
        return parseCommentsList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.comments_layout, null);
            // Locate the TextViews in listview_item.xml
            holder.comment = (TextView) view.findViewById(R.id.comments);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.comment.setText(parseCommentsList.get(position).getComments());
        return view;
    }
}