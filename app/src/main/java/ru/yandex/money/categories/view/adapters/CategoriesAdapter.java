package ru.yandex.money.categories.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.money.categories.R;
import ru.yandex.money.categories.data.entity.Category;
import ru.yandex.money.categories.helpers.Utils;

public class CategoriesAdapter extends BaseAdapter {

    private final List<Category> items = new ArrayList<>();

    private LayoutInflater inflater;

    public CategoriesAdapter(Context context, List<Category> items) {
        super();
        inflater = LayoutInflater.from(context);
        setItems(items);
    }

    public void setItems(List<Category> items) {
        this.items.clear();
        if (!Utils.isEmpty(items)) {
            this.items.addAll(items);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Category getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.list_item_category, parent, false);
            holder = new ViewHolder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.setItem(getItem(position));

        return rowView;
    }

    private class ViewHolder {
        public TextView title;

        public ViewHolder(View rowView) {
            title = (TextView) rowView.findViewById(R.id.title);
        }

        public void setItem(Category item) {
            title.setText(item.getTitle());
        }
    }
}