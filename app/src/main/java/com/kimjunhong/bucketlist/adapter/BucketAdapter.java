package com.kimjunhong.bucketlist.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kimjunhong.bucketlist.R;
import com.kimjunhong.bucketlist.item.BucketItem;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INMA on 2017. 5. 12..
 */

public class BucketAdapter extends RecyclerView.Adapter<BucketAdapter.ViewHolder> {
    Context context;
    List<BucketItem> items;
    Dialog dialog;

    public BucketAdapter(Context context, List<BucketItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public BucketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bucket, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BucketAdapter.ViewHolder holder, int position) {
        BucketItem item = items.get(position);
        holder.bucketLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // 커스텀 다이얼로그 가져오기
                View customLayout = View.inflate(context, R.layout.dialog_edit,null);
                // 빌더에 다이얼로그 적용
                builder.setView(customLayout);

                dialog = builder.create();
                // 빌더 크기 적용
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = 1000;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.show();

                // 디폴트 다이얼로그 투명화
                Window w = dialog.getWindow();
                w.setAttributes(lp);
                w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                //수정, 취소 클릭
                customLayout.findViewById(R.id.dialog_button_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "수정 되었습니다 :')", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                customLayout.findViewById(R.id.dialog_button_edit_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "취소 :^)", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        holder.bucketTitle.setText(item.getTitle());
        holder.bucketDate.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void addItem(int position) {
        items.add(new BucketItem("New Title", "New Date"));
        notifyItemInserted(items.size());
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

    public void swapItem(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void complete(RecyclerView recyclerView) {
        // Snackbar 텍스트 설정
        SpannableStringBuilder spanText = new SpannableStringBuilder();
        spanText.append("버킷을 ");
        int emphasisStart = spanText.length();
        spanText.append("완료");
        // 강조 텍스트 색상 (16진수 ARGB)
        spanText.setSpan(new ForegroundColorSpan(0xFF4CAF50), emphasisStart, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 강조 텍스트 bold
        spanText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), emphasisStart, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanText.append(" 했습니다");

        // 2초(LENGTH_SHORT) 뒤 완료 처리
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                완료 기능
                */
            }
        }, 2000);

        Snackbar s = Snackbar.make(recyclerView, spanText, Snackbar.LENGTH_SHORT).setAction("취소", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                취소 기능
                 */
            }
        });

        View v = s.getView();
        //v.setBackgroundColor(Color.parseColor("#4CAF50"));

        TextView text = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(Color.WHITE);

        TextView action = (TextView) v.findViewById(android.support.design.R.id.snackbar_action);
        action.setTextColor(Color.WHITE);

        s.show();
    }

    public void delete(View view) {
        // Snackbar 텍스트 설정
        SpannableStringBuilder spanText = new SpannableStringBuilder();
        spanText.append("버킷을 ");
        int emphasisStart = spanText.length();
        spanText.append("삭제");
        // 강조 텍스트 색상 (16진수 ARGB)
        spanText.setSpan(new ForegroundColorSpan(0xFFF44336), emphasisStart, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 강조 텍스트 bold
        spanText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), emphasisStart, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanText.append(" 했습니다");

        // 2초(LENGTH_SHORT) 뒤 데이터 삭제 처리
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                삭제 기능
                 */
            }
        }, 2000);


        Snackbar s = Snackbar.make(view, spanText, Snackbar.LENGTH_SHORT).setAction("취소", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                취소 기능
                 */
            }
        });

        View v = s.getView();
        //v.setBackgroundColor(Color.parseColor("#F44336"));

        TextView text = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(Color.WHITE);

        TextView action = (TextView) v.findViewById(android.support.design.R.id.snackbar_action);
        action.setTextColor(Color.WHITE);

        s.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bucket_layout) LinearLayout bucketLayout;
        @BindView(R.id.bucket_title) TextView bucketTitle;
        @BindView(R.id.bucket_date) TextView bucketDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
