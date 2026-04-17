package com.berrybyte.RPT;

import com.berrybyte.RPT.model.MerchantOption;
import com.berrybyte.RPT.repository.ReportRepository;
import org.junit.jupiter.api.Assumptions;

import java.time.LocalDate;
import java.util.List;

public final class ReportIntegrationTestSupport {

    private static final LocalDate START_DATE = LocalDate.of(2000, 1, 1);

    private ReportIntegrationTestSupport() {
    }

    public static MerchantOption requireActiveMerchant(ReportRepository repository) {
        List<MerchantOption> merchants = repository.findMerchantOptions();

        Assumptions.assumeFalse(
                merchants.isEmpty(),
                "Integration test requires at least one active merchant in MerchantAccounts."
        );

        return merchants.get(0);
    }

    public static LocalDate startDate() {
        return START_DATE;
    }

    public static LocalDate endDate() {
        return LocalDate.now().plusDays(1);
    }
}
