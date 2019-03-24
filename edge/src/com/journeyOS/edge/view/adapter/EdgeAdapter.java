/*
 * Copyright (c) 2019 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.edge.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.database.edge.Edge;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.edge.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EdgeAdapter extends RecyclerView.Adapter<EdgeAdapter.EdgeViewHolder> {
    private static final String TAG = EdgeAdapter.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    EdgeDirection mEdgeDirection;
    List<Edge> mEdges;
    Context mContext;
    int mCount = 0;

    public EdgeAdapter(Context context, EdgeDirection direction, List<Edge> edges) {
        this.layoutInflater = LayoutInflater.from(context);
        mContext = context;
        mEdgeDirection = direction;
        mEdges = edges;
        if (mEdges != null && mEdges.size() > 0) {
            mCount = mEdges.size();
        }
//        Collections.sort(edges, new ComparatorDesc());
    }

    @Override
    public EdgeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean landscape = EdgeDirection.UP.equals(mEdgeDirection);
        RelativeLayout layout = (RelativeLayout) layoutInflater.inflate(landscape ? R.layout.edge_item_landscape : R.layout.edge_item_portrait, parent, false);
        EdgeViewHolder holder = new EdgeViewHolder(layout);
        holder.mAdapter = this;
        return holder;
    }

    @Override
    public void onBindViewHolder(EdgeViewHolder holder, int position) {
        if (mCount > position) {
            Edge edge = mEdges.get(position);
            Drawable drawable = null;
            String name = null;

            boolean isAppExisted = AppUtils.isPackageExisted(mContext, edge.packageName);
            if (isAppExisted) {
                drawable = AppUtils.getAppIcon(mContext, edge.packageName);
                name = AppUtils.getAppName(mContext, edge.packageName, Constant.LENGTH);
            }

            if (drawable != null) {
//                Bitmap bitmap = UIUtils.drawableToBitmap(drawable);
//                Bitmap circularBitmap = UIUtils.getCircularBitmap(bitmap);
//                holder.icon.setImageBitmap(circularBitmap);
                holder.icon.setImageDrawable(drawable);
            }
            if (name != null) {
                boolean itemText = SpUtils.getInstant().getBoolean(Constant.EDGE_ITEM_TEXT, Constant.EDGE_ITEM_TEXT_DEFAULT);
                holder.name.setText(itemText ? name : "");
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = SpUtils.getInstant().getInt(Constant.EDGE_CONUT, Constant.EDGE_CONUT_DEFAULT);
        return count;
    }

    public void onClicked(int postion) {
        LogUtils.d(TAG, "onClicked() called with: postion = [" + postion + "]");
        if (mListener != null) {
            mListener.onItemClick(postion + 1);
        }
    }

    public void onLongClicked(int postion) {
        LogUtils.d(TAG, "onLongClicked() called with: postion = [" + postion + "]");
        if (mListener != null) {
            mListener.onItemLongClick(postion + 1);
        }
    }

    private OnEdgeAdapterListener mListener;

    public void setOnEdgeAdapterListener(OnEdgeAdapterListener listener) {
        mListener = listener;
    }

    public interface OnEdgeAdapterListener {
        void onItemClick(int postion);

        void onItemLongClick(int postion);
    }


    class EdgeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        EdgeAdapter mAdapter;
        CircleImageView icon;
        TextView name;

        public EdgeViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.icon = (CircleImageView) itemView.findViewById(R.id.icon);
            this.name = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public void onClick(View view) {
            mAdapter.onClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            mAdapter.onLongClicked(getAdapterPosition());
            return true;
        }
    }

//    public static class ComparatorDesc implements Comparator<Edge> {
//        @Override
//        public int compare(Edge a, Edge b) {
//            return Integer.compare(CoreManager.getDefault().getImpl(IEdgeProvider.class).getPostion(a.item), CoreManager.getDefault().getImpl(IEdgeProvider.class).getPostion(b.item));
//        }
//    }
}
