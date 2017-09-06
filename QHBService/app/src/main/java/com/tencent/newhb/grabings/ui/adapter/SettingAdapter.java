package com.tencent.newhb.grabings.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tencent.newhb.grabings.Config;
import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.entity.Plans;
import com.tencent.newhb.grabings.ui.holder.SettingHolder;
import com.tencent.newhb.grabings.util.ScreenUtils;
import com.tencent.newhb.grabings.util.SharePreferenceHelper;

import java.util.ArrayList;
import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingHolder>{

    private Context _context;
    private ArrayList<Plans> _plans = new ArrayList<>();
    private SettingItemClickListener _settingItemClickListener;

    public SettingAdapter(Context context, ArrayList<Plans> planses) {
        this._context = context;
        this._plans = planses;
    }

    @Override
    public SettingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SettingHolder(LayoutInflater.from(_context).inflate(R.layout.item_setting, parent, false));
    }

    @Override
    public void onBindViewHolder(final SettingHolder holder, final int position) {
        Log.d("SettingAdapter", "onBindViewHolder position : " + position);
        if (_plans != null && _plans.size() > 0) {
            final Plans plans = _plans.get(position);
            holder._typeTextView.setText(_plans.get(position).getName());

            initSpinner(holder._spinnerView, plans.getWords());

            boolean isChecked;
            switch (plans.getWords()) {
                case Config.KEY_ENABLE_QQ:
                case Config.KEY_ENABLE_WECHAT:
                case Config.KEY_SETTING_MODE_FANHUI:
                case Config.KEY_SETTING_MODE_MUSICTISHI:
                case Config.KEY_SETTING_MODE_TONGZHILAN:
                case Config.KEY_SETTING_MODE_ZIDONGQIANG:
                    isChecked = getConfig().getSettingPreferenceWithTrue(plans.getWords());
                    break;
                default:
                    isChecked = getConfig().getSettingPreferenceWithFalse(plans.getWords());
                    break;
            }

            if (isChecked) {
                holder._typeSwitchView.setChecked(true);
            } else {
                holder._typeSwitchView.setChecked(false);
            }
            if (Config.getConfig(_context).getSettingPreferenceWithFalse(Config.KEY_MODE_SPECIAL_USER) || plans.getActive() == 1) {

                if (!plans.getWords().equals(Config.KEY_SETTING_MODE_YONGJIU)) {
                    //开通
                    holder._actionView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //根据本地的SharePreference值来判断是否选中\
                            holder._typeSwitchView.setChecked(!holder._typeSwitchView.isChecked());
                            _settingItemClickListener.onItemClick(plans, true);
                        }
                    });
                } else {
                    holder._actionView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }

            } else {
                //未开通，要钱
                holder._actionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       //显示要钱的dialog
                        _settingItemClickListener.onItemClick(plans, false);
                    }
                });
            }

            if (plans.getWords().equals(Config.KEY_SETTING_MODE_YONGJIU)
                    || plans.getWords().equals(Config.KEY_NOTIFICATION_SERVICE_ENABLE)) {
                holder._typeTextView.setTextColor(_context.getResources().getColor(R.color.colorPrimary));
                TextPaint tp = holder._typeTextView.getPaint();
                tp.setFakeBoldText(true);
            } else {
                holder._typeTextView.setTextColor(_context.getResources().getColor(R.color.white_30));
                TextPaint tp = holder._typeTextView.getPaint();
                tp.setFakeBoldText(false);
            }

            if (plans.getWords().equals(Config.KEY_SETTING_MODE_YONGJIU)
                    || plans.getWords().equals(Config.KEY_SETTING_MODE_ZIDONGQIANG)) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder._itemView.getLayoutParams();
                layoutParams.setMargins(0, ScreenUtils.dip2px(_context, 15), 0, 0);
                holder._rootView.setLayoutParams(layoutParams);
            } else {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder._itemView.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);
                holder._rootView.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public int getItemCount() {
        return _plans.size();
    }

    public interface SettingItemClickListener {
        void onItemClick(Plans plans, boolean isCanCheck);
    }

    public void setSettingItemClickListener(SettingItemClickListener settingItemClickListener) {
        this._settingItemClickListener = settingItemClickListener;
    }

    //添加数据
    public void setData(ArrayList<Plans> data) {
        if (data != null) {
            _plans = data;
        } else {
            _plans.clear();
        }

        notifyDataSetChanged();
    }

    public Config getConfig() {
        return Config.getConfig(_context);
    }

    private void initSpinner(Spinner spinner, String word) {
        List<String> list = new ArrayList<>();
        ArrayAdapter arrayAdapter = null;

        switch (word) {
            case Config.KEY_SETTING_MODE_SAOLEI:
                String[] strings_ray = _context.getResources().getStringArray(R.array.setting_ray_value);
                for (int i = 0; i < strings_ray.length; i ++) {
                    list.add(strings_ray[i]);
                }

                arrayAdapter = new ArrayAdapter(_context, R.layout.custom_list_item, list);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(arrayAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SharePreferenceHelper.saveSharePreferenceFromInteger(_context, Config.APP_CONFIG, Config.KEY_SETTING_MODE_SAOLEI, position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spinner.setSelection(SharePreferenceHelper.getSharePreferenceFromInteger(_context, Config.APP_CONFIG, Config.KEY_SETTING_MODE_SAOLEI));
                spinner.setVisibility(View.VISIBLE);
                break;

            case Config.KEY_SETTING_MODE_NIUNIU:
                String[] strings_cattle = _context.getResources().getStringArray(R.array.setting_cattle_value);
                for (int i = 0; i < strings_cattle.length; i ++) {
                    list.add(strings_cattle[i]);
                }

                arrayAdapter = new ArrayAdapter(_context, R.layout.custom_list_item, list);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(arrayAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SharePreferenceHelper.saveSharePreferenceFromInteger(_context, Config.APP_CONFIG, Config.KEY_SETTING_MODE_NIUNIU, position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spinner.setSelection(SharePreferenceHelper.getSharePreferenceFromInteger(_context, Config.APP_CONFIG, Config.KEY_SETTING_MODE_NIUNIU));
                spinner.setVisibility(View.VISIBLE);
                break;

            case Config.KEY_SETTING_MODE_WEIHAOKONGZHI:
                String[] strings_last = _context.getResources().getStringArray(R.array.setting_last_number_value);
                for (int i = 0; i < strings_last.length; i ++) {
                    list.add(strings_last[i]);
                }

                arrayAdapter = new ArrayAdapter(_context, R.layout.custom_list_item, list);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(arrayAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SharePreferenceHelper.saveSharePreferenceFromInteger(_context, Config.APP_CONFIG, Config.KEY_SETTING_MODE_WEIHAOKONGZHI, position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spinner.setSelection(SharePreferenceHelper.getSharePreferenceFromInteger(_context, Config.APP_CONFIG, Config.KEY_SETTING_MODE_WEIHAOKONGZHI));
                spinner.setVisibility(View.VISIBLE);
                break;

            default:
                spinner.setVisibility(View.GONE);
                break;
        }


    }
}
