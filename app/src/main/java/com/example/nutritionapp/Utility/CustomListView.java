package com.example.nutritionapp.Utility;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nutritionapp.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomListView extends ArrayAdapter<String>{
    private String[] activity;
    private String[] description;
    private Integer[] image;
    private Activity context;

    public CustomListView(Context context, String[] activity, String[] description, Integer[] image) {
        super(context, R.layout.listview_layout, activity);
        this.context = (Activity) context;
        this.activity = activity;
        this.description = description;
        this.image = image;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder;
        if (r == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            r = inflater.inflate(R.layout.listview_layout, null, true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) r.getTag();
        }
        viewHolder.mImage.setImageResource(image[position]);
        viewHolder.mActivity.setText(activity[position]);
        viewHolder.mDescrition.setText(description[position]);

        return r;
    }

    class ViewHolder {
        TextView mActivity, mDescrition;
        ImageView mImage;

        ViewHolder(View v) {
            mActivity = v.findViewById(R.id.title);
            mDescrition = v.findViewById(R.id.description);
            mImage = v.findViewById(R.id.image);
        }
    }
}
