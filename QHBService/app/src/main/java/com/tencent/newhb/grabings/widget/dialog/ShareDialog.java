package com.tencent.newhb.grabings.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.newhb.grabings.Config;
import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.ShareHelper;
import com.tencent.newhb.grabings.util.SharePreferenceHelper;

public class ShareDialog extends Dialog {

    private Activity _context;
    private TextView _downLoadUrlView;
    private ImageView _rcodeImageView;

    private String _downLoadUrl;
    private String _rcodeUrl;

    public ShareDialog(Activity activity) {
        super(activity);
        _context = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_share);

        setCanceledOnTouchOutside(true);
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        _downLoadUrlView = (TextView) findViewById(R.id.share_download_url);
        _rcodeImageView = (ImageView) findViewById(R.id.share_rcode);

        _downLoadUrl = SharePreferenceHelper.getSharePreferenceFromString(getContext(), Config.APP_CONFIG, Config.KEY_DOWNLOAD_URL);

        if (_downLoadUrl.isEmpty()) {
            _downLoadUrl = Config.DEFAULT_DOWNLOAD_URL;
        }

        _downLoadUrlView.setText(_downLoadUrl);

        _rcodeUrl = SharePreferenceHelper.getSharePreferenceFromString(getContext(), Config.APP_CONFIG, Config.KEY_SHARE_RCODE);

        if (_rcodeUrl.isEmpty()) {
            Glide.with(_context).load(R.drawable.icon_rcode).into(_rcodeImageView);
        } else {
            Glide.with(_context).load(_rcodeUrl).into(_rcodeImageView);
        }

        _downLoadUrlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(_downLoadUrlView.getText().toString());
                Toast.makeText(getContext(), "复制成功，可以发给朋友们了。", Toast.LENGTH_LONG).show();
            }
        });

        Button actionButton = (Button) findViewById(R.id.share_action_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ShareHelper.shareMsg(_context, getContext().getString(R.string.app_name), getContext().getString(R.string.share_title),
                        String.format(getContext().getString(R.string.share_content), _downLoadUrl), 0);
            }
        });
    }
}
