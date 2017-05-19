package com.kimjunhong.bucketlist.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.kimjunhong.bucketlist.R;
import com.kimjunhong.bucketlist.adapter.BucketAdapter;

/**
 * Created by INMA on 2017. 5. 12..
 */

public class BucketTouchHelper extends ItemTouchHelper.SimpleCallback {
    private BucketAdapter adapter;
    private RecyclerView recyclerView;

    public BucketTouchHelper(BucketAdapter bucketAdapter, RecyclerView recyclerView) {
        // Up|Down DRAG 방향, Left|Right SWIPE 방향
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = bucketAdapter;
        this.recyclerView = recyclerView;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        /* itemView 작업 완료 후 설정 */
        View itemView = viewHolder.itemView;
        itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        /* itemView DRAG */
        View itemView = viewHolder.itemView;
        itemView.setBackgroundColor(Color.parseColor("#EEEEEE"));

        adapter.swapBucket(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        /* itemView SWIPE */
        int position = viewHolder.getAdapterPosition();

        // position은 0부터 시작하는데 sequence는 1부터 시작하므로 position + 1
        // View가 지워지는 index랑 DB가 지워지는 index가 다름
        if (direction == ItemTouchHelper.LEFT) {
            // 버킷 삭제
            adapter.deleteBucket(recyclerView, position + 1);
        } else {
            // 버킷 완료, processing -> completed
            adapter.completeBucket(recyclerView, position + 1);
        }

        Log.v("log", "swiped position : " + position + 1);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        /* Child 설정 */
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Get RecyclerView item from the ViewHolder
            View itemView = viewHolder.itemView;

            // calculate for icon's height & width
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            Paint p = new Paint();
            Bitmap icon;

            if (dX > 0) {
                // icon Resource 가져오기, 여기서는 recyclerView를 활용하여 Context를 가져왔음, 무슨 방법이던 Context만 가져오면 되는데 여기서는 외부 메서드 안 만들기 위해 이렇게 작성
                icon = BitmapFactory.decodeResource(recyclerView.getContext().getResources(), R.drawable.icon_complete);

                /* Set your color for positive displacement */
                p.setColor(Color.parseColor("#4CAF50"));
                // p.setARGB(255, 0, 255, 0);

                // Draw Rect with varying right side, equal to displacement dX
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                c.drawRect(background, p);

                // icon과 동시에 사라지게 하기 위해서 clipRect 함
                c.clipRect(background);

                // Set the image icon for Right swipe
                RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);

            } else {
                icon = BitmapFactory.decodeResource(recyclerView.getContext().getResources(), R.drawable.icon_delete);

                /* Set your color for negative displacement */
                p.setColor(Color.parseColor("#F44336"));
                // p.setARGB(255, 255, 0, 0);

                // Draw Rect with varying left side, equal to the item's right side
                // plus negative displacement dX
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, p);

                // icon과 동시에 사라지게 하기 위해서 clipRect 함
                c.clipRect(background);

                // Set the image icon for Left swipe
                RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            }

            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);

        } else {
            // 상,하 드래그시 부드럽게 이동
            // super.onChildDraw()
            // c.restore();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
