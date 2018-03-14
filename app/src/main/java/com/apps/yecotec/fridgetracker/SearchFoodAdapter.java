package com.apps.yecotec.fridgetracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.yecotec.fridgetracker.data.Food;
import com.apps.yecotec.fridgetracker.utils.DBUtils;
import com.apps.yecotec.fridgetracker.utils.FoodUtils;

import static com.apps.yecotec.fridgetracker.SectionAdapter.FOOD_ID;
import static com.apps.yecotec.fridgetracker.SectionAdapter.REQUEST_FOR_ACTIVITY_CODE;

/**
 * Created by kenruizinoue on 10/5/17.
 */

public class SearchFoodAdapter extends RecyclerView.Adapter<SearchFoodAdapter.SearchFoodAdapterViewHolder> {

    Food[] foods;
    Context context;

    public SearchFoodAdapter(Food[] foods, Context context) {
        this.foods = foods;
        this.context = context;
    }

    @Override
    public SearchFoodAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.food_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        SearchFoodAdapterViewHolder viewHolder = new SearchFoodAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchFoodAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return foods.length;
    }

    public class SearchFoodAdapterViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView circleBackGround;
        ImageView foodImageView;
        TextView foodNameTextView;
        TextView foodQuantityTextView;
        ImageView calendarImageView;
        TextView expirationDateTextView;

        public SearchFoodAdapterViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            circleBackGround = (ImageView) itemView.findViewById(R.id.image_view_circle_back_ground);
            foodImageView = (ImageView) itemView.findViewById(R.id.image_view_food);
            foodNameTextView = (TextView) itemView.findViewById(R.id.text_view_food_name);
            foodQuantityTextView = (TextView) itemView.findViewById(R.id.text_view_food_quantity);
            calendarImageView = (ImageView) itemView.findViewById(R.id.image_view_calendar);
            expirationDateTextView = (TextView) itemView.findViewById(R.id.text_view_expiration_date);
        }

        void bind(int position) {
            //set up attributes
            Drawable circleDrawable = context.getResources().getDrawable(R.drawable.circle_color_1, null);
            circleDrawable.setColorFilter(new PorterDuffColorFilter(new DBUtils(context).getSectionById(foods[position].sectionId).sectionColor, PorterDuff.Mode.SRC_IN));
            circleBackGround.setBackground(circleDrawable);

            foodImageView.setImageResource(FoodUtils.getFoodAsset(foods[position].foodCategory));

            foodNameTextView.setText(foods[position].foodName);
            foodQuantityTextView.setText(foods[position].foodQuantity + " " + new FoodUtils(context).getUnitString(foods[position].foodUnit));
            if(foods[position].foodUnit == 0) foodQuantityTextView.setText((int) foods[position].foodQuantity + " " + new FoodUtils(context).getUnitString(foods[position].foodUnit));

            Drawable calendarDrawable = context.getResources().getDrawable(R.drawable.ic_calendar, null).mutate();
            calendarDrawable.setColorFilter(new PorterDuffColorFilter(new DBUtils(context).getSectionById(foods[position].sectionId).sectionColor, PorterDuff.Mode.SRC_IN));
            calendarImageView.setImageDrawable(calendarDrawable);

            expirationDateTextView.setText(foods[position].foodExpireDate);

            final int finalPosition = position;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(FOOD_ID, foods[finalPosition].foodId);
                    ((Activity) context).startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE);
                }
            });
        }
    }
}
