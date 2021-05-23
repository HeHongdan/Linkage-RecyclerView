package com.kunminx.linkage.defaults;
/*
 * Copyright (c) 2018-present. KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.kunminx.linkage.R;
import com.kunminx.linkage.adapter.viewholder.LinkagePrimaryViewHolder;
import com.kunminx.linkage.contract.ILinkagePrimaryAdapterConfig;

/**
 * 默认：父适配器配置。
 *
 * Create by KunMinX at 19/5/8
 */
public class DefaultLinkagePrimaryAdapterConfig implements ILinkagePrimaryAdapterConfig {

    private static final int MARQUEE_REPEAT_LOOP_MODE = -1;
    private static final int MARQUEE_REPEAT_NONE_MODE = 0;

    /** 上下文。 */
    private Context mContext;
    /** 父Item绑定的事件。 */
    private OnPrimaryItemBindListener mListener;
    /** 父Item点击的事件。 */
    private OnPrimaryItemClickListner mClickListener;

    /**
     * 设置父监听器。
     *
     * @param listener 父Item绑定的事件。
     * @param clickListener 父Item点击的事件。
     */
    public void setListener(OnPrimaryItemBindListener listener,
                            OnPrimaryItemClickListner clickListener) {
        mListener = listener;
        mClickListener = clickListener;
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.default_adapter_linkage_primary;
    }

    @Override
    public int getGroupTitleViewId() {
        return R.id.tv_group;
    }

    @Override
    public int getRootViewId() {
        return R.id.layout_group;
    }

    @Override
    public void onBindViewHolder(LinkagePrimaryViewHolder holder, boolean selected, String title) {
        TextView tvTitle = ((TextView) holder.mGroupTitle);
        tvTitle.setText(title);

        tvTitle.setBackgroundColor(mContext.getResources().getColor(selected ? R.color.colorPurple : R.color.colorWhite));
        tvTitle.setTextColor(ContextCompat.getColor(mContext, selected ? R.color.colorWhite : R.color.colorGray));
        tvTitle.setEllipsize(selected ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
        tvTitle.setFocusable(selected);
        tvTitle.setFocusableInTouchMode(selected);
        //跑马灯效果
        tvTitle.setMarqueeRepeatLimit(selected ? MARQUEE_REPEAT_LOOP_MODE : MARQUEE_REPEAT_NONE_MODE);

        if (mListener != null) {
            mListener.onBindViewHolder(holder, title);
        }
    }

    @Override
    public void onItemClick(LinkagePrimaryViewHolder holder, View view, String title) {
        if (mClickListener != null) {
            mClickListener.onItemClick(holder, view, title);
        }
    }



    /**
     * 父Item点击的事件。
     */
    public interface OnPrimaryItemClickListner {
        /**
         * we suggest you get position by holder.getAdapterPosition
         * 我们建议您通过holder.getAdapterPosition获取位置。
         *
         * @param holder primaryHolder
         * @param view   view
         * @param title  groupTitle
         */
        void onItemClick(LinkagePrimaryViewHolder holder, View view, String title);
    }

    /**
     * 父Item绑定的事件。
     */
    public interface OnPrimaryItemBindListener {
        /**
         * Note: Please do not override rootView click listener in here, because of linkage selection rely on it.
         * and we suggest you get position by holder.getAdapterPosition
         * 请不要在此处覆盖rootView click监听器，因为链接选择依赖它。 我们建议您通过holder.getAdapterPosition获取位置。
         *
         * @param primaryHolder primaryHolder 父Holder。
         * @param title         groupTitle 父(组)的标题。
         */
        void onBindViewHolder(LinkagePrimaryViewHolder primaryHolder, String title);
    }
}
