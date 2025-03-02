//package com.shizzy.moneytransfer.service;
//
//import com.shizzy.moneytransfer.api.ApiResponse;
//import com.shizzy.moneytransfer.dto.CreateTransactionRequestBody;
//import com.shizzy.moneytransfer.dto.TransactionReceipt;
//import com.shizzy.moneytransfer.exception.ResourceNotFoundException;
//import com.shizzy.moneytransfer.exception.IllegalArgumentException;
//import com.shizzy.moneytransfer.exception.TransactionLimitException;
//import com.shizzy.moneytransfer.model.*;
//import com.shizzy.moneytransfer.repository.*;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class TransactionServiceTest {
//
//    @InjectMocks
//    private TransactionService transactionService;
//    private AutoCloseable autoCloseable;
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private AdminRepository adminRepository;
//
//    @Mock
//    private CountryRepository countryRepository;
//
//    @Mock
//    TransactionStatusRepository statusRepository;
//
//    private static final int TRANSACTION_FREQUENCY_PER_MONTH_TO_SAME_USER = 5;
//
//
//    @BeforeEach
//    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
//    }
//
//    @AfterEach
//    void tearDown() throws Exception {
//        autoCloseable.close();
//    }
//
//    @Test
//    public void canGetAllTransactionsWhenTransactionsExist() {
//        // Given
//        List<Transaction> transactions = new ArrayList<>();
//        transactions.add(new Transaction()); // Add some sample transactions
//
//        // Mock the behavior of transactionRepository.findAll() to return the list of transactions
//        when(transactionRepository.findAll()).thenReturn(transactions);
//
//        // When
//        ApiResponse<List<Transaction>> response = transactionService.getAllTransactions();
//
//        // Then
//        assertTrue(response.isSuccess());
//        assertEquals(transactions.size() + " transactions found", response.getMessage());
//        assertEquals(transactions, response.getData());
//    }
//
//    @Test
//    public void willThrowWhenNoTransactionsExist() {
//        // Given
//        List<Transaction> emptyTransaction = new ArrayList<>();
//
//        // Mock the behavior of transactionRepository.findAll() to return an empty list
//        when(transactionRepository.findAll()).thenReturn(emptyTransaction);
//
//        // When/Then
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            transactionService.getAllTransactions();
//        });
//        assertEquals("No transactions found", exception.getMessage());
//    }
//
//    @Test
//    public void testGetTransactionByIdWhenTransactionExists() {
//        // Given
//        Transaction transaction = new Transaction();
//        int transactionId = 123; // Example transaction ID
//
//        // Mock the behavior of transactionRepository.findById() to return the transaction
//        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
//
//        // When
//        ApiResponse<Transaction> response = transactionService.getTransactionById(transactionId);
//
//        // Then
//        assertTrue(response.isSuccess());
//        assertEquals("Transaction found", response.getMessage());
//        assertEquals(transaction, response.getData());
//    }
//
//    @Test
//    public void testGetTransactionByIdWhenTransactionDoesNotExist() {
//        // Given
//        int nonExistentId = 999; // Non-existent transaction ID
//
//        // Mock the behavior of transactionRepository.findById() to return an empty Optional
//        when(transactionRepository.findById(nonExistentId)).thenReturn(Optional.empty());
//
//        // When/Then
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            transactionService.getTransactionById(nonExistentId);
//        });
//        assertEquals("transaction not found", exception.getMessage());
//    }
//
//    @Test
//    public void willSendMoneyAndCreateSuccessfulTransaction() {
//
//        // Mock admin user
//        Admin admin = createAdmin();
//
//        User sender = createUser("frank@mail.com");
//
//        User receiver = createUser("jtark@mail.com");
//
//        Country origin = createCountry("JAPAN");
//
//        Country destination = createCountry("UNITED KINGDOM");
//
//        // Mock admin repository behavior
//        when(adminRepository.findAdminByUsername(admin.getUsername())).thenReturn(Optional.of(admin));
//
//        // Mock authentication
//        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(admin.getUsername()).password(admin.getPassword()).roles("ADMIN").build();
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
//        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//        securityContext.setAuthentication(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//
//        // Mock required dependencies
//        when(adminRepository.findAdminByUsername(admin.getUsername())).thenReturn(Optional.of(admin));
//        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.ofNullable(sender));
//        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.ofNullable(receiver));
//        when(countryRepository.getCountryByName(anyString())).thenReturn(Optional.of(origin));
//        when(countryRepository.getCountryByName(anyString())).thenReturn(Optional.of(destination));
//
//        // Mock transaction repository behavior
//        when(transactionRepository.save(any())).thenReturn(new Transaction());
//
//        // Create request body
//        CreateTransactionRequestBody requestBody = new CreateTransactionRequestBody(
//                sender,
//                receiver,
//                origin,
//                destination,
//                100.0
//        );
//
//        // Perform the transaction
//        ApiResponse<TransactionReceipt> response = transactionService.sendMoney(requestBody);
//
//        // Verify behavior
//        assertTrue(response.isSuccess());
//        assertEquals("Transaction saved", response.getMessage());
//        verify(transactionRepository, times(1)).save(any());
//    }
//
//    @Test
//    public void testWillFailWhenAdminIsNotPresent() {
//
//        User sender = createUser("frank@mail.com");
//
//        User receiver = createUser("jtark@mail.com");
//
//        Country origin = createCountry("JAPAN");
//
//        Country destination = createCountry("UNITED KINGDOM");
//
//        // Create request body
//        CreateTransactionRequestBody requestBody = new CreateTransactionRequestBody(
//                sender,
//                receiver,
//                origin,
//                destination,
//                100.0
//        );
//
//        // Mock admin user
//        Admin admin = createAdmin();
//
//        // Mock authentication
//        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(admin.getUsername()).password(admin.getPassword()).roles("ADMIN").build();
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
//        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//        securityContext.setAuthentication(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        // Mock admin repository behavior to return an empty Optional
//        when(adminRepository.findAdminByUsername("admin")).thenReturn(Optional.of(admin));
//
//        // Now, let's run the test by calling the method that should throw ResourceNotFoundException
//        assertThrows(ResourceNotFoundException.class, () -> {
//            transactionService.sendMoney(requestBody);
//        }, "No Admin Found");
//
//        // Optionally, you can verify other interactions or states as needed
//    }
//
//    @Test
//    public void testWillFailWhenAdminIsNull() {
//
//        User sender = createUser("frank@mail.com");
//
//        User receiver = createUser("jtark@mail.com");
//
//        Country origin = createCountry("JAPAN");
//
//        Country destination = createCountry("UNITED KINGDOM");
//
//        // Create request body
//        CreateTransactionRequestBody requestBody = new CreateTransactionRequestBody(
//                sender,
//                receiver,
//                origin,
//                destination,
//                100.0
//        );
//        // Mock admin repository behavior to return null
//        when(adminRepository.findAdminByUsername("admin")).thenReturn(null);
//
//        // Clear any existing authentication
//        SecurityContextHolder.clearContext();
//
//        // Now, let's run the test by calling the method that should throw ResourceNotFoundException
//        assertThrows(ResourceNotFoundException.class, () -> {
//            transactionService.sendMoney(requestBody);
//        }, "No Admin Found");
//
//    }
//
//    @Test
//    public void testWillFailWhenSenderIsEqualsReceiver() {
//
//        User sender = createUser("frank@mail.com");
//
//        User receiver = createUser("frank@mail.com");
//
//        Country origin = createCountry("JAPAN");
//
//        Country destination = createCountry("UNITED KINGDOM");
//
//        // Create request body
//        CreateTransactionRequestBody requestBody = new CreateTransactionRequestBody(
//                sender,
//                receiver,
//                origin,
//                destination,
//                100.0
//        );
//
//        // Now, let's run the test by calling the method that should throw IllegalArgumentException
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            transactionService.sendMoney(requestBody);
//        });
//
//        // Verify the exception message
//        String expectedMessage = "You cannot send money to yourself";
//        String actualMessage = exception.getMessage();
//        assert(actualMessage.contains(expectedMessage));
//    }
//
//    @Test
//    void willThrowTransactionLimitExceptionWhenMonthlyFrequencyLimitToSameUserHasBeenExceeded(){
//        User sender = createUser("frank@mail.com");
//
//        User receiver = createUser("james@mail.com");
//
//        LocalDate currentDate = LocalDate.now();
//        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
//        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
//
//        // Mock list of transactions between sender and receiver within a month
//        List<Transaction> transactions = new ArrayList<>();
//        for (int i = 0; i < TRANSACTION_FREQUENCY_PER_MONTH_TO_SAME_USER; i++) {
//            Transaction transaction = new Transaction();
//            transaction.setSender(sender);
//            transaction.setReceiver(receiver);
//            transactions.add(transaction);
//        }
//
//        // Mock transaction repository behavior to return the list of transactions
//        when(transactionRepository.findBySenderAndReceiverAndTransactionDateBetween(
//                sender, receiver, firstDayOfMonth,lastDayOfMonth))
//                .thenReturn(transactions);
//
//        // Now, let's run the test by calling the method that should throw IllegalStateException
//        TransactionLimitException exception = assertThrows(TransactionLimitException.class, () -> {
//            transactionService.checkSenderAndReceiverMonthlyLimit(sender, receiver, BigDecimal.ONE);
//        });
//
//        // Verify the exception message
//        String expectedMessage = "You have reached the monthly limit of transactions to the same user. "+ receiver.getFirstName();
//        String actualMessage = exception.getMessage();
//        assert(actualMessage.contains(expectedMessage));
//
//    }
//
//    @Test
//    void willThrowTransactionLimitExceptionWhenMonthlyLimitToSameUserHasBeenExceeded() {
//        User sender = createUser("frank@mail.com");
//
//        User receiver = createUser("james@mail.com");
//
//
//        LocalDate currentDate = LocalDate.now();
//        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
//        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
//
//        // Mock previous transactions between sender and receiver
//        List<Transaction> transactions = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            Transaction transaction = new Transaction();
//            transaction.setSender(sender);
//            transaction.setReceiver(receiver);
//            transaction.setAmount(BigDecimal.valueOf(1000));
//            transactions.add(transaction);
//        }
//        // Mock transaction repository behavior
//        when(transactionRepository.findBySenderAndReceiverAndTransactionDateBetween(
//                sender, receiver, firstDayOfMonth,
//               lastDayOfMonth))
//                .thenReturn(transactions);
//
//        // Now, let's run the test by calling the method that should throw TransactionLimitException
//        TransactionLimitException exception = assertThrows(TransactionLimitException.class, () -> {
//            transactionService.checkSenderAndReceiverMonthlyLimit(sender, receiver, BigDecimal.valueOf(1000));
//        });
//
//        // Verify the exception message
//        String expectedMessage = "You have exceeded the monthly transaction limit to this recipient " + receiver.getFirstName();
//        String actualMessage = exception.getMessage();
//        assert(actualMessage.contains(expectedMessage));
//
//    }
//
//    @Test
//    public void testWillThrowTransactionLimitExceptionWhenReceiverExceedsMonthlyTransactionLimit() {
//        User sender = createUser("frank@mail.com");
//
//        User receiver = createUser("james@mail.com");
//
//
//        LocalDate currentDate = LocalDate.now();
//        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
//        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
//
//        // Mock previous transactions between sender and receiver
//        List<Transaction> transactions = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            Transaction transaction = new Transaction();
//            transaction.setSender(sender);
//            transaction.setReceiver(receiver);
//            transaction.setAmount(BigDecimal.valueOf(3000));
//            transactions.add(transaction);
//        }
//        // Mock transaction repository behavior
//        when(transactionRepository.findByReceiverAndTransactionDateBetween(
//                receiver, firstDayOfMonth,
//                lastDayOfMonth))
//                .thenReturn(transactions);
//
//        // Now, let's run the test by calling the method that should throw TransactionLimitException
//        TransactionLimitException exception = assertThrows(TransactionLimitException.class, () -> {
//            transactionService.checkSenderAndReceiverMonthlyLimit(sender, receiver, BigDecimal.valueOf(3000));
//        });
//
//        // Verify the exception message
//        String expectedMessage = "Recipient can no longer receive funds this month";
//        String actualMessage = exception.getMessage();
//        assert(actualMessage.contains(expectedMessage));
//
//    }
//
//
//    @Test
//    void sendMoney() {
//    }
//
//    @Test
//    void searchTransactions() {
//    }
//
//    @Test
//    void trackMoneyWithMtcn() {
//    }
//
//    @Test
//    void receiveMoney() {
//    }
//
//    @Test
//    void updateTransaction() {
//    }
//
//    @Test
//    void getTransactionsBySender() {
//    }
//
//    @Test
//    void getTransactionsByReceiver() {
//    }
//
//    @Test
//    void getUserTransactionsByDate() {
//    }
//
//    @Test
//    void findAllTransactionsByUserId() {
//    }
//
//    @Test
//    void checkSenderAndReceiverMonthlyLimit() {
//    }
//
//    private Transaction createTransaction(User receiver, BigDecimal amount) {
//        return Transaction.builder()
//                .receiver(receiver)
//                .amount(amount)
//                .build();
//    }
//
//    private Admin createAdmin(){
//        return Admin.builder()
//                .username("admin")
//                .password("password")
//                .build();
//    }
//    private User createUser(String email){
//        return User.builder()
//                .firstName("Frank")
//                .lastName("James")
//                .email(email)
//                .build();
//    }
//
//    private Country createCountry(String name) {
//        return Country.builder()
//                .rating(5)
//                .name(name)
//                .build();
//    }
//}