package com.google.moviestvsentiments.service.account;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.service.web.ApiResponse;
import com.google.moviestvsentiments.service.web.Resource;
import com.google.moviestvsentiments.service.web.WebService;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import com.google.moviestvsentiments.util.MainThreadDatabaseExecutor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import retrofit2.Response;

@RunWith(JUnit4.class)
public class AccountRepositoryTest {

    private static final Account ACCOUNT = createAccount("Account Name", Instant.ofEpochSecond(1337), true);

    private static Account createAccount(String accountName, Instant timestamp, boolean isCurrent) {
        Account account = new Account();
        account.name = accountName;
        account.timestamp = timestamp;
        account.isCurrent = isCurrent;
        return account;
    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AccountDao dao;
    private AccountRepository repository;
    private WebService webService;

    @Before
    public void setUp() {
        dao = mock(AccountDao.class);
        webService = mock(WebService.class);
        repository = AccountRepository.create(dao, new MainThreadDatabaseExecutor(), webService);
    }

    @Test
    public void addAccount_invokesDao() {
        repository.addAccount("Account Name");

        verify(dao).addAccount("Account Name");
    }

    @Test
    public void getAlphabetizedAccounts_webServiceFailure_returnsLocalAccounts() {
        MutableLiveData<List<Account>> localData = new MutableLiveData<>(Arrays.asList(ACCOUNT));
        MutableLiveData<ApiResponse<List<Account>>> remoteData = new MutableLiveData<>(
                new ApiResponse(new RuntimeException("Network error")));
        when(dao.getAlphabetizedAccounts()).thenReturn(localData);
        when(webService.getAlphabetizedAccounts()).thenReturn(remoteData);

        Resource<List<Account>> results = LiveDataTestUtil.getValue(repository.getAlphabetizedAccounts());

        assertThat(results.getStatus()).isEqualTo(Resource.Status.ERROR);
        assertThat(results.getValue()).containsExactly(ACCOUNT);
    }

    @Test
    public void getAlphabetizedAccounts_webServiceSuccess_addsRemoteAccountsToDatabase() {
        MutableLiveData<List<Account>> localData = new MutableLiveData<>(Arrays.asList(ACCOUNT));
        List<Account> remoteAccounts = Arrays.asList(createAccount("Remote Account",
                Instant.ofEpochSecond(17), false));
        MutableLiveData<ApiResponse<List<Account>>> remoteData = new MutableLiveData<>(
                new ApiResponse(Response.success(remoteAccounts)));
        when(dao.getAlphabetizedAccounts()).thenReturn(localData);
        when(webService.getAlphabetizedAccounts()).thenReturn(remoteData);

        Resource<List<Account>> results = LiveDataTestUtil.getValue(repository.getAlphabetizedAccounts());

        assertThat(results.getStatus()).isEqualTo(Resource.Status.SUCCESS);
        verify(dao, times(1)).addAccounts(remoteAccounts);
    }

    @Test
    public void getCurrentAccount_returnsAccount() {
        MutableLiveData<Account> accountData = new MutableLiveData<>();
        accountData.setValue(ACCOUNT);
        when(dao.getCurrentAccount()).thenReturn(accountData);

        Account result = LiveDataTestUtil.getValue(repository.getCurrentAccount());

        assertThat(result).isEqualTo(ACCOUNT);
    }

    @Test
    public void setIsCurrent_invokesDao() {
        repository.setIsCurrent("Account Name", false);

        verify(dao).setIsCurrent("Account Name", false);
    }
}
