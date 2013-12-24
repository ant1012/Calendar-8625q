/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.bupt.calendar;

import com.android.ex.chips.BaseRecipientAdapter;

import android.accounts.Account;
import android.content.Context;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 选择参与人时的自动补全列表
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，有修改
 * 
 * */

public class RecipientAdapter extends BaseRecipientAdapter {
//    public RecipientAdapter(Context context) {
//        super(context);
//    }

    /** zzz */
    // zzz 搜索电话号码而不是邮箱地址
    public RecipientAdapter(Context context) {
        super(QUERY_TYPE_PHONE, context);
    }

    /**
     * Set the account when known. Causes the search to prioritize contacts from
     * that account.
     */
    public void setAccount(Account account) {
        if (account != null) {
            // TODO: figure out how to infer the contacts account
            // type from the email account
            super.setAccount(new android.accounts.Account(account.name, "unknown"));
        }
    }
}
