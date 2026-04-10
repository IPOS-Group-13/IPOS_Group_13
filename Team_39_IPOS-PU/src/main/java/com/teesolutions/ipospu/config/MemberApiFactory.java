package com.teesolutions.ipospu.config;

import com.teesolutions.ipospu.api.I_MemberAPI;
import com.teesolutions.ipospu.integrations.SaJdbcMemberApiClient;


public final class MemberApiFactory {

    private MemberApiFactory() {
    }

    public static I_MemberAPI create() {
        return new SaJdbcMemberApiClient();
    }
}
