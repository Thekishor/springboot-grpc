package com.stock_trading_client;

import com.stock_trading_client.service.StockClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockTradingClientApplication implements CommandLineRunner {

	private final StockClientService stockClientService;

    public StockTradingClientApplication(StockClientService stockClientService) {
        this.stockClientService = stockClientService;
    }

    public static void main(String[] args) {
		SpringApplication.run(StockTradingClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//stockClientService.subscribeStockPrice("GOOGL");
		stockClientService.placeBulkOrders();
	}

	/*@Override
	public void run(String... args) throws Exception {
		System.out.println("Grpc client response : " + stockClientService.getStockPrice("GOOGL"));
	}*/
}
