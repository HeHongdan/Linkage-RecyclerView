package com.kunminx.linkage.adapter.viewholder;
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


import android.view.View;

import androidx.annotation.NonNull;

import com.kunminx.linkage.contract.ILinkagePrimaryAdapterConfig;

/**
 * 父(组)RV的Holder。
 *
 * Create by KunMinX at 19/5/15
 */
public class LinkagePrimaryViewHolder extends BaseViewHolder {

    /** 显示父内容的文本控件。 */
    public View mGroupTitle;
    /** 父布局的容器ID。 */
    public View mLayout;
    /** 链接父Adapter的配置。 */
    private ILinkagePrimaryAdapterConfig mConfig;

    public LinkagePrimaryViewHolder(@NonNull View itemView, ILinkagePrimaryAdapterConfig config) {
        super(itemView);
        mConfig = config;
        mGroupTitle = itemView.findViewById(mConfig.getGroupTitleViewId());
        //need bind root layout by users, because rootLayout may not viewGroup, which can not getChild(0).
        //需要由用户绑定根布局，因为rootLayout可能不是viewGroup，而后者无法getChild（0）。
        mLayout = itemView.findViewById(mConfig.getRootViewId());
    }
}
