package com.door43.translationstudio.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.door43.translationstudio.MainApplication;
import com.door43.translationstudio.R;
import com.door43.translationstudio.projects.Frame;
import com.door43.translationstudio.projects.Model;
import com.door43.translationstudio.rendering.USXRenderer;
import com.door43.translationstudio.util.AppContext;

/**
 * Created by jaicksninan on 4/10/15.
 */
public class FramesListAdapter  extends BaseAdapter {

    private final DisplayOption mDisplayOption;
    private MainApplication mContext;
    private  boolean mIndicateSelected;
    private Model[] mModels;

    public enum DisplayOption {
        SOURCE_TRANSLATION,
        TARGET_TRANSLATION,
        DRAFT_TRANSLATION
    }

    public FramesListAdapter(MainApplication c, Model[] models, DisplayOption option) {
        mContext = c;
        mModels = models;
        mIndicateSelected = true;
        mDisplayOption = option;
    }

    @Override
    public int getCount() {
        return mModels.length;
    }

    @Override
    public Frame getItem(int i) {
        return (Frame)mModels[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_frame_reader_list_item, null);
            holder.text = (TextView)v.findViewById(R.id.text);
            holder.title = (TextView)v.findViewById(R.id.title);
            v.setTag(holder);
        } else {
            holder = (ViewHolder)v.getTag();
        }

        // load correct frame text
        switch(mDisplayOption) {
            case TARGET_TRANSLATION:
                holder.text.setText(new USXRenderer().render(getItem(position).getTranslation().getText()));
                break;
            case DRAFT_TRANSLATION:
                // TODO: we need to finish implementing how drafts are loaded from the file system
                holder.text.setText("not implemented yet");
                break;
            case SOURCE_TRANSLATION:
            default:
                holder.title.setText(getItem(position).getTitle());
                holder.text.setText(new USXRenderer().render(getItem(position).getText()));
        }

        // set graphite fontface
        Typeface typeface;
        if(getItem(position).getSelectedSourceLanguage() != null) {
            typeface = AppContext.graphiteTypeface(getItem(position).getSelectedSourceLanguage());
        } else {
            // use english as default
            typeface = AppContext.graphiteTypeface(AppContext.projectManager().getLanguage("en"));
        }
        holder.title.setTypeface(typeface, 0);
        holder.text.setTypeface(typeface, 0);

        // set font size
        float fontsize = AppContext.typefaceSize();
        holder.title.setTextSize(fontsize);
        holder.text.setTextSize(fontsize);

        // color selection
        if(getItem(position).isSelected() && mIndicateSelected) {
            v.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
            holder.title.setTextColor(Color.WHITE);
            holder.text.setTextColor(Color.WHITE);
        } else {
            v.setBackgroundColor(Color.TRANSPARENT);
            holder.title.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.text.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        return v;
    }

    /**
     * Changes the dataset
     * @param models
     */
    public void changeDataSet(Model[] models) {
        mModels = models;
        notifyDataSetChanged();
    }

    /**
     * Provides better list performance
     */
    private static class ViewHolder {
        public TextView text;
        public TextView title;
    }
}
