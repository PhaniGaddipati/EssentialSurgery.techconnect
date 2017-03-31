package org.techconnect.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.techconnect.R;

import java.util.ArrayList;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

/**
 * Created by doranwalsten on 2/8/17.
 */

public class SkillListAdapter extends BaseAdapter {

    private ArrayList<String> skills_library = new ArrayList<>();
    private ArrayList<String> skills = new ArrayList<>();
    LayoutInflater inflater;
    Context context;

    public SkillListAdapter(Context context, List<String> skills) {
        for (String s: skills) {
            this.skills.add(s);
            this.skills_library.add(s);
        }
        this.context = context;
        this.skills.add(this.context.getString(R.string.other));
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return this.skills.size();
    }

    @Override
    public String getItem(int i) {
        return this.skills.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(android.R.layout.simple_list_item_1,null);
            ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(i));
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
            ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(i));
        }

        return view;
    }

    private static class ViewHolder {
        private View view;
    }

    //Filtering
    public void filter(String text) {
        skills.clear();
        if (!TextUtils.isEmpty(text)) {
            //Add any skills with partial exact match
            for (String s: skills_library) {
                if (s.contains(text)) {
                    skills.add(s);
                }
            }
            ExtractedResult best = FuzzySearch.extractOne(text, skills_library);
            if (!skills.contains(best.getString()) && best.getScore() > 50) {
                skills.add(best.getString());
            }
        } else {
            for (String s: skills_library) {
                skills.add(s);
            }
        }
        skills.add(context.getString(R.string.other));
        Log.d("Skill List",skills.toString());
        notifyDataSetChanged();
    }
}
