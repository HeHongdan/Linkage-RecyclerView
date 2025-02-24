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
import android.util.Log;
import android.widget.TextView;

import com.kunminx.linkage.R;
import com.kunminx.linkage.adapter.viewholder.LinkageSecondaryFooterViewHolder;
import com.kunminx.linkage.adapter.viewholder.LinkageSecondaryHeaderViewHolder;
import com.kunminx.linkage.adapter.viewholder.LinkageSecondaryViewHolder;
import com.kunminx.linkage.bean.BaseGroupedItem;
import com.kunminx.linkage.bean.DefaultGroupedItem;
import com.kunminx.linkage.contract.ILinkageSecondaryAdapterConfig;

/**
 * 默认：子适配器配置。
 *
 * Create by KunMinX at 19/5/8
 */
public class DefaultLinkageSecondaryAdapterConfig implements ILinkageSecondaryAdapterConfig<DefaultGroupedItem.ItemInfo> {

    /** 上下文。 */
    private Context mContext;
    /** 子Item绑定的事件。 */
    private OnSecondaryItemBindListener mItemBindListener;
    /** 头部绑定的事件。 */
    private OnSecondaryHeaderBindListener mHeaderBindListener;
    /** (页)脚部绑定的事件。 */
    private OnSecondaryFooterBindListener mFooterBindListener;
    /** 列数。 */
    private static final int SPAN_COUNT = 3;


    /**
     * 设置子Item的监听器。
     *
     * @param itemBindListener 子Item绑定的事件。
     * @param headerBindListener 头部绑定的事件。
     * @param footerBindListener (页)脚部绑定的事件。
     */
    public void setItemBindListener(OnSecondaryItemBindListener itemBindListener,
                                    OnSecondaryHeaderBindListener headerBindListener,
                                    OnSecondaryFooterBindListener footerBindListener) {
        mItemBindListener = itemBindListener;
        mHeaderBindListener = headerBindListener;
        mFooterBindListener = footerBindListener;
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public int getGridLayoutId() {
        return R.layout.default_adapter_linkage_secondary_grid;
    }

    @Override
    public int getLinearLayoutId() {
        return R.layout.default_adapter_linkage_secondary_linear;
    }

    @Override
    public int getHeaderLayoutId() {
        return R.layout.default_adapter_linkage_secondary_header;
    }

    @Override
    public int getFooterLayoutId() {
        return R.layout.default_adapter_linkage_secondary_footer;
    }

    @Override
    public int getHeaderTextViewId() {
        return R.id.secondary_header;
    }

    @Override
    public int getSpanCountOfGridMode() {
        return SPAN_COUNT;
    }

    @Override
    public void onBindViewHolder(LinkageSecondaryViewHolder holder,
                                 BaseGroupedItem<DefaultGroupedItem.ItemInfo> item) {

        ((TextView) holder.getView(R.id.level_2_title)).setText(item.info.getTitle());
        ((TextView) holder.getView(R.id.level_2_content)).setText(item.info.getContent());

        if (mItemBindListener != null) {
            mItemBindListener.onBindViewHolder(holder, item);
        }
    }

    @Override
    public void onBindHeaderViewHolder(LinkageSecondaryHeaderViewHolder holder,
                                       BaseGroupedItem<DefaultGroupedItem.ItemInfo> item) {

        ((TextView) holder.getView(R.id.secondary_header)).setText(item.header);

        if (mHeaderBindListener != null) {
            mHeaderBindListener.onBindHeaderViewHolder(holder, item);
        }
    }

    @Override
    public void onBindFooterViewHolder(LinkageSecondaryFooterViewHolder holder,
                                       BaseGroupedItem<DefaultGroupedItem.ItemInfo> item) {
        ((TextView) holder.getView(R.id.tv_secondary_footer)).setText(mContext.getString(R.string.the_end));

        if (mFooterBindListener != null) {
            mFooterBindListener.onBindFooterViewHolder(holder, item);
        }
    }

    /**
     * 子Item绑定的事件。
     */
    public interface OnSecondaryItemBindListener {
        /**
         * we suggest you get position by holder.getAdapterPosition
         * 我们建议您通过holder.getAdapterPosition获取位置。
         *
         * @param secondaryHolder 子 Holder。
         * @param item 子 Item。
         */
        void onBindViewHolder(LinkageSecondaryViewHolder secondaryHolder,
                              BaseGroupedItem<DefaultGroupedItem.ItemInfo> item);
    }

    /**
     * 头部绑定的事件。
     */
    public interface OnSecondaryHeaderBindListener {
        /**
         * we suggest you get position by holder.getAdapterPosition
         * 我们建议您通过holder.getAdapterPosition获取位置。
         *
         * @param headerHolder (子RV)头部 Holder。
         * @param item (子RV)头部 Item。
         */
        void onBindHeaderViewHolder(LinkageSecondaryHeaderViewHolder headerHolder,
                                    BaseGroupedItem<DefaultGroupedItem.ItemInfo> item);
    }

    /**
     * (页)脚部绑定的事件。
     */
    public interface OnSecondaryFooterBindListener {
        /**
         * we suggest you get position by holder.getAdapterPosition
         * 我们建议您通过holder.getAdapterPosition获取位置。
         *
         * @param footerHolder (页)脚的 Holder。
         * @param item (页)脚的 Item。
         */
        void onBindFooterViewHolder(LinkageSecondaryFooterViewHolder footerHolder,
                                    BaseGroupedItem<DefaultGroupedItem.ItemInfo> item);
    }
}
