package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.repositories.CampaignRepository;
import com.teesolutions.ipospu.utils.LegacySampleProductIds;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CampaignService {
    private record PromotionGroupKey(int campaignId, String normName) {
    }

    private final CampaignRepository campaignRepository = new CampaignRepository();

    public int createCampaign(String name, LocalDateTime start, LocalDateTime end, String itemSpec) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Campaign name is required");
        }
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Campaign start/end time is invalid");
        }
        List<ItemDiscount> items = parseItemSpec(itemSpec);
        for (ItemDiscount item : items) {
            if (campaignRepository.isProductInOverlappingCampaign(item.productId, start, end)) {
                throw new IllegalArgumentException("Product " + item.productId + " is already in an overlapping active campaign");
            }
        }
        int campaignId = campaignRepository.createCampaign(name.trim(), start, end);
        campaignRepository.replaceCampaignItems(campaignId, toDiscountMap(items));
        return campaignId;
    }

    public void cancelCampaign(int campaignId) {
        campaignRepository.cancelCampaign(campaignId);
    }

    public void updateCampaign(int campaignId, String name, LocalDateTime start, LocalDateTime end, String itemSpec) {
        if (campaignId <= 0) {
            throw new IllegalArgumentException("Select a valid campaign first");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Campaign name is required");
        }
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Campaign start/end time is invalid");
        }
        List<ItemDiscount> items = parseItemSpec(itemSpec);
        for (ItemDiscount item : items) {
            if (campaignRepository.isProductInOverlappingCampaign(item.productId, start, end, campaignId)) {
                throw new IllegalArgumentException("Product " + item.productId + " still conflicts with another overlapping campaign");
            }
        }
        campaignRepository.updateCampaign(campaignId, name.trim(), start, end);
        campaignRepository.replaceCampaignItems(campaignId, toDiscountMap(items));
    }

    public void terminateCampaignEarly(int campaignId) {
        campaignRepository.terminateCampaignEarly(campaignId);
    }

    public void deleteCampaign(int campaignId) {
        campaignRepository.deleteCampaign(campaignId);
    }

    public void recordPromotionsView() {
        campaignRepository.incrementCampaignHits();
    }

    public void recordItemAdded(String productId, int qty) {
        campaignRepository.incrementAdded(productId, qty);
    }

    public void recordItemPurchased(String productId, int qty) {
        campaignRepository.incrementPurchased(productId, qty);
    }

    public List<Map<String, Object>> listCampaigns() {
        return campaignRepository.listCampaigns();
    }

    public List<Map<String, Object>> listPromotionItems() {
        return dedupePromotionItemsByName(campaignRepository.listPromotionItems());
    }

    /**
     * Hides legacy 8-digit sample rows when the same display name exists on a CA-style product id
     * within the same campaign (matches catalogue deduplication).
     */
    private static List<Map<String, Object>> dedupePromotionItemsByName(List<Map<String, Object>> rows) {
        Map<PromotionGroupKey, List<Map<String, Object>>> groups = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            int cid = ((Number) row.get("campaign_id")).intValue();
            String norm = LegacySampleProductIds.normalizeNameKey(String.valueOf(row.get("product_name")));
            groups.computeIfAbsent(new PromotionGroupKey(cid, norm), k -> new ArrayList<>()).add(row);
        }
        List<Map<String, Object>> out = new ArrayList<>(groups.size());
        for (List<Map<String, Object>> group : groups.values()) {
            out.add(pickPromotionRow(group));
        }
        out.sort(Comparator
                .comparingInt((Map<String, Object> m) -> ((Number) m.get("campaign_id")).intValue())
                .thenComparing(m -> String.valueOf(m.get("product_name")), String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    private static Map<String, Object> pickPromotionRow(List<Map<String, Object>> group) {
        if (group.size() == 1) {
            return group.get(0);
        }
        List<Map<String, Object>> nonLegacy = new ArrayList<>();
        for (Map<String, Object> row : group) {
            String pid = row.get("product_id") == null ? "" : String.valueOf(row.get("product_id"));
            if (!LegacySampleProductIds.isLegacyEightDigitSampleId(pid)) {
                nonLegacy.add(row);
            }
        }
        if (!nonLegacy.isEmpty()) {
            nonLegacy.sort(Comparator.comparing(r -> String.valueOf(r.get("product_id"))));
            return nonLegacy.get(0);
        }
        group.sort(Comparator.comparing(r -> String.valueOf(r.get("product_id"))));
        return group.get(0);
    }

    public boolean isProductInOverlappingCampaign(String productId, LocalDateTime start, LocalDateTime end) {
        return campaignRepository.isProductInOverlappingCampaign(productId, start, end);
    }

    public boolean isProductInOverlappingCampaign(String productId, LocalDateTime start, LocalDateTime end, Integer excludedCampaignId) {
        return campaignRepository.isProductInOverlappingCampaign(productId, start, end, excludedCampaignId);
    }

    public Optional<Map<String, Object>> findCampaign(int campaignId) {
        return campaignRepository.findCampaign(campaignId);
    }

    public String getCampaignItemSpec(int campaignId) {
        List<String> lines = new ArrayList<>();
        for (Map<String, Object> row : campaignRepository.listCampaignItems(campaignId)) {
            String productId = String.valueOf(row.get("product_id"));
            double discount = row.get("discount_percent") instanceof Number number ? number.doubleValue() : 0.0;
            if (Math.rint(discount) == discount) {
                lines.add(productId + ":" + String.format("%.0f", discount));
            } else {
                lines.add(productId + ":" + String.format("%.1f", discount));
            }
        }
        return String.join("\n", lines);
    }

    private List<ItemDiscount> parseItemSpec(String itemSpec) {
        List<ItemDiscount> result = new ArrayList<>();
        if (itemSpec == null || itemSpec.isBlank()) {
            throw new IllegalArgumentException("Please provide at least one campaign item");
        }
        String[] rows = itemSpec.split("\\n");
        for (String row : rows) {
            String trimmed = row.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            String[] parts = trimmed.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid item format. Use PRODUCT_ID:DISCOUNT, for example 10000001:10");
            }
            String productId = parts[0].trim();
            String discountText = parts[1].trim();
            if (discountText.endsWith("%")) {
                discountText = discountText.substring(0, discountText.length() - 1).trim();
            }
            double discount;
            try {
                discount = Double.parseDouble(discountText);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Discount must be a number, for example 10000001:10");
            }
            if (productId.isBlank()) {
                throw new IllegalArgumentException("Product ID is required for every campaign item");
            }
            if (discount <= 0 || discount >= 100) {
                throw new IllegalArgumentException("Discount must be between 0 and 100");
            }
            result.add(new ItemDiscount(productId, discount));
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Please provide at least one valid campaign item");
        }
        return result;
    }

    private Map<String, Double> toDiscountMap(List<ItemDiscount> items) {
        Map<String, Double> discounts = new LinkedHashMap<>();
        for (ItemDiscount item : items) {
            Double existing = discounts.putIfAbsent(item.productId, item.discountPercent);
            if (existing != null) {
                if (existing.doubleValue() == item.discountPercent) {
                    throw new IllegalArgumentException("Product " + item.productId + " is listed more than once");
                }
                throw new IllegalArgumentException(
                        "Product " + item.productId + " has conflicting discounts in the same draft. Keep one discount value only."
                );
            }
        }
        return discounts;
    }

    private static class ItemDiscount {
        private final String productId;
        private final double discountPercent;

        private ItemDiscount(String productId, double discountPercent) {
            this.productId = productId;
            this.discountPercent = discountPercent;
        }
    }
}
