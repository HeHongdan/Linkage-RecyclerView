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


import java.io.Serializable;

/**
 * 父(组)Item基类。
 *
 * items which support grouped
 * <p>
 * Create by KunMinX at 19/4/29
 */
public abstract class BaseGroupedItem<T extends BaseGroupedItem.ItemInfo> implements Serializable {

    /** 是否(子)RV顶部标题(一般填父内容)。 */
    public boolean isHeader;
    /** (子)RV顶部标题(一般填父内容)文本。 */
    public String header;
    public T info;

    public BaseGroupedItem(boolean isHeader, String header) {
        this.isHeader = isHeader;
        this.header = header;
        this.info = null;
    }

    public BaseGroupedItem(T info) {
        this.isHeader = false;
        this.header = null;
        this.info = info;
    }

    /**
     * 父(组)Item数据的基类型。
     */
    public static class ItemInfo implements Serializable {

        /** 组的标题(一般为父的内容)。 */
        private String group;
        /** 组的标题(一般为父的内容)。 */
        private String title;

        public ItemInfo(String title, String group) {
            this.title = title;
            this.group = group;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }
    }
}
