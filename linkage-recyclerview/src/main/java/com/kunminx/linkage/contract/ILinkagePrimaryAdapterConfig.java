package com.kunminx.linkage.contract;
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
import android.view.View;

import com.kunminx.linkage.adapter.viewholder.LinkagePrimaryViewHolder;

/**
 * 链接父Adapter的配置。
 *
 * Create by KunMinX at 19/5/8
 */
public interface ILinkagePrimaryAdapterConfig {

    /**
     * setContext.
     * 设置上下文。
     *
     * @param context context
     */
    void setContext(Context context);

    /**
     * get layout res id
     * 获取父布局的资源ID。
     *
     * @return layout res id. 父容器的ID。
     */
    int getLayoutId();

    /**
     * get textView id of layout
     * 获取显示父内容的文本控件。
     *
     * @return textView id of layout. 父内容的文本控件。
     */
    int getGroupTitleViewId();

    /**
     * get rootView id of layout
     * 获取父布局的容器ID。
     *
     * @return rootView id of layout.取父布局的容器ID。
     */
    int getRootViewId();

    /**
     * achieve the onBindViewHolder logic on outside
     * <p>
     * Note: Do not setOnClickListener in onBindViewHolder,
     * instead, you can deal with item click in method 'ILinkagePrimaryAdapterConfig.onItemSelected()'
     * or 'LinkageRecyclerView.OnPrimaryItemClickListener.onItemClick()'
     * <p>
     * and we suggest you get position by holder.getAdapterPosition
     *
     * 在外部实现onBindViewHolder逻辑
     * <p>
     *     注意：请勿在onBindViewHolder中设置setOnClickListener，而可以在方法'ILinkagePrimaryAdapterConfig.onItemSelected（）'或'LinkageRecyclerView.OnPrimaryItemClickListener.onItemClick（）'中处理项目单击。
     * <p>
     *     我们建议您通过holder.getAdapterPosition获取位置。
     *
     * @param holder   LinkagePrimaryViewHolder.链接父的ViewHolder。
     * @param title    title of this position. 父RV内容。
     * @param selected selected of this position. 选择的位置。
     */
    void onBindViewHolder(LinkagePrimaryViewHolder holder, boolean selected, String title);

    /**
     * on primary item clicked
     * and we suggest you get position by holder.getAdapterPosition
     * 在单击的父项目上，我们建议您通过holder.getAdapterPosition获得位置。
     *
     * @param holder LinkagePrimaryViewHolder. 链接父的ViewHolder。
     * @param view   itemView. 父Item的容器。
     * @param title  title of primary item. 父RV内容。
     */
    void onItemClick(LinkagePrimaryViewHolder holder, View view, String title);
}
