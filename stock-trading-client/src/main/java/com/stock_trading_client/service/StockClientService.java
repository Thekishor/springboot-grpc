package com.stock_trading_client.service;

import com.stockTrading.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {

    @GrpcClient("stock_client")
    //this is for unary operation which block the calls
    //private StockTradingServiceGrpc.StockTradingServiceBlockingStub stockTradingServiceStub;

    //this is for server streaming operation which is non-block in nature or calls
    private StockTradingServiceGrpc.StockTradingServiceStub stockTradingServiceStub;

    /*//unary operations
    public StockResponse getStockPrice(String stockSymbol) {
        StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
        return stockTradingServiceStub.getStockPrice(request);
    }*/

    public void subscribeStockPrice(String stockSymbol) {
        StockRequest stockRequest = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
        stockTradingServiceStub.subscribeStockPrice(stockRequest, new StreamObserver<StockResponse>() {
            @Override
            public void onNext(StockResponse stockResponse) {
                System.out.println("Stock Price Update: " + stockResponse.getStockSymbol() + " " +
                        "Price: " + stockResponse.getPrice() + " " +
                        "Time: " + stockResponse.getTimestamp());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stock price stream live update completed");
            }
        });
    }

    public void placeBulkOrders() {
        StreamObserver<OrderSummary> streamObserver = new StreamObserver<OrderSummary>() {
            @Override
            public void onNext(OrderSummary orderSummary) {
                System.out.println("Order Summary Received from Server:");
                System.out.println("Total Orders: " + orderSummary.getTotalOrders());
                System.out.println("Successful Orders: " + orderSummary.getSuccessCount());
                System.out.println("Total Amount: " + orderSummary.getTotalAmount());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Order Summary Received Error from Server: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed, server is done sending summary !");
            }
        };
        StreamObserver<StockOrder> orderStreamObserver = stockTradingServiceStub.bulkStockOrder(streamObserver);
        try {
            orderStreamObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("1")
                    .setStockSymbol("AAPL")
                    .setOrderType("BUY")
                    .setPrice(199.23)
                    .setQuantity(10)
                    .build());

            orderStreamObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("2")
                    .setStockSymbol("GOOGL")
                    .setOrderType("BUY")
                    .setPrice(2750.45)
                    .setQuantity(10)
                    .build());

            orderStreamObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("10")
                    .setStockSymbol("TATA")
                    .setOrderType("BUY")
                    .setPrice(680)
                    .setQuantity(10)
                    .build());
            orderStreamObserver.onCompleted();
        } catch (Exception exception) {
            orderStreamObserver.onError(exception);
        }
    }
}
