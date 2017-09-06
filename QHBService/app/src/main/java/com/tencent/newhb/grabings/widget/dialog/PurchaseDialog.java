package com.tencent.newhb.grabings.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.entity.Payment;
import com.tencent.newhb.grabings.ui.adapter.PayAdapter;
import com.tencent.newhb.grabings.widget.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by MSI05 on 2016/12/30.
 */
public class PurchaseDialog extends Dialog {

    private PurchaseDialogClickListener _purchaseDialogClickListener;
    private String _dialogName;
    private String _dialogPrice;
    private ArrayList<Payment> _payments = new ArrayList<>();
    private RecyclerView _payListView;
    private PayAdapter _paymentAdapter;
    private String _payType;

    public PurchaseDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_pay);

        setCanceledOnTouchOutside(true);
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        ImageView close_iv = (ImageView) findViewById(R.id.close_iv);
        TextView nam_tv = (TextView) findViewById(R.id.name_tv);
        TextView price_tv = (TextView) findViewById(R.id.price_tv);
        TextView pay_iv = (TextView) findViewById(R.id.pay_iv);

        if (_payments != null && _payments.size() > 0) {
            for (int i = 0; i < _payments.size(); i ++) {
                if (i == 0) {
                    setPayType(_payments.get(i).getCode());
                    _payments.get(i).setSelected(true);
                } else {
                    _payments.get(i).setSelected(false);
                }
            }
        }

        _payListView = (RecyclerView) findViewById(R.id.pay_list_view);
        _payListView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL, R.drawable.recycle_item_divider);
        _payListView.addItemDecoration(itemDecoration);

        _paymentAdapter = new PayAdapter(getContext(), _payments);
        _payListView.setAdapter(_paymentAdapter);
        _paymentAdapter.setPaymentItemClickListener(new PayAdapter.PaymentItemClickListener() {
            @Override
            public void onItemClick(String id, String code) {
                for (int i = 0; i < _payments.size(); i ++) {
                    if (_payments.get(i).getId().equals(id)) {
                        _payments.get(i).setSelected(true);
                    } else {
                        _payments.get(i).setSelected(false);
                    }
                }
                _paymentAdapter.notifyDataSetChanged();
                setPayType(code);
            }
        });

        nam_tv.setText(_dialogName);
        price_tv.setText(_dialogPrice);

        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        pay_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (_purchaseDialogClickListener != null) {
                    _purchaseDialogClickListener.onConfirmPay(_payType);
                }
            }
        });
    }

    public PurchaseDialog setDialogName(String name) {
        this._dialogName = name;
        return this;
    }

    public PurchaseDialog setDialogPrice(String price) {
        this._dialogPrice = price;
        return this;
    }

    public PurchaseDialog setPayList(ArrayList<Payment> payments) {
        this._payments = payments;

        return this;
    }

    public void setPayType(String payType) {
        this._payType = payType;
    }

    public interface PurchaseDialogClickListener {
        void onConfirmPay(String payType);
    }

    public PurchaseDialog setPurchaseDialogClickListener(PurchaseDialogClickListener purchaseDialogClickListener){
        this._purchaseDialogClickListener = purchaseDialogClickListener;

        return this;
    }
}
