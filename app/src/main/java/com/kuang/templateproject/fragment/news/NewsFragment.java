/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.kuang.templateproject.fragment.news;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.kuang.templateproject.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.kuang.templateproject.adapter.base.delegate.SimpleDelegateAdapter;
import com.kuang.templateproject.adapter.base.delegate.SingleDelegateAdapter;
import com.kuang.templateproject.adapter.entity.AudioFile;
import com.kuang.templateproject.core.BaseFragment;
import com.kuang.templateproject.utils.Date.DateTimeUtil;
import com.kuang.templateproject.utils.XToastUtils;
import com.kuang.templateproject.utils.handle.AudioFileUtils;
import com.kuang.templateproject.utils.handle.PlayUtils;
import com.kuang.templateproject.utils.update.DemoAudioProvider;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.templateproject.R;
import com.kuang.templateproject.adapter.entity.NewInfo;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.banner.widget.banner.SimpleImageBanner;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

/**
 * 首页动态
 *
 * @author xuexiang
 * @since 2019-10-30 00:15
 */
@Page(anim = CoreAnim.none)
public class NewsFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private SimpleDelegateAdapter<NewInfo> mNewsAdapter;
    private SimpleDelegateAdapter<AudioFile> mAudiosAdapter;
    private boolean flag = false;
    private String filePath;
    private PlayUtils playUtils;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        mediaPlayer = new MediaPlayer();
        playUtils = new PlayUtils(mediaPlayer);
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        //轮播条
        SingleDelegateAdapter bannerAdapter = new SingleDelegateAdapter(R.layout.include_head_view_banner) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
                SimpleImageBanner banner = holder.findViewById(R.id.sib_simple_usage);
                banner.setSource(DemoAudioProvider.getBannerList())
                        .setOnItemClickListener((view, item, position1) -> XToastUtils.toast("headBanner position--->" + position1)).startScroll();
            }
        };

//        //九宫格菜单
//        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(3);
//        gridLayoutHelper.setPadding(0, 16, 0, 0);
//        gridLayoutHelper.setVGap(10);
//        gridLayoutHelper.setHGap(0);
//        SimpleDelegateAdapter<AdapterItem> commonAdapter = new SimpleDelegateAdapter<AdapterItem>(R.layout.adapter_common_grid_item, gridLayoutHelper, DemoDataProvider.getGridItems(getContext())) {
//            @Override
//            protected void bindData(@NonNull RecyclerViewHolder holder, int position, AdapterItem item) {
//                if (item != null) {
//                    RadiusImageView imageView = holder.findViewById(R.id.riv_item);
//                    imageView.setCircle(true);
//                    ImageLoader.get().loadImage(imageView, item.getIcon());
//                    holder.text(R.id.tv_title, item.getTitle().toString().substring(0, 1));
//                    holder.text(R.id.tv_sub_title, item.getTitle());
//
//                    holder.click(R.id.ll_container, v -> XToastUtils.toast("点击了：" + item.getTitle()));
//                }
//            }
//        };

        //资讯的标题
        SingleDelegateAdapter titleAdapter = new SingleDelegateAdapter(R.layout.adapter_title_item) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
                holder.text(R.id.tv_title, "文件");
                holder.text(R.id.tv_action, "更多");
                holder.click(R.id.tv_action, v -> XToastUtils.toast("没有更多"));
            }
        };





//        //资讯
//        mNewsAdapter = new BroccoliSimpleDelegateAdapter<NewInfo>(R.layout.adapter_news_card_view_list_item, new LinearLayoutHelper(), DemoDataProvider.getEmptyNewInfo()) {
//            @Override
//            protected void onBindData(RecyclerViewHolder holder, NewInfo model, int position) {
//                if (model != null) {
//                    holder.text(R.id.tv_user_name, model.getUserName());
//                    holder.text(R.id.tv_tag, model.getTag());
//                    holder.text(R.id.tv_title, model.getTitle());
//                    holder.text(R.id.tv_summary, model.getSummary());
//                    holder.text(R.id.tv_praise, model.getPraise() == 0 ? "点赞" : String.valueOf(model.getPraise()));
//                    holder.text(R.id.tv_comment, model.getComment() == 0 ? "评论" : String.valueOf(model.getComment()));
//                    holder.text(R.id.tv_read, "阅读量 " + model.getRead());
//                    holder.image(R.id.iv_image, model.getImageUrl());
//
//                    holder.click(R.id.card_view, v -> Utils.goWeb(getContext(), model.getDetailUrl()));
//                }
//            }
//            //资讯
//
//            @Override
//            protected void onBindBroccoli(RecyclerViewHolder holder, Broccoli broccoli) {
//                broccoli.addPlaceholders(
//                        holder.findView(R.id.tv_user_name),
//                        holder.findView(R.id.tv_tag),
//                        holder.findView(R.id.tv_title),
//                        holder.findView(R.id.tv_summary),
//                        holder.findView(R.id.tv_praise),
//                        holder.findView(R.id.tv_comment),
//                        holder.findView(R.id.tv_read),
//                        holder.findView(R.id.iv_image)
//                );
//            }
//        };

        mAudiosAdapter = new BroccoliSimpleDelegateAdapter<AudioFile>(R.layout.adapter_audio_card_view_list_item, new LinearLayoutHelper(), DemoAudioProvider.getEmptyNewAudios()) {
            @Override
            protected void onBindData(RecyclerViewHolder holder, AudioFile model, int position) {
                if(model != null){
                    holder.text(R.id.audio_name, model.getFilename());
                    holder.text(R.id.audio_tag, model.getTag());
                    holder.text(R.id.audio_createTime, ("时间:"+DateTimeUtil.dateToStr(model.getDate())));
                    holder.text(R.id.audio_dateString, DateTimeUtil.formatFriendly(model.getDate()));
                    holder.click(R.id.audio_delete, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("delete","click");
                            showSimpleConfirmDialog();
                            filePath = model.getFilePath();
                        }
                    });
                    holder.click(R.id.audio_back, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String currentFile = model.getFilePath().substring(0,model.getFilePath().lastIndexOf("/"));
                            Log.i("audioBackPlay",model.getFilePath());
//                            if(!isPlaying)
                            Log.i("backback",currentFile+"/back/"+model.getFile().getName());
                                playUtils.startVoice(currentFile+"/back/"+model.getFile().getName());
                        }
                    });
                    holder.click(R.id.audio_forward, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if(!isPlaying)
                            playUtils.startVoice(model.getFilePath());
                        }
                    });
                }
            }

            @Override
            protected void onBindBroccoli(RecyclerViewHolder holder, Broccoli broccoli) {
                holder.findView(R.id.audio_dateString);
                holder.findView(R.id.audio_createTime);
                holder.findView(R.id.audio_tag);
                holder.findView(R.id.audio_name);
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(bannerAdapter);
//        delegateAdapter.addAdapter(commonAdapter);
        delegateAdapter.addAdapter(titleAdapter);
//        delegateAdapter.addAdapter(mNewsAdapter);
        delegateAdapter.addAdapter(mAudiosAdapter);

        recyclerView.setAdapter(delegateAdapter);

    }

    @Override
    protected void initListeners() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
            }
        });
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
//                mNewsAdapter.refresh(DemoDataProvider.getDemoNewInfos());
                mAudiosAdapter.refresh(DemoAudioProvider.getDemoNewAudios());
                for (AudioFile demoNewAudio : DemoAudioProvider.getDemoNewAudios()) {
                    Log.i("List",demoNewAudio.toString());
                }
                Log.i("method","refresh被执行");
                refreshLayout.finishRefresh();
            }, 1000);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
//                mNewsAdapter.loadMore(DemoDataProvider.getDemoNewInfos());
                mAudiosAdapter.loadMore(DemoAudioProvider.getDemoNewAudios());
                refreshLayout.finishLoadMore();
            }, 1000);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }
    private void showSimpleConfirmDialog() {
        MaterialDialog dialog= new MaterialDialog.Builder(getContext())
                .content("是否删除")
                .positiveText(R.string.lab_yes)
                .negativeText(R.string.lab_no)
                .onPositive((dialog1, which) -> mHandler.sendEmptyMessage(1))
                .onNegative((dialog1, which) -> changeFlag(false))
                .show();
    }
    private void changeFlag(boolean flag){
        this.flag = flag;
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    new Thread(deleteRunnable).start();
                    break;
                case 2:

                    break;
                case 3:
                    break;

            }
            return false;
        }
    });
    private Runnable deleteRunnable = new Runnable() {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            Looper.prepare();
            Log.i("delete线程", "start");
            if (AudioFileUtils.deleteFile(filePath)) {
                new MaterialDialog.Builder(getContext())
                        .iconRes(R.drawable.icon_tip)
                        .title("提示")
                        .content("删除成功")
                        .positiveText("好的")
                        .show();
            } else {
                new MaterialDialog.Builder(getContext())
                        .iconRes(R.drawable.icon_tip)
                        .title("提示")
                        .content("删除失败")
                        .positiveText("好的")
                        .show();
            }
            refreshLayout.autoRefresh();
            flag = false;
            Looper.loop();

        }
    };

}
