package com.perry.reader.view.activity.impl;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.perry.reader.R;
import com.perry.reader.utils.BaseUtils;
import com.perry.reader.utils.ToastUtils;
import com.perry.reader.view.activity.IFeedBack;
import com.perry.reader.view.base.BaseActivity;
import com.perry.reader.viewmodel.BaseViewModel;
import com.perry.reader.viewmodel.activity.VMFeedBackInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedBackActivity extends BaseActivity implements IFeedBack {

    @BindView(R.id.et_qq)
    EditText mEtQq;
    @BindView(R.id.et_feedback)
    EditText mEtFeedback;
    private VMFeedBackInfo mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new VMFeedBackInfo(this, this);
        setBinddingView(R.layout.activity_feed_back, NO_BINDDING, mModel);
        initThemeToolBar("意见反馈");
    }

    @OnClick(R.id.btn_commit)
    public void onViewClicked() {
        if (TextUtils.isEmpty(mEtQq.getText())) {
            ToastUtils.show("请输入QQ号码");
            return;
        }
        if (TextUtils.isEmpty(mEtFeedback.getText())) {
            ToastUtils.show("请描述一下问题或者好的建议哟");
            return;
        }
        mModel.commitFeedBack(mEtQq.getText().toString(), mEtFeedback.getText().toString());
        BaseUtils.hideInput(mEtFeedback);
    }

    @Override
    public void feedBackSuccess() {
        mEtQq.setText("");
        mEtFeedback.setText("");
    }
}
