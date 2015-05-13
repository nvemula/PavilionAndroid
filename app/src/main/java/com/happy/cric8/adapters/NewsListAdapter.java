package com.happy.cric8.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.happy.cric8.R;
import com.happy.cric8.data.NewsItem;

import java.util.List;

/**
 * Created by nayan on 12/25/14.
 */
public class NewsListAdapter extends ArrayAdapter<NewsItem> {
    private Context context;
    private List<NewsItem> values;

    static class ViewHolder {

        TextView heading;
        TextView articleSource;
        TextView description;

    }


    public NewsListAdapter(Context context,List<NewsItem> values) {
        super(context, R.layout.news_list_row, values);
        this.context=context;
        this.values=values;
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(rowView==null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = layoutInflater.inflate(R.layout.news_list_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.heading = (TextView) rowView.findViewById(R.id.heading);
            viewHolder.articleSource = (TextView) rowView.findViewById(R.id.article_source);
            viewHolder.description = (TextView) rowView.findViewById(R.id.description);

            rowView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)rowView.getTag();


        }

        NewsItem newsItem = (NewsItem) getItem(position);


        viewHolder.heading.setText(values.get(position).heading);
        viewHolder.articleSource.setText(values.get(position).shortLink);
        viewHolder.description.setText(values.get(position).shortIntro);
        return rowView;
    }
}
