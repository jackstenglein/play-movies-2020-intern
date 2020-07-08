package com.google.moviestvsentiments.assetSentiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * A controller that handles requests related to Assets and UserSentiments.
 */
@RestController
public class AssetSentimentController {

    @Autowired
    private AssetSentimentRepository assetSentimentRepository;

    /**
     * Returns a list of AssetSentiments that match the given AssetType, account name and SentimentType.
     * @param assetType The type of Asset to match.
     * @param accountName The name of the account to use when checking for user sentiments.
     * @param sentimentType The type of user sentiment to match.
     * @return A list of matching AssetSentiments.
     */
    @GetMapping("/assets")
    public List<AssetSentiment> getAssets(@RequestParam("assetType") AssetType assetType,
                                          @RequestParam("accountName") String accountName,
                                          @RequestParam("sentimentType") SentimentType sentimentType) {
        List<AssetSentiment> withReaction = assetSentimentRepository.getAssetsWithSentiment(assetType, accountName,
                sentimentType);
        if (sentimentType != SentimentType.UNSPECIFIED) {
            return withReaction;
        }

        List<AssetSentiment> withoutReaction = assetSentimentRepository.getAssetsWithoutSentiment(assetType, accountName);
        withReaction.addAll(withoutReaction);
        return withReaction;
    }
}
