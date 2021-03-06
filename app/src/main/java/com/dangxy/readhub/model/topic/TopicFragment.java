package com.dangxy.readhub.model.topic;


import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.dangxy.readhub.R;
import com.dangxy.readhub.ReadhubApplication;
import com.dangxy.readhub.base.BaseLazyFragment;
import com.dangxy.readhub.entity.TopicEntity;
import com.dangxy.readhub.model.DetailActivity;
import com.dangxy.readhub.room.Wait;
import com.dangxy.readhub.room.WaitDataBase;

import butterknife.BindView;

/**
 * @author dangxy99
 * @description 热门话题
 * @date 2017/12/30
 */
public class TopicFragment extends BaseLazyFragment implements TopicContract.ITopicView, TopicListAdapter.DetailClickListener {


    @BindView(R.id.rv_topic)
    RecyclerView rvTopic;
    @BindView(R.id.srl_topic)
    SwipeRefreshLayout srlTopic;
    private TopicPresenter topicPresenter;
    private TopicListAdapter topicListAdapter;
    private Wait wait;
    private boolean isAdd;

    public TopicFragment() {
    }


    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_topic;
    }

    @Override
    protected void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ReadhubApplication.getInstance());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvTopic.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void loadData() {
        topicPresenter = new TopicPresenter(this);
        topicPresenter.getData();
        topicPresenter.setRefresh(srlTopic, rvTopic);

    }


    @Override
    public void setRefresh(TopicEntity topicEntity) {
        topicListAdapter.refresh(topicEntity.getData());
    }

    @Override
    public void getTopicEntity(TopicEntity topicEntity, boolean isFirst) {
        if (isFirst) {
            topicListAdapter = new TopicListAdapter(topicEntity.getData());
            rvTopic.setAdapter(topicListAdapter);
        } else {
            topicListAdapter.addAll(topicEntity.getData());
        }

        topicListAdapter.setOnDetailClickListener(this);

    }

    @Override
    public void onDetailClickListener(String title, String summary, String url) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("summary", summary);
        startActivity(intent);
    }

    @Override
    public void onWaitClickListener(final ImageView imageView, String id, String title, String summary, String url) {
        wait = new Wait(id, title, summary, url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                WaitDataBase.getDatabase().waitDao().addWait(wait);
            }
        }).start();
        Snackbar.make(imageView, "已添加到稍后再看", Snackbar.LENGTH_SHORT).setAction("知道了", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();

    }
}
