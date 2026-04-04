package com.teesolutions.ipospu.integrations;

import com.teesolutions.ipospu.api.I_MemberAPI;
import com.teesolutions.ipospu.dto.CommercialApplicationDto;
import com.teesolutions.ipospu.repositories.CommercialApplicationRepository;

public class MockMemberApiClient implements I_MemberAPI {
    private final CommercialApplicationRepository repository = new CommercialApplicationRepository();

    @Override
    public boolean submitCommercialApplication(CommercialApplicationDto application) {
        return repository.save(application);
    }
}
