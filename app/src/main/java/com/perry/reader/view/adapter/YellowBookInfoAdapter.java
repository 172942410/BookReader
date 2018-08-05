package com.perry.reader.view.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.perry.reader.R;
import com.perry.reader.model.YellowBookBean;

import java.util.List;

/**
 * Created by lipengjun on 2018/8/5.
 */

public class YellowBookInfoAdapter extends BaseQuickAdapter<YellowBookBean.Content, BaseViewHolder> {
    public YellowBookInfoAdapter(@Nullable List<YellowBookBean.Content> data) {
        super(R.layout.item_yellow_bookinfo, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, YellowBookBean.Content item) {
//        int follower = item.getLatelyFollower() / 10000;
//        String wordCount = item.getLatelyFollower() / 10000 > 0 ? BaseUtils.format1Digits((double)item.getLatelyFollower() / 10000 ) + "万" : item.getLatelyFollower() + "";

        helper.setText(R.id.book_brief_tv_title, item.name)
                .setText(R.id.book_brief_tv_author, "开始的索引:"+item.startpage+"")
                .setText(R.id.book_brief_tv_brief, "第 ( "+(item.contetindex+1)+" ) 篇")
                .setText(R.id.ctv_arrow_count, "共 "+item.pagecount + "")
                .setText(R.id.ctv_retention, item.progress + "%")
        ;

//        Glide.with(mContext).load(Constant.ZHUISHU_IMAGE_URL + item.getCover())
//                .apply(new RequestOptions().placeholder(R.mipmap.ic_book_loading))/*.transform(new GlideRoundTransform(mContext))*/
//                .into((ImageView) helper.getView(R.id.book_brief_iv_portrait));

    }
}
