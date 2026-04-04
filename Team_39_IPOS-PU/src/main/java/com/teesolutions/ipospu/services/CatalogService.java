package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.dto.InventoryItemDto;
import com.teesolutions.ipospu.repositories.CampaignRepository;
import com.teesolutions.ipospu.repositories.ProductRepository;
import com.teesolutions.ipospu.utils.LegacySampleProductIds;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CatalogService {

    private final ProductRepository productRepository = new ProductRepository();
    private final CampaignRepository campaignRepository = new CampaignRepository();

    public List<InventoryItemDto> search(String keyword) {
        List<InventoryItemDto> raw = productRepository.findActiveProducts(keyword == null ? "" : keyword.trim());
        return dedupeSameNamePreferNonLegacyId(raw);
    }

    public Map<String, Double> activeDiscounts() {
        return campaignRepository.getActiveDiscountByProduct();
    }

    public boolean hasPromotions() {
        return campaignRepository.hasActiveCampaigns();
    }

    public Optional<InventoryItemDto> findProduct(String productId) {
        return productRepository.findById(productId);
    }

    private static List<InventoryItemDto> dedupeSameNamePreferNonLegacyId(List<InventoryItemDto> items) {
        Map<String, List<InventoryItemDto>> byNormName = new LinkedHashMap<>();
        for (InventoryItemDto item : items) {
            String key = LegacySampleProductIds.normalizeNameKey(item.getName());
            byNormName.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
        }
        List<InventoryItemDto> out = new ArrayList<>(byNormName.size());
        for (List<InventoryItemDto> group : byNormName.values()) {
            out.add(pickCatalogueRow(group));
        }
        out.sort(Comparator.comparing(i -> i.getName() == null ? "" : i.getName(), String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    private static InventoryItemDto pickCatalogueRow(List<InventoryItemDto> group) {
        if (group.size() == 1) {
            return group.get(0);
        }
        List<InventoryItemDto> nonLegacy = new ArrayList<>();
        for (InventoryItemDto p : group) {
            if (!LegacySampleProductIds.isLegacyEightDigitSampleId(p.getProductId())) {
                nonLegacy.add(p);
            }
        }
        if (!nonLegacy.isEmpty()) {
            nonLegacy.sort(Comparator.comparing(InventoryItemDto::getProductId));
            return nonLegacy.get(0);
        }
        group.sort(Comparator.comparing(InventoryItemDto::getProductId));
        return group.get(0);
    }
}
