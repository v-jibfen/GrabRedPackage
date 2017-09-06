package com.tencent.newhb.grabings.job;

import android.content.Context;

import com.tencent.newhb.grabings.Config;
import com.tencent.newhb.grabings.QiangHongBaoService;

public abstract class BaseAccessbilityJob implements AccessbilityJob {

    private QiangHongBaoService service;

    @Override
    public void onCreateJob(QiangHongBaoService service) {
        this.service = service;
    }

    public Context getContext() {
        return service.getApplicationContext();
    }

    public Config getConfig() {
        return service.getConfig();
    }

    public QiangHongBaoService getService() {
        return service;
    }
}
