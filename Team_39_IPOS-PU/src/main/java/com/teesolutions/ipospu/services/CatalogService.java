package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.api.I_InventoryAPI;
import com.teesolutions.ipospu.config.InventoryApiFactory;
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

    private final I_InventoryAPI inventoryApi = InventoryApiFactory.create();
    private final ProductRepository productRepository = new ProductRepository();
    private final CampaignRepository campaignRepository = new CampaignRepository();

    public List<InventoryItemDto> search(String keyword) {
        String k = keyword == null ? "" : keyword.trim();
        List<InventoryItemDto> raw;
        if (InventoryApiFactory.isCaInventoryEnabled()) {
            raw = filterCatalogueByKeyword(inventoryApi.getCatalogue(), k);
        } else {
            raw = productRepository.findActiveProducts(k);
        }
        return dedupeSameNamePreferNonLegacyId(raw);
    }

    public Map<String, Double> activeDiscounts() {
        return campaignRepository.getActiveDiscountByProduct();
    }

    public boolean hasPromotions() {
        return campaignRepository.hasActiveCampaigns();
    }

    public Optional<InventoryItemDto> findProduct(String productId) {
        if (InventoryApiFactory.isCaInventoryEnabled()) {
            if (productId == null) {
                return Optional.empty();
            }
            return inventoryApi.getCatalogue().stream()
                    .filter(p -> productId.equals(p.getProductId()))
                    .findFirst();
        }
        return productRepository.findById(productId);
    }

    private static List<InventoryItemDto> filterCatalogueByKeyword(List<InventoryItemDto> all, String keyword) {
        if (keyword.isEmpty()) {
            return new ArrayList<>(all);
        }
        String lower = keyword.toLowerCase();
        List<InventoryItemDto> out = new ArrayList<>();
        for (InventoryItemDto item : all) {
            String name = item.getName() != null ? item.getName().toLowerCase() : "";
            String desc = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
            if (name.contains(lower) || desc.contains(lower)) {
                out.add(item);
            }
        }
        return out;
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
