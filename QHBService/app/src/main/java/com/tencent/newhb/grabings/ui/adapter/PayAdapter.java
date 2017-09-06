package com.tencent.newhb.grabings.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.entity.Payment;
import com.tencent.newhb.grabings.ui.holder.PayHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MSI05 on 2017/5/12.
 */
public class PayAdapter extends RecyclerView.Adapter<PayHolder> {

    private PaymentItemClickListener _paymentItemClickListener;
    private ArrayList<Payment> _payments = new ArrayList<>();
    private Context _context;

    public PayAdapter(Context context, ArrayList<Payment> payments) {
        this._context = context;
        this._payments = payments;
    }

    @Override
    public PayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PayHolder(LayoutInflater.from(_context).inflate(R.layout.item_payment, parent, false));
    }

    @Override
    public void onBindViewHolder(final PayHolder holder, int position) {
        if (_payments != null && _payments.size() > 0) {

            final Payment payment = _payments.get(position);

            if (payment.getCode().contains("weixin")) {
                holder.mLogoView.setImageResource(R.drawable.icon_wx);
            } else if (payment.getCode().contains("alipay")) {
                holder.mLogoView.setImageResource(R.drawable.icon_zfb);
            }
            holder.mTextView.setText(payment.getName());

            holder.mCheckBox.setChecked(payment.isSelected());

            holder.mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (_paymentItemClickListener != null) {
                        _paymentItemClickListener.onItemClick(payment.getId(), payment.getCode());
                    }
                }
            });

            holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_paymentItemClickListener != null) {
                        _paymentItemClickListener.onItemClick(payment.getId(), payment.getCode());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return _payments.size();
    }

    //添加数据
    public void setData(List<Payment> newDatas) {
        _payments.clear();
        _payments.addAll(newDatas);
        notifyDataSetChanged();
    }

    public interface PaymentItemClickListener{
        void onItemClick(String id, String code);
    }

    public void setPaymentItemClickListener(PaymentItemClickListener paymentItemClickListener) {
        this._paymentItemClickListener = paymentItemClickListener;
    }
}
