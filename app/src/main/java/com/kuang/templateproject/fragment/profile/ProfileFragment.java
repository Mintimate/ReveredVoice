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

package com.kuang.templateproject.fragment.profile;

import com.kuang.templateproject.core.BaseFragment;
import com.kuang.templateproject.fragment.other.AboutFragment;
import com.kuang.templateproject.fragment.other.SettingsFragment;
import com.kuang.templateproject.utils.XToastUtils;
import com.xuexiang.templateproject.R;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import butterknife.BindView;

import static com.kuang.templateproject.utils.XToastUtils.toast;

/**
 * @author xuexiang
 * @since 2019-10-30 00:18
 */
@Page(anim = CoreAnim.none)
public class ProfileFragment extends BaseFragment implements SuperTextView.OnSuperTextViewClickListener {
    @BindView(R.id.riv_head_pic)
    RadiusImageView rivHeadPic;
    @BindView(R.id.menu_settings)
    SuperTextView menuSettings;
    @BindView(R.id.menu_about)
    SuperTextView menuAbout;
    @BindView(R.id.menu_account)
    SuperTextView menu_Account;

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
        return R.layout.fragment_profile;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {
        menuSettings.setOnSuperTextViewClickListener(this);
        menuAbout.setOnSuperTextViewClickListener(this);
        menu_Account.setOnSuperTextViewClickListener(this);

    }

    @SingleClick
    @Override
    public void onClick(SuperTextView view) {
        switch(view.getId()) {
            case R.id.menu_settings:
                openNewPage(SettingsFragment.class);
                break;
            case R.id.menu_about:
                openNewPage(AboutFragment.class);
                break;
            case R.id.menu_account:
                XToastUtils.toast("点击了:" + menu_Account.getLeftString());
                break;
            default:
                XToastUtils.toast("点击了:");
                break;
        }
    }
}
