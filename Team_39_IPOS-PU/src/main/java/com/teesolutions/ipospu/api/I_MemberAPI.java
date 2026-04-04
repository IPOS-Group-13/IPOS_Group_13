package com.teesolutions.ipospu.api;

import com.teesolutions.ipospu.dto.CommercialApplicationDto;

public interface I_MemberAPI {
    boolean submitCommercialApplication(CommercialApplicationDto application);
}
