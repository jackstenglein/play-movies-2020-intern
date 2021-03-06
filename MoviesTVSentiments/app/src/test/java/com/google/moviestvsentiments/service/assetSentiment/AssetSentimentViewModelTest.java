package com.google.moviestvsentiments.service.assetSentiment;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.web.Resource;
import com.google.moviestvsentiments.util.AssetUtil;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class AssetSentimentViewModelTest {

    private static final Asset ASSET = AssetUtil.createShowAsset("assetId");

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AssetSentimentRepository repository;
    private AssetSentimentViewModel viewModel;

    @Before
    public void setUp() {
        repository = mock(AssetSentimentRepository.class);
        viewModel = AssetSentimentViewModel.create(repository);
    }

    @Test
    public void addAsset_invokesRepository() {
        viewModel.addAsset(ASSET);

        verify(repository).addAsset(ASSET);
    }

    @Test
    public void getAsset_returnsAsset() {
        MutableLiveData<AssetSentiment> assetData = new MutableLiveData<>();
        assetData.setValue(AssetSentiment.create(ASSET, SentimentType.THUMBS_DOWN));
        when(repository.getAsset("Account Name", "assetId", AssetType.SHOW))
                .thenReturn(assetData);

        AssetSentiment result = LiveDataTestUtil.getValue(viewModel.getAsset("Account Name",
                "assetId", AssetType.SHOW));

        assertThat(result).isEqualTo(AssetSentiment.create(ASSET, SentimentType.THUMBS_DOWN));
    }

    @Test
    public void getAssets_returnsAssets() {
        MutableLiveData<Resource<List<AssetSentiment>>> assetData = new MutableLiveData<>();
        assetData.setValue(Resource.success(Arrays.asList(
                AssetSentiment.create(ASSET, SentimentType.UNSPECIFIED))));
        when(repository.getAssets(AssetType.SHOW, "Account Name", SentimentType.UNSPECIFIED))
                .thenReturn(assetData);

        Resource<List<AssetSentiment>> result = LiveDataTestUtil.getValue(viewModel.getAssets(
                AssetType.SHOW, "Account Name", SentimentType.UNSPECIFIED));

        assertThat(result.getStatus()).isEqualTo(Resource.Status.SUCCESS);
        assertThat(result.getValue()).containsExactly(AssetSentiment.create(ASSET,
                SentimentType.UNSPECIFIED));
    }

    @Test
    public void updateSentiment_invokesRepository() {
        viewModel.updateSentiment("Account Name", "assetId", AssetType.SHOW,
                SentimentType.THUMBS_UP);

        verify(repository).updateSentiment("Account Name", "assetId", AssetType.SHOW,
                SentimentType.THUMBS_UP);
    }

    @Test
    public void deleteAllSentiments_invokesRepository() {
        viewModel.deleteAllSentiments("Account Name");

        verify(repository).deleteAllSentiments("Account Name");
    }
}
