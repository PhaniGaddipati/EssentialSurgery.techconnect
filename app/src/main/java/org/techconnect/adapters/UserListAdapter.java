package org.techconnect.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.techconnect.R;
import org.techconnect.model.User;
import org.techconnect.views.UserListItemView;

import java.util.List;

/**
 * Created by doranwalsten on 11/13/16.
 */

public class UserListAdapter extends BaseAdapter {

    private List<User> users;

    public UserListAdapter() {

    }

    public void setUsers(List<User> u) {
        Log.d("Directory Setup", "Setting the List");
        this.users = u;
    }

    public void addUser(User u) {
        this.users.add(u);
    }

    @Override
    public int getCount() {
        if (this.users == null) {
            return 0;
        } else {
            return this.users.size();
        }
    }

    @Override
    public User getItem(int i) {
        if (this.users == null) {
            return null;
        } else {
            return this.users.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) { //Has not been initialized
            holder = new ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_view_user, null);
            holder.user_view = (UserListItemView) view;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.user_view.setUser(this.users.get(i));
        holder.user_view.updateViews();
        return holder.user_view;
    }

    private static class ViewHolder {
        private UserListItemView user_view;
    }
}


