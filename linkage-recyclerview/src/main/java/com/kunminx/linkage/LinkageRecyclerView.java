package com.kunminx.linkage;
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
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.kunminx.linkage.adapter.LinkagePrimaryAdapter;
import com.kunminx.linkage.adapter.LinkageSecondaryAdapter;
import com.kunminx.linkage.adapter.viewholder.LinkagePrimaryViewHolder;
import com.kunminx.linkage.bean.BaseGroupedItem;
import com.kunminx.linkage.bean.DefaultGroupedItem;
import com.kunminx.linkage.contract.ILinkagePrimaryAdapterConfig;
import com.kunminx.linkage.contract.ILinkageSecondaryAdapterConfig;
import com.kunminx.linkage.defaults.DefaultLinkagePrimaryAdapterConfig;
import com.kunminx.linkage.defaults.DefaultLinkageSecondaryAdapterConfig;
import com.kunminx.linkage.manager.RecyclerViewScrollHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by KunMinX at 19/4/27
 */
public class LinkageRecyclerView<T extends BaseGroupedItem.ItemInfo> extends ConstraintLayout {

    /** (子RV)列数。 */
    private static final int DEFAULT_SPAN_COUNT = 1;
    /** (滑动)定位到指定(置顶)项。 */
    private static final int SCROLL_OFFSET = 0;
    /** 标记：父RV(分割线)。 */
    public static final int FOR_PRIMARY = 1;
    /** 标记：子RV(分割线)。 */
    public static final int FOR_SECONDARY = 2;


    /** 整个视图控件。 */
    private View mView;
    /** 父RV。 */
    private RecyclerView mRvPrimary;
    /** 子RV。 */
    private RecyclerView mRvSecondary;
    /** 父适配器。 */
    private LinkagePrimaryAdapter mPrimaryAdapter;
    /** 子适配器。 */
    private LinkageSecondaryAdapter mSecondaryAdapter;
    /** (子)RV顶部(组)标题(一般为父的内容)文本。 */
    private TextView mTvHeader;
    /** (子)头部视图的容器。 */
    private FrameLayout mHeaderContainer;
    /** (子)头部视图的布局(放到容器中)。 */
    private View mHeaderLayout;

    /** (子)头部视图容器的高度。 */
    private int mTitleHeight;
    /** 父RV布局管理器。 */
    private LinearLayoutManager mPrimaryLayoutManager;
    /** 子RV布局管理器。 */
    private LinearLayoutManager mSecondaryLayoutManager;
    /** 父数据在子RV中位置的集合。 */
    private final List<Integer> mHeaderPositions = new ArrayList<>();
    /** 父数据集合。 */
    private List<String> mInitGroupNames;
    /** 子(包含父)RV的数据集合。 */
    private List<BaseGroupedItem<T>> mInitItems;
    /** (子)第一个控件项位置。 */
    private int mFirstVisiblePosition;
    /** 最后的(子)RV顶部(组)标题(一般为父的内容)文本。 */
    private String mLastGroupName;
    /** 是否已响应(父)点击事件，区别自动还是点击选取。 */
    private boolean mPrimaryClicked = false;
    /** 是否平滑滑动。 */
    private boolean mScrollSmoothly = true;
    /** 上下文。 */
    private Context mContext;



    public LinkageRecyclerView(Context context) {
        super(context);
    }

    public LinkageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public LinkageRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        this.mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.layout_linkage_view, this);
        mRvPrimary = (RecyclerView) mView.findViewById(R.id.rv_primary);
        mRvSecondary = (RecyclerView) mView.findViewById(R.id.rv_secondary);
        mHeaderContainer = (FrameLayout) mView.findViewById(R.id.header_container);
    }

    /**
     * 设置子RV的布局管理器(Linear 或 Grid)。
     */
    private void setLevel2LayoutManager() {
        if (mSecondaryAdapter.isGridMode()) {
            mSecondaryLayoutManager = new GridLayoutManager(mContext,
                    mSecondaryAdapter.getConfig().getSpanCountOfGridMode());
            ((GridLayoutManager) mSecondaryLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {//https://www.jianshu.com/p/b61c28ab2f24
                @Override
                public int getSpanSize(int position) {
                    //for header and footer
                    if (((BaseGroupedItem<T>) mSecondaryAdapter.getItems().get(position)).isHeader || position == mInitItems.size() - 1) {
                        return mSecondaryAdapter.getConfig().getSpanCountOfGridMode();
                    }
                    return DEFAULT_SPAN_COUNT;
                }
            });
        } else {
            mSecondaryLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        }
        mRvSecondary.setLayoutManager(mSecondaryLayoutManager);
    }

    /**
     * 设置父、子RV。
     *
     * @param primaryAdapterConfig 父适配器的配置。
     * @param secondaryAdapterConfig 子适配器的配置。
     */
    private void initRecyclerView(ILinkagePrimaryAdapterConfig primaryAdapterConfig,
                                  ILinkageSecondaryAdapterConfig secondaryAdapterConfig) {

        mPrimaryAdapter = new LinkagePrimaryAdapter(mInitGroupNames, primaryAdapterConfig,
                new LinkagePrimaryAdapter.OnLinkageListener() {
                    @Override
                    public void onLinkageClick(LinkagePrimaryViewHolder holder, String title) {
                        if (isScrollSmoothly()) {
                            //平滑滑动
                            RecyclerViewScrollHelper.smoothScrollToPosition(mRvSecondary,
                                    LinearSmoothScroller.SNAP_TO_START,
                                    mHeaderPositions.get(holder.getBindingAdapterPosition()));
                        } else {
                            //定位到指定项//https://www.jianshu.com/p/3acc395ae933
                            mSecondaryLayoutManager.scrollToPositionWithOffset(
                                    mHeaderPositions.get(holder.getBindingAdapterPosition()), SCROLL_OFFSET);
                        }
                        mPrimaryAdapter.setSelectedPosition(holder.getBindingAdapterPosition());
                        mPrimaryClicked = true;
                    }
                });

        mPrimaryLayoutManager = new LinearLayoutManager(mContext);
        mRvPrimary.setLayoutManager(mPrimaryLayoutManager);
        mRvPrimary.setAdapter(mPrimaryAdapter);

        mSecondaryAdapter = new LinkageSecondaryAdapter(mInitItems, secondaryAdapterConfig);
        setLevel2LayoutManager();
        mRvSecondary.setAdapter(mSecondaryAdapter);
    }

    /**
     * 初始化联动子(次要)。
     */
    private void initLinkageSecondary() {

        // Note: headerLayout is shared by both SecondaryAdapter's header and HeaderView
        // headerLayout由SecondaryAdapter的头部和HeaderView共享

        if (mTvHeader == null && mSecondaryAdapter.getConfig() != null) {
            ILinkageSecondaryAdapterConfig config = mSecondaryAdapter.getConfig();
            int layout = config.getHeaderLayoutId();
            mHeaderLayout = LayoutInflater.from(mContext).inflate(layout, mHeaderContainer, false);
            mHeaderContainer.addView(mHeaderLayout);
            mTvHeader = mHeaderLayout.findViewById(config.getHeaderTextViewId());
        }

        if (mInitItems.get(mFirstVisiblePosition).isHeader) {
            mTvHeader.setText(mInitItems.get(mFirstVisiblePosition).header);
        }

        mRvSecondary.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mTitleHeight = mHeaderContainer.getMeasuredHeight();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //第一个可见的位置
                int firstPosition = mSecondaryLayoutManager.findFirstVisibleItemPosition();
                //第一个(已)可见的位置//https://blog.csdn.net/agf81596/article/details/101471082
                int firstCompletePosition = mSecondaryLayoutManager.findFirstCompletelyVisibleItemPosition();
                List<BaseGroupedItem<T>> items = mSecondaryAdapter.getItems();

                // Here is the logic of the sticky:
                // 下面是粘性的逻辑
                if (firstCompletePosition > 0 && (firstCompletePosition) < items.size()
                        && items.get(firstCompletePosition).isHeader) {

                    //第一个(已)可见位置的Item视图
                    View view = mSecondaryLayoutManager.findViewByPosition(firstCompletePosition);
                    if (view != null && view.getTop() <= mTitleHeight) {
                        //Y轴位移
                        mHeaderContainer.setY(view.getTop() - mTitleHeight);
                    }
                } else {
                    mHeaderContainer.setY(0);
                }

                // Here is the logic of group title changes and linkage:
                // (组)标题更改和链接的逻辑

                // (组)标题是否更改
                boolean groupNameChanged = false;

                //判断第一个fei子RV的Item(是标题)
                if (mFirstVisiblePosition != firstPosition && firstPosition >= 0) {
                    if (mFirstVisiblePosition < firstPosition) {
                        mHeaderContainer.setY(0);//纠正位置(拉回屏幕内部)
                    }

                    mFirstVisiblePosition = firstPosition;

                    String currentGroupName = items.get(mFirstVisiblePosition).isHeader
                            ? items.get(mFirstVisiblePosition).header
                            : items.get(mFirstVisiblePosition).info.getGroup();

                    if (TextUtils.isEmpty(mLastGroupName) || !mLastGroupName.equals(currentGroupName)) {
                        mLastGroupName = currentGroupName;
                        groupNameChanged = true;
                        mTvHeader.setText(mLastGroupName);
                    }
                }

                // the following logic can not be perfect, because tvHeader's title may not
                // always equals to the title of selected primaryItem, while there
                // are several groups which has little items to stick group item to tvHeader.
                //
                // To avoid to this extreme situation, my idea is to add a footer on the bottom,
                // to help wholly execute this logic.
                //
                //下面的逻辑不可能完美，因为tvHeader的标题可能并不总是等于所选primaryItem的标题，而有几个组几乎没有项目可以将group item粘贴到tvHeader。
                //为了避免这种极端情况，我的想法是在底部添加一个页脚，以帮助完全执行这个逻辑。

                // Note: 2019.5.22 KunMinX

                if (groupNameChanged) {
                    List<String> groupNames = mPrimaryAdapter.getStrings();
                    for (int i = 0; i < groupNames.size(); i++) {
                        if (groupNames.get(i).equals(mLastGroupName)) {
                            if (mPrimaryClicked) {
                                if (mPrimaryAdapter.getSelectedPosition() == i) {
                                    mPrimaryClicked = false;
                                }
                            } else {
                                mPrimaryAdapter.setSelectedPosition(i);
                                RecyclerViewScrollHelper.smoothScrollToPosition(mRvPrimary,
                                        LinearSmoothScroller.SNAP_TO_END, i);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * init LinkageRV by items and configs
     * 按项目和配置初始化LinkageRV。
     *
     * @param linkageItems Json数据(含父、子)。
     * @param primaryAdapterConfig 父适配器配置。
     * @param secondaryAdapterConfig 子适配器配置。
     */
    public void init(List<BaseGroupedItem<T>> linkageItems,
                     ILinkagePrimaryAdapterConfig primaryAdapterConfig,
                     ILinkageSecondaryAdapterConfig secondaryAdapterConfig) {

        initRecyclerView(primaryAdapterConfig, secondaryAdapterConfig);

        this.mInitItems = linkageItems;

        String lastGroupName = null;
        List<String> groupNames = new ArrayList<>();
        // 筛选出父RV的Item的数据
        if (mInitItems != null && mInitItems.size() > 0) {
            for (BaseGroupedItem<T> item : mInitItems) {
                if (item.isHeader) {
                    groupNames.add(item.header);
                    lastGroupName = item.header;
                }
            }
        }

        // 收集父数据(标题)在子RV中的位置
        if (mInitItems != null) {
            for (int i = 0; i < mInitItems.size(); i++) {
                if (mInitItems.get(i).isHeader) {
                    mHeaderPositions.add(i);
                }
            }
        }

        DefaultGroupedItem.ItemInfo info = new DefaultGroupedItem.ItemInfo(null, lastGroupName);
        // 页脚(占空间用的)
        BaseGroupedItem<T> footerItem = (BaseGroupedItem<T>) new DefaultGroupedItem(info);
        mInitItems.add(footerItem);

        this.mInitGroupNames = groupNames;
        mPrimaryAdapter.initData(mInitGroupNames);
        mSecondaryAdapter.initData(mInitItems);
        initLinkageSecondary();
    }

    /**
     * simplify init by only items and default configs。
     * 仅通过项目和默认配置简化初始化。
     *
     * @param linkageItems Json数据(含父、子)。
     */
    public void init(List<BaseGroupedItem<T>> linkageItems) {
        init(linkageItems, new DefaultLinkagePrimaryAdapterConfig(), new DefaultLinkageSecondaryAdapterConfig());
    }

    /**
     * bind listeners for primary or secondary adapter
     * 绑定主适配器或辅助适配器的(点击、绑定)侦听器。
     *
     * @param primaryItemClickListener 父Item点击的事件。
     * @param primaryItemBindListener 父Item绑定的事件。
     * @param secondaryItemBindListener 子Item绑定的事件。
     * @param headerBindListener 头部绑定的事件。
     * @param footerBindListener (页)脚部绑定的事件。
     */
    public void setDefaultOnItemBindListener(
            DefaultLinkagePrimaryAdapterConfig.OnPrimaryItemClickListner primaryItemClickListener,
            DefaultLinkagePrimaryAdapterConfig.OnPrimaryItemBindListener primaryItemBindListener,
            DefaultLinkageSecondaryAdapterConfig.OnSecondaryItemBindListener secondaryItemBindListener,
            DefaultLinkageSecondaryAdapterConfig.OnSecondaryHeaderBindListener headerBindListener,
            DefaultLinkageSecondaryAdapterConfig.OnSecondaryFooterBindListener footerBindListener) {

        //设置父(点击、绑定)侦听器。
        if (mPrimaryAdapter.getConfig() != null) {
            ((DefaultLinkagePrimaryAdapterConfig) mPrimaryAdapter.getConfig())
                    .setListener(primaryItemBindListener, primaryItemClickListener);
        }
        //设置子(点击、绑定)侦听器。
        if (mSecondaryAdapter.getConfig() != null) {
            ((DefaultLinkageSecondaryAdapterConfig) mSecondaryAdapter.getConfig())
                    .setItemBindListener(secondaryItemBindListener, headerBindListener, footerBindListener);
        }
    }

    /**
     * custom linkageRV width in some scene like dialog
     * 在某些场景（如对话框）中自定义的RV高度。
     *
     * @param dp
     */
    public void setLayoutHeight(float dp) {
        ViewGroup.LayoutParams lp = mView.getLayoutParams();
        lp.height = dpToPx(getContext(), dp);
        mView.setLayoutParams(lp);
    }

    /**
     * custom primary list width.
     * <p>
     * The reason for this design is that：The width of the first-level list must be an accurate value,
     * otherwise the onBindViewHolder may be called multiple times due to the RecyclerView's own bug.
     * <p>
     * Note 2021.1.20: this bug has been deal with in the newest version of RecyclerView
     *
     * 自定义父列表宽度。
     * <p>
     *     这种设计的原因是：一级列表的宽度必须是一个准确的值，否则由于RecyclerView自身的错误，onBindViewHolder可能会被多次调用。
     * <p>
     * 注意2021.1.20：此错误已在最新版本的RecyclerView中解决。
     *
     * @param dp 宽度dp。
     */
    @Deprecated
    public void setPrimaryWidth(float dp) {
        //父RV宽度。
        ViewGroup.LayoutParams lpLeft = mRvPrimary.getLayoutParams();
        lpLeft.width = dpToPx(getContext(), dp);
        mRvPrimary.setLayoutParams(lpLeft);

        //子RV宽度。
        ViewGroup.LayoutParams lpRight = mRvSecondary.getLayoutParams();
        lpRight.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mRvSecondary.setLayoutParams(lpRight);
    }

    /**
     * 是否为网格模式。
     *
     * @return 是否网格模式。
     */
    public boolean isGridMode() {
        return mSecondaryAdapter.isGridMode();
    }

    /**
     * custom if secondary list is hope to be grid mode
     * 如果希望将子列表设置为网格模式，则自定义。
     *
     * @param isGridMode 是否网格模式。
     */
    public void setGridMode(boolean isGridMode) {
        mSecondaryAdapter.setGridMode(isGridMode);
        setLevel2LayoutManager();
        mRvSecondary.requestLayout();
    }

    /**
     * (父)是否平滑滑动。
     *
     * @return 是否平滑滑动。
     */
    public boolean isScrollSmoothly() {
        return mScrollSmoothly;
    }

    /**
     * custom if is hope to scroll smoothly while click primary item to linkage secondary list
     * 如果希望在单击父要项目以链接次要列表时平滑滚动，请自定义。
     *
     * @return 是否平滑滑动。
     */
    public void setScrollSmoothly(boolean scrollSmoothly) {
        this.mScrollSmoothly = scrollSmoothly;
    }

    /**
     * 获取父适配器。
     *
     * @return 父适配器。
     */
    public LinkagePrimaryAdapter getPrimaryAdapter() {
        return mPrimaryAdapter;
    }

    /**
     * 获取子适配器。
     *
     * @return 子适配器。
     */
    public LinkageSecondaryAdapter getSecondaryAdapter() {
        return mSecondaryAdapter;
    }

    /**
     * 父数据在子RV中位置的集合。
     *
     * @return 父数据位置的集合。
     */
    public List<Integer> getHeaderPositions() {
        return mHeaderPositions;
    }

    /**
     * set percent of primary list and secondary list width
     * 设置父列表和子列表宽度的百分比。
     *
     * @param percent 百分比。
     */
    public void setPercent(float percent) {
        Guideline guideline = (Guideline) mView.findViewById(R.id.guideline);
        guideline.setGuidelinePercent(percent);
    }

    /**
     * 设置父列表背景色。
     *
     * @param color (父)背景色。
     */
    public void setRvPrimaryBackground(int color) {
        mRvPrimary.setBackgroundColor(color);
    }

    /**
     * 设置子列表背景色。
     *
     * @param color (子)背景色。
     */
    public void setRvSecondaryBackground(int color) {
        mRvSecondary.setBackgroundColor(color);
    }

    public View getHeaderLayout() {
        return mHeaderLayout;
    }

    /**
     * addItemDecoration for Primary or Secondary RecyclerView
     * 添加父或子RecyclerView的分割线。
     *
     * @param forPrimaryOrSecondary 标记：子或父RV(分割线)。
     * @param decoration 分割线。
     */
    public void addItemDecoration(int forPrimaryOrSecondary, RecyclerView.ItemDecoration decoration) {
        switch (forPrimaryOrSecondary) {
            case FOR_PRIMARY:
                mRvPrimary.removeItemDecoration(decoration);
                mRvPrimary.addItemDecoration(decoration);
            case FOR_SECONDARY:
                mRvSecondary.removeItemDecoration(decoration);
                mRvSecondary.addItemDecoration(decoration);
        }
    }



    private int dpToPx(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5f);
    }

}
