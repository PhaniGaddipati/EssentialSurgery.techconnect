package org.techconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.centum.techconnect.R;
import org.techconnect.views.CategoryListItemView;

import java.util.Iterator;
import java.util.Map;

/**
 * Used to represent a Map<String, Integer> nicely in a ListView
 * Created by doranwalsten on 1/8/17.
 */

public class CategoryListAdapter extends BaseAdapter {

    private Map<String, Integer> baseMap;

    public CategoryListAdapter() {

    }

    public void setBaseMap(Map<String, Integer> base_map) {
        this.baseMap = base_map;
    }

    @Override
    public int getCount() {
        if (this.baseMap == null) {
            return 0;
        } else {
            return this.baseMap.size();
        }
    }

    @Override
    public String[] getItem(int i) {
        if (this.baseMap == null) {
            return null;
        } else {
            Iterator<String> it = this.baseMap.keySet().iterator();
            for (int j = 0; j < i; j++) {
                it.next();
            }
            String key = it.next();
            return new String[]{key, String.format("%d", this.baseMap.get(key))};
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CategoryListAdapter.ViewHolder holder;
        if (view == null) { //Has not been initialized
            holder = new CategoryListAdapter.ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_view_category, viewGroup, false);
            holder.category_view = (CategoryListItemView) view;
            view.setTag(holder);
        } else {
            holder = (CategoryListAdapter.ViewHolder) view.getTag();
        }
        String[] entries = getItem(i);
        holder.category_view.setTitle(entries[0]);
        holder.category_view.setCount(entries[1]);
        holder.category_view.updateViews();
        return holder.category_view;
    }

    private static class ViewHolder {
        private CategoryListItemView category_view;
    }


}
