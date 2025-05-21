package com.cintie.marketplace_backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cintie.marketplace_backend.entities.BidEntity;
import com.cintie.marketplace_backend.entities.ProductEntity;
import com.cintie.marketplace_backend.entities.UserEntity;
import com.cintie.marketplace_backend.exceptions.BidException;
import com.cintie.marketplace_backend.exceptions.ProductNotFoundException;
import com.cintie.marketplace_backend.repositories.BidRepository;
import com.cintie.marketplace_backend.repositories.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final ProductRepository productRepository;

    @Transactional
    public BidEntity placeBid(String productId, double amount, UserEntity bidder) throws Exception{
        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        
        if(!product.isAuction()){
            throw new BidException("This product is not available for bidding");
        }
        if (product.getStatus() != ProductEntity.ProductStatus.ACTIVE) {
            throw new BidException("This product is not active for bidding");
        }

        if (LocalDateTime.now().isAfter(product.getAuctionEndTime())) {
            throw new BidException("Auction has ended");
        }

        if (amount <= product.getCurrentPrice()) {
            throw new BidException("Bid amount must be higher than current price");
        }

        if (product.getUser().getId().equals(bidder.getId())) {
            throw new BidException("You cannot bid on your own product");
        }

        BidEntity bid = BidEntity.builder()
                                .amount(amount)
                                .bidder(bidder)
                                .product(product)
                                .createdAt(LocalDateTime.now())
                                .build();
        
        product.setCurrentPrice(amount);
        productRepository.save(product);

        return bidRepository.save(bid);
    }

    public List<BidEntity> getBidsForProduct(String productId) throws Exception{
        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        
        return bidRepository.findByProductOrderByAmountDesc(product);
    }

    public List<BidEntity> getBidsByUser(UserEntity user){
        return bidRepository.findByBidder(user);
    }

    @Transactional
    public void cancelBid(String bidId, UserEntity user) throws Exception {
        BidEntity bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new BidException("Bid not found"));

        if (!bid.getBidder().getId().equals(user.getId())) {
            throw new BidException("You can only cancel your own bids");
        }

        ProductEntity product = bid.getProduct();
        if (product.getStatus() != ProductEntity.ProductStatus.ACTIVE) {
            throw new BidException("Cannot cancel bid on inactive product");
        }

        bidRepository.delete(bid);

        List<BidEntity> remainingBids = bidRepository.findByProductOrderByAmountDesc(product);
        if (remainingBids.isEmpty()) {
            product.setCurrentPrice(product.getStartPrice());
        } else {
            product.setCurrentPrice(remainingBids.get(0).getAmount());
        }
        productRepository.save(product);
    }
}
