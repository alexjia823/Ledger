package com.hsbc.ledger;

import com.hsbc.ledger.dto.AccountEvent;
import com.hsbc.ledger.dto.AssetEvent;
import com.hsbc.ledger.dto.WalletEvent;
import com.hsbc.ledger.entities.Account;
import com.hsbc.ledger.entities.Asset;
import com.hsbc.ledger.entities.Posting;
import com.hsbc.ledger.entities.Wallet;
import com.hsbc.ledger.repository.AccountRepository;
import com.hsbc.ledger.repository.AssetRepository;
import com.hsbc.ledger.repository.PostingRepository;
import com.hsbc.ledger.repository.WalletRepository;

import com.hsbc.ledger.utils.AccountState;
import com.hsbc.ledger.utils.PostingState;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.ServletException;

import org.junit.Test;
import org.junit.Before;

import org.springframework.beans.factory.annotation.Autowired;
import org.junit.runner.RunWith;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.hsbc.ledger.utils.AccountState.OPEN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LedgerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private AssetRepository assetRepo;

	@Autowired
	private PostingRepository postingRepo;

	@Autowired
	private WalletRepository walletRepo;

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@PersistenceContext
	private EntityManager entityManager;

	@Before
	public void setUp() throws InterruptedException {
		// 在每次执行测试方法之前执行的代码
		System.out.println("Before each test method");
        Account account = new Account((long)1, "account1", AccountState.OPEN, "Desc", 1);
		Account newAccountRtn = accountRepo.save(account);
		AccountEvent eventNewAccountRtn = new AccountEvent("CreateAccount", newAccountRtn);
		kafkaTemplate.send("Account-event-topic", eventNewAccountRtn);

		Wallet wallet1 = new Wallet(1, "wallet1", 500.0, 1);
		Wallet fromWalletRtn = walletRepo.save(wallet1);
		WalletEvent eventFromWalletRtn = new WalletEvent("CreateWallet", fromWalletRtn);
		kafkaTemplate.send("Wallet-event-topic", eventFromWalletRtn);

		Wallet wallet2 = new Wallet(2, "wallet2", 300.0, 1);
		Wallet toWalletRtn = walletRepo.save(wallet2);
		WalletEvent eventToWalletRtn = new WalletEvent("CreateWallet", toWalletRtn);
		kafkaTemplate.send("Wallet-event-topic", eventToWalletRtn);

		Asset asset1 = new Asset((long)1, "stock", 90.0, (long)1);
		Asset fromAssetRtn = assetRepo.save(asset1);
		AssetEvent eventFromAssetRtn = new AssetEvent("CreateAsset", fromAssetRtn);
		kafkaTemplate.send("Asset-event-topic", eventFromAssetRtn);

		Asset asset2 = new Asset((long)2, "stock", 30.0, (long)1);
		Asset toAssetRtn = assetRepo.save(asset2);
		AssetEvent eventToAssetRtn = new AssetEvent("CreateAsset", toAssetRtn);
		kafkaTemplate.send("Asset-event-topic", eventToAssetRtn);

		Thread.sleep(1000);
	}


	@Test
	public void testSingleTransferFromWalletBalance() throws Exception {

		String requestBody = "{\"accountID\": 1, \"fromWalletID\": 1, \"toWalletID\": 2, \"fromAssetID\": 1, " +
				"\"toAssetID\": 2, \"amount\": 1}";

		Wallet fromOriginWallet = walletRepo.findById((long)1).get();

		mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				// 验证响应状态码是否为 200
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 验证响应内容是否为 "User created successfully"
				.andExpect(MockMvcResultMatchers.content().string("Transaction successfully"));

		Wallet fromNewWallet = walletRepo.findById((long)1).get();

		assertEquals(fromOriginWallet.getBalance() - 1, fromNewWallet.getBalance());

	}

	@Test
	public void testSingleTransferToWalletBalance() throws Exception {

		String requestBody = "{\"accountID\": 1, \"fromWalletID\": 1, \"toWalletID\": 2, \"fromAssetID\": 1, " +
				"\"toAssetID\": 2, \"amount\": 1}";

		Wallet toOriginWallet = walletRepo.findById((long)2).get();

		mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				// 验证响应状态码是否为 200
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 验证响应内容是否为 "User created successfully"
				.andExpect(MockMvcResultMatchers.content().string("Transaction successfully"));

		Wallet toNewWallet = walletRepo.findById((long)2).get();

		assertEquals(toOriginWallet.getBalance() + 1, toNewWallet.getBalance());

	}

	@Test
	public void testSingleTransferFromAssetBalance() throws Exception {

		String requestBody = "{\"accountID\": 1, \"fromWalletID\": 1, \"toWalletID\": 2, \"fromAssetID\": 1, " +
				"\"toAssetID\": 2, \"amount\": 1}";

		Asset fromOriginalAsset = assetRepo.findById((long)1).get();

		mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				// 验证响应状态码是否为 200
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 验证响应内容是否为 "User created successfully"
				.andExpect(MockMvcResultMatchers.content().string("Transaction successfully"));

		Asset fromNewAsset = assetRepo.findById((long)1).get();

		assertEquals(fromOriginalAsset.getBalance() - 1, fromNewAsset.getBalance());

	}

	@Test
	public void testSingleTransferToAssetBalance() throws Exception {

		String requestBody = "{\"accountID\": 1, \"fromWalletID\": 1, \"toWalletID\": 2, \"fromAssetID\": 1, " +
				"\"toAssetID\": 2, \"amount\": 1}";

		Asset toOriginAsset = assetRepo.findById((long)2).get();

		mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				// 验证响应状态码是否为 200
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 验证响应内容是否为 "User created successfully"
				.andExpect(MockMvcResultMatchers.content().string("Transaction successfully"));

		Asset toNewAsset = assetRepo.findById((long)2).get();

		assertEquals(toOriginAsset.getBalance() + 1, toNewAsset.getBalance());
	}

	@Test
	public void testChangeAccountState() throws Exception {

		String requestBody = "\"OPEN\"";
		mockMvc.perform(MockMvcRequestBuilders.patch("/accounts/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				// 验证响应状态码是否为 200
				.andExpect(MockMvcResultMatchers.status().isOk());

		Account account = accountRepo.findById((long)1).get();
		assertEquals(OPEN , account.getState());
	}

	@Test
	public void testSingleTransferAccountIsClosed() throws Exception {

		String requestBody = "{\"accountID\": 1, \"fromWalletID\": 1, \"toWalletID\": 2, \"fromAssetID\": 1, " +
				"\"toAssetID\": 2, \"amount\": 1}";

		Account existingAccount = accountRepo.findById((long)1).get();
		existingAccount.setState(AccountState.CLOSED);
		Account accountDO = accountRepo.save(existingAccount);
		AccountEvent event = new AccountEvent("UpdateAccount", accountDO);
		kafkaTemplate.send("Account-event-topic", event);
        Thread.sleep(800);

		mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().string("The account state is closed"));

		existingAccount.setState(OPEN);
		accountDO = accountRepo.save(existingAccount);
		event = new AccountEvent("UpdateAccount", accountDO);
		kafkaTemplate.send("Account-event-topic", event);
		Thread.sleep(800);

	}

	@Test
	public void testSingleTransferFinishedPostingStateShouldBeCleared() throws Exception {

		String requestBody = "{\"accountID\": 1, \"fromWalletID\": 1, \"toWalletID\": 2, \"fromAssetID\": 1, " +
				"\"toAssetID\": 2, \"amount\": 1}";

		mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Transaction successfully"));

		Query query = entityManager.createQuery("SELECT e FROM Posting e WHERE e.id = (SELECT MAX(e.id) FROM Posting e)");
		Posting posting = (Posting)query.getSingleResult();
		assertEquals(PostingState.CLEARED, posting.getState());
	}

	@Test
	public void testSingleTransferBalanceInsufficient() throws Exception {

		String requestBody = "{\"accountID\": 1, \"fromWalletID\": 1, \"toWalletID\": 2, \"fromAssetID\": 1, " +
				"\"toAssetID\": 2, \"amount\": 1000000}";

		Asset fromOriginalAsset = assetRepo.findById((long)1).get();
		Asset toOriginAsset = assetRepo.findById((long)2).get();
		Wallet fromOriginWallet = walletRepo.findById((long)1).get();
		Wallet toOriginWallet = walletRepo.findById((long)2).get();

		ServletException exception = assertThrows(ServletException.class, () -> {
			mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody));
		});

		Asset fromNewAsset = assetRepo.findById((long)1).get();
		Asset toNewAsset = assetRepo.findById((long)2).get();
		Wallet fromNewWallet = walletRepo.findById((long)1).get();
		Wallet toNewWallet = walletRepo.findById((long)2).get();

		assertEquals(toOriginAsset.getBalance(), toNewAsset.getBalance());
		assertEquals(fromOriginalAsset.getBalance(), fromNewAsset.getBalance());
		assertEquals(fromOriginWallet.getBalance(), fromNewWallet.getBalance());
		assertEquals(toOriginWallet.getBalance(), toNewWallet.getBalance());
	}

	@Test
	public void testBatchTransferSuccess() throws Exception {

		String requestBody = "[{\"accountID\": 1, \"fromWalletID\": 1, \"toWalletID\": 2, \"fromAssetID\": 1, " +
				"\"toAssetID\": 2, \"amount\": 1}, " +
				"{\"accountID\": 1, \"fromWalletID\": 1, \"toWalletID\": 2, \"fromAssetID\": 1, " +
				"\"toAssetID\": 2, \"amount\": 2}]";

		Asset fromOriginalAsset = assetRepo.findById((long)1).get();
		Asset toOriginAsset = assetRepo.findById((long)2).get();
		Wallet fromOriginWallet = walletRepo.findById((long)1).get();
		Wallet toOriginWallet = walletRepo.findById((long)2).get();

		mockMvc.perform(MockMvcRequestBuilders.post("/accounts/batch")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				// 验证响应状态码是否为 200
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 验证响应内容是否为 "User created successfully"
				.andExpect(MockMvcResultMatchers.content().string("Transaction successfully"));

		Asset fromNewAsset = assetRepo.findById((long)1).get();
		Asset toNewAsset = assetRepo.findById((long)2).get();
		Wallet fromNewWallet = walletRepo.findById((long)1).get();
		Wallet toNewWallet = walletRepo.findById((long)2).get();

		assertEquals(toOriginAsset.getBalance() + 3, toNewAsset.getBalance());
		assertEquals(fromOriginalAsset.getBalance() - 3, fromNewAsset.getBalance());
		assertEquals(fromOriginWallet.getBalance() - 3, fromNewWallet.getBalance());
		assertEquals(toOriginWallet.getBalance() + 3, toNewWallet.getBalance());
	}

	@Test
	public void testBatchTransferBalanceNotSufficient() throws Exception {

		String requestBody = "[{\"accountID\": 1, \"fromWalletID\": 1, \"toWalletID\": 2, \"fromAssetID\": 1, " +
				"\"toAssetID\": 2, \"amount\": 1}, " +
				"{\"accountID\": 1, \"fromWalletID\": 1, \"toWalletID\": 2, \"fromAssetID\": 1, " +
				"\"toAssetID\": 2, \"amount\": 5000000}]";

		Asset fromOriginalAsset = assetRepo.findById((long)1).get();
		Asset toOriginAsset = assetRepo.findById((long)2).get();
		Wallet fromOriginWallet = walletRepo.findById((long)1).get();
		Wallet toOriginWallet = walletRepo.findById((long)2).get();

		ServletException exception = assertThrows(ServletException.class, () -> {
			mockMvc.perform(MockMvcRequestBuilders.post("/accounts/batch")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody));
		});

		Asset fromNewAsset = assetRepo.findById((long)1).get();
		Asset toNewAsset = assetRepo.findById((long)2).get();
		Wallet fromNewWallet = walletRepo.findById((long)1).get();
		Wallet toNewWallet = walletRepo.findById((long)2).get();

		assertEquals(toOriginAsset.getBalance(), toNewAsset.getBalance());
		assertEquals(fromOriginalAsset.getBalance(), fromNewAsset.getBalance());
		assertEquals(fromOriginWallet.getBalance(), fromNewWallet.getBalance());
		assertEquals(toOriginWallet.getBalance(), toNewWallet.getBalance());
	}

}

