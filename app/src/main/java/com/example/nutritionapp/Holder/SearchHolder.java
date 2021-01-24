package com.example.nutritionapp.Holder;

import android.view.View;
import android.widget.TextView;
import com.example.nutritionapp.R;
import com.squareup.picasso.Picasso;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchHolder extends RecyclerView.ViewHolder {

    View mView;
    public SearchHolder(View itemView){
        super(itemView);
        mView = itemView;

    }

    public void setName(String name){
        TextView food_name = mView.findViewById(R.id.food_name);
        food_name.setText(name);
    }

    public void setCalorii(String calorii){
        TextView food_calorii = mView.findViewById(R.id.calorii);
        food_calorii.setText(calorii);
    }

    public void setImage(String url){
        CircleImageView circleImageView = mView.findViewById(R.id.food_image);
        Picasso.get().load(url).placeholder(R.drawable.dinner).into(circleImageView);
    }
}
