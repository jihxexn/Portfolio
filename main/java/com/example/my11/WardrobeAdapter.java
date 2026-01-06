package com.example.my11;

import android.content.Context;
import android.view.*;
import android.widget.*;

import java.util.List;

public class WardrobeAdapter extends BaseAdapter {

    private Context context;
    private List<String> items;

    public WardrobeAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String itemName = items.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_grid_item, parent, false);
        }

        ImageView itemImage = convertView.findViewById(R.id.itemImage);
        TextView itemText = convertView.findViewById(R.id.itemName);

        int resId = context.getResources().getIdentifier(itemName, "drawable", context.getPackageName());
        if (resId != 0) {
            itemImage.setImageResource(resId);
        } else {
            itemImage.setImageResource(android.R.color.transparent);
        }

        itemText.setText(itemName);

        return convertView;
    }
}
