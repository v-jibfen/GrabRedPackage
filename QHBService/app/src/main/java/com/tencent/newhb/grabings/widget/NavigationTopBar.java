package com.tencent.newhb.grabings.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.newhb.grabings.R;

/**
 * Created by MSI05 on 2016/12/29.
 */
public class NavigationTopBar extends Fragment {

    private Context mContext;

    private RelativeLayout mRoot;
    private TextView mTitle;
    private ImageView mLeftButton;
    private ImageView mRightButton;
    private TextView mRightText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.navigation_top_bar, container, false);

        mRoot = (RelativeLayout) view.findViewById(R.id.navigation_top_bar_root);
        mTitle = (TextView) view.findViewById(R.id.navigation_top_bar_title);
        mLeftButton = (ImageView) view.findViewById(R.id.navigation_top_bar_let_res);
        mRightButton = (ImageView) view.findViewById(R.id.navigation_top_bar_right_res);
        mRightText = (TextView) view.findViewById(R.id.navigation_top_bar_right_text);
        return view;
    }

    public void setViewVisibility(NAVIGATION_VIEWS views, final int visibility) {
        switch (views) {
            case LEFT:
                mLeftButton.setVisibility(visibility);
                break;
            case RIGHT:
                mRightButton.setVisibility(visibility);
                break;
            case RIGHT_TEXT:
                mRightText.setVisibility(visibility);
                break;
            case TITLE:
                mTitle.setVisibility(visibility);
                break;
            case ROOT:
                mRoot.setVisibility(visibility);
                break;
        }
    }

    public void setResource(NAVIGATION_VIEWS views, int resourceId) {
        switch (views) {
            case LEFT:
                mLeftButton.setVisibility(View.VISIBLE);
                mLeftButton.setImageResource(resourceId);
                break;
            case RIGHT:
                mRightButton.setVisibility(View.VISIBLE);
                mRightButton.setImageResource(resourceId);
                break;
            case RIGHT_TEXT:
                mRightText.setVisibility(View.VISIBLE);
                mRightText.setText(resourceId);
                break;
            case TITLE:
                mTitle.setVisibility(View.VISIBLE);
                mTitle.setText(resourceId);
                break;
            case ROOT:
                mRoot.setVisibility(View.VISIBLE);
                mRoot.setBackgroundColor(resourceId);
                break;
        }
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setText(NAVIGATION_VIEWS views, int id) {
        switch (views) {
            case RIGHT_TEXT:
                mRightText.setText(getString(id));
                break;
            case TITLE:
                mTitle.setText(getString(id));
                break;
        }
    }

    public void setClickListener(NAVIGATION_VIEWS views, View.OnClickListener listener) {
        switch (views) {
            case LEFT:
                mLeftButton.setOnClickListener(listener);
                break;
            case RIGHT:
                mRightButton.setOnClickListener(listener);
                break;
            case RIGHT_TEXT:
                mRightText.setOnClickListener(listener);
                break;
            case TITLE:
                mTitle.setOnClickListener(listener);
                break;
            case ROOT:
                mRoot.setOnClickListener(listener);
                break;
        }
    }

    public enum NAVIGATION_VIEWS {
        LEFT, RIGHT, RIGHT_TEXT, TITLE, ROOT
    }
}
