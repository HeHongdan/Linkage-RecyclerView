package com.kunminx.linkage.bean;

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


/**
 * 默认父(组)Item。
 *
 * Create by KunMinX at 19/4/27
 */
public class DefaultGroupedItem extends BaseGroupedItem<DefaultGroupedItem.ItemInfo> {

    public DefaultGroupedItem(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public DefaultGroupedItem(ItemInfo item) {
        super(item);
    }



    /**
     * 默认的子RV的Item信息。
     */
    public static class ItemInfo extends BaseGroupedItem.ItemInfo {

        private String content;

        /**
         * 默认的子RV的Item信息。
         *
         * @param title 标题。
         * @param group 组标题(内容)。
         * @param content
         */
        public ItemInfo(String title, String group, String content) {
            super(title, group);
            this.content = content;
        }

        /**
         *
         * @param title
         * @param group 组标题(内容)。
         */
        public ItemInfo(String title, String group) {
            super(title, group);
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
